package com.combatinsight;
import org.junit.Test;
import net.runelite.api.NPC;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.*;
public class EncounterRecorderTest
{
 @Test public void archiveRoomChangesAreNotLimitedToOcellus(){
  EncounterRecorder r=new EncounterRecorder();r.setEnabled(true);
  r.snapshot(1,16640,"Bork state pending","animation=14538");
  r.snapshot(2,16641,"Smorks","health=71/101");
  r.snapshot(3,16662,"Guardian","animation=-1");
  r.snapshot(4,16684,"Xarth","animation=-1");
  r.snapshot(5,999,"Unrelated NPC","animation=-1");
  r.setEnabled(false);
  assertTrue(r.report().contains("Bork state pending"));
  assertTrue(r.report().contains("Smorks"));
  assertTrue(r.report().contains("Guardian"));
  assertTrue(r.report().contains("Xarth"));
  assertFalse(r.report().contains("Unrelated NPC"));
 }
 @Test public void optInAndActorChangesAndFreezeIndependentHistory(){
  EncounterRecorder r=new EncounterRecorder();r.snapshot(1,16674,"P1","idle");assertFalse(r.report().contains("P1"));
  r.setEnabled(true);r.snapshot(2,16674,"P1","idle");r.snapshot(3,16676,"Blade","walk");r.snapshot(4,16677,"P3/P4","curse");
  r.setEnabled(false);assertTrue(r.report().contains("P1"));assertTrue(r.report().contains("Blade"));assertTrue(r.report().contains("P3/P4"));
  r.setEnabled(true);assertFalse(r.report().contains("Blade"));
 }
 @Test public void boundedAndDeduplicated(){
  EncounterRecorder r=new EncounterRecorder();r.setEnabled(true);r.snapshot(1,16674,"same","cue");r.snapshot(2,16674,"same","cue");
  assertFalse(r.report().contains("Tick 2"));
  for(int i=0;i<12100;i++)r.event(i,16677,"hit "+i);
  assertTrue(r.report().contains("Dropped entries: 101"));assertTrue(r.report().contains("hit 12099"));
 }
 @Test public void fullRaidRetainsRoomSummariesAfterTimelineOverflow(){
  EncounterRecorder r=new EncounterRecorder();r.setEnabled(true);
  r.snapshot(1,16660,"Guardian first room","grounded");
  r.snapshot(2,16640,"Bork second room","standing");
  r.snapshot(3,16665,"Moia third room","active");
  r.snapshot(4,16684,"Xarth fourth room","active");
  for(int i=0;i<12100;i++)r.event(10+i,16677,"hit "+i);
  r.snapshot(13000,16677,"Ocellus final room","final");r.setEnabled(false);
  String report=r.report();
  assertTrue(report.contains("Guardian first room"));assertTrue(report.contains("Bork second room"));
  assertTrue(report.contains("Moia third room"));assertTrue(report.contains("Xarth fourth room"));
  assertTrue(report.contains("Ocellus final room"));assertTrue(report.contains("Dropped entries:"));
 }
 @Test public void roomSignalsAreRecordedWithoutASelectedTargetAndOnlyWhenChanged(){
  AtomicInteger animation=new AtomicInteger(14538),health=new AtomicInteger(-1);
  NPC bork=npc(16640,animation,new AtomicInteger(-1));
  NPC smorks=npc(16641,new AtomicInteger(-1),health);
  EncounterRecorder r=new EncounterRecorder();
  r.observeRoom(1,Arrays.asList(bork,smorks));
  assertFalse(r.report().contains("14538"));
  r.setEnabled(true);r.observeRoom(2,Arrays.asList(bork,smorks));
  r.observeRoom(3,Arrays.asList(bork,smorks));
  animation.set(14546);health.set(71);r.observeRoom(4,Arrays.asList(bork,smorks));
  r.setEnabled(false);r.observeRoom(5,Arrays.asList(bork,smorks));
  String report=r.report();
  assertTrue(report.contains("NPC 16640"));assertTrue(report.contains("animation=14538"));
  assertTrue(report.contains("animation=14546"));assertTrue(report.contains("health ratio/scale=71/101"));
  assertFalse(report.contains("Tick 1 "));assertFalse(report.contains("Tick 3 "));assertFalse(report.contains("Tick 5 "));
 }
 @Test public void roomWithoutSelectedTargetSurvivesOverflowAndMarksTickGaps(){
  EncounterRecorder r=new EncounterRecorder();r.setEnabled(true);
  r.observeRoom(1,Arrays.asList(npc(16660,new AtomicInteger(42),new AtomicInteger(-1))));
  for(int i=0;i<12100;i++)r.event(2+i,16677,"hit "+i);
  r.observeRoom(12102,Arrays.asList());
  String report=r.report();
  assertTrue(report.contains("1 Guardian\nFirst: Tick 1 NPC 16660"));
  assertTrue(report.contains("Room observation gap after tick 1"));
 }
 @Test public void returningToGuardianRetainsSeparateRoomSummaries(){
  EncounterRecorder r=new EncounterRecorder();r.setEnabled(true);
  NPC guardian=npc(16660,new AtomicInteger(-1),new AtomicInteger(-1));
  NPC bork=npc(16640,new AtomicInteger(-1),new AtomicInteger(-1));
  // Replay the supplied recording's partial first raid, then Guardian on raid two.
  r.observeRoom(15359,Arrays.asList(bork));r.snapshot(15360,16640,"first raid Bork","standing");
  r.observeRoom(15988,Arrays.asList(npc(16674,new AtomicInteger(-1),new AtomicInteger(-1))));
  r.observeRoom(17238,Arrays.asList(guardian));
  r.observeRoom(17239,Arrays.asList(guardian));
  r.observeRoom(17424,Arrays.asList(bork));r.snapshot(17425,16640,"second raid Bork","standing");
  for(int i=0;i<12100;i++)r.event(18000+i,16677,"hit "+i);
  String report=r.report();
  assertTrue(report.contains("Observed sequence 1 / 2 Bork or Ezekiel"));
  assertTrue(report.contains("Observed sequence 2 / 2 Bork or Ezekiel"));
  assertFalse(report.contains("Observed sequence 3"));
  assertTrue(report.contains("first raid Bork"));assertTrue(report.contains("second raid Bork"));
 }
 private static NPC npc(int id,AtomicInteger animation,AtomicInteger health){
  return (NPC)Proxy.newProxyInstance(NPC.class.getClassLoader(),new Class<?>[]{NPC.class},(p,m,a)->{
   switch(m.getName()){
    case "getId":return id;
    case "getAnimation":return animation.get();
    case "getPoseAnimation":return -1;
    case "getHealthRatio":return health.get();
    case "getHealthScale":return 101;
    default:return m.getReturnType()==int.class?0:m.getReturnType()==boolean.class?false:null;
   }
  });
 }
}
