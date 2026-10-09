package com.combatinsight.live;
import org.junit.Test;
import static org.junit.Assert.*;
import com.combatinsight.calculation.TargetProfile;
public class EncounterResolverTest {
 @Test public void resolvesAllNormalAndAwakenedBosses() {
  int[] ids={12191,12204,12214,12223}, normal={758,791,798,784}, awake={1099,1146,1157,1136}, hp={485,900,900,700}, ahp={1697,2700,2700,1400};
  for(int i=0;i<ids.length;i++) {
   assertEquals(hp[i],EncounterResolver.resolve(ids[i],new EncounterContext(EncounterMode.AUTO,normal[i],30,30)).getHitpoints());
   assertEquals(ahp[i],EncounterResolver.resolve(ids[i],new EncounterContext(EncounterMode.AUTO,awake[i],30,30)).getHitpoints());
  }
 }
 @Test public void autoWithoutEvidenceKeepsAmbiguity() {
  assertTrue(EncounterResolver.resolve(12191,EncounterContext.UNKNOWN).isAmbiguous());
 }
 @Test public void overridesAndQuestRemainSeparate() {
  assertEquals(1697,EncounterResolver.resolve(12191,new EncounterContext(EncounterMode.AWAKENED,-1,30,30)).getHitpoints());
  assertEquals(330,EncounterResolver.resolve(12195,EncounterContext.UNKNOWN).getHitpoints());
  assertTrue(EncounterResolver.resolve(12191,new EncounterContext(EncounterMode.QUEST,-1,30,30)).isAmbiguous());
 }
 @Test public void vardorvisDefenceFollowsHealth() {
  assertEquals(215,EncounterResolver.resolve(12223,new EncounterContext(EncounterMode.NORMAL,784,30,30)).getDefenceLevel());
  assertEquals(180,EncounterResolver.resolve(12223,new EncounterContext(EncounterMode.NORMAL,784,15,30)).getDefenceLevel());
  assertEquals(181,EncounterResolver.resolve(12223,new EncounterContext(EncounterMode.AWAKENED,1136,0,1)).getDefenceLevel());
  assertTrue(EncounterResolver.resolve(12223,new EncounterContext(EncounterMode.NORMAL,784,-1,-1)).isAmbiguous());
 }
 @Test public void unrelatedNpcUnchanged() {assertSame(TargetDatabase.find(11278),EncounterResolver.resolve(11278,EncounterContext.UNKNOWN));}
}
