package com.combatinsight.live;
import org.junit.Test;
import static org.junit.Assert.*;
public class RaidInputResolverTest {
 @Test public void automaticCoxRequiresActiveRaidAndValidSize() {
  RaidScaling manual=new RaidScaling(2,false,100,90,80,4);
  assertEquals(manual,RaidInputResolver.resolve(manual,true,false,5,1,126));
  assertEquals(manual,RaidInputResolver.resolve(manual,true,true,0,1,126));
  assertEquals(manual,RaidInputResolver.resolve(manual,false,true,5,1,126));
  assertEquals(new RaidScaling(5,true,126,90,80,4),RaidInputResolver.resolve(manual,true,true,5,1,126));
 }
 @Test public void invalidObservedCombatKeepsManualTeamStats() {
  assertEquals(new RaidScaling(5,false,100,90,80,4),RaidInputResolver.resolve(new RaidScaling(2,false,100,90,80,4),true,true,5,0,0));
 }
}
