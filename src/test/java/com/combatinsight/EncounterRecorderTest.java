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
