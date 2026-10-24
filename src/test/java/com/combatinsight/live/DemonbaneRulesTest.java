package com.combatinsight.live;
import org.junit.Test;
import static org.junit.Assert.*;
public class DemonbaneRulesTest {
 @Test public void archiveDemonsReduceOnlyTheBonusWithReferenceRounding() {
  for (int id : new int[]{16665,16666}) {
   assertEquals(17,DemonbaneRules.bonusPercent(id,70));
   assertEquals(7,DemonbaneRules.bonusPercent(id,30));
   assertEquals(1,DemonbaneRules.bonusPercent(id,5));
   assertEquals(5,DemonbaneRules.bonusPercent(id,20));
  }
  for (int id : new int[]{16674,16677,16684}) {
   assertEquals(56,DemonbaneRules.bonusPercent(id,70));
   assertEquals(24,DemonbaneRules.bonusPercent(id,30));
   assertEquals(4,DemonbaneRules.bonusPercent(id,5));
   assertEquals(16,DemonbaneRules.bonusPercent(id,20));
  }
  assertEquals(80,DemonbaneRules.bonusPercent(7584,70));
  assertEquals(30,DemonbaneRules.bonusPercent(3129,30));
 }
 @Test public void dukeUsesSeventyPercentOfDemonbaneBonus() {
  assertEquals(49,DemonbaneRules.bonusPercent(12191,70));
  assertEquals(21,DemonbaneRules.bonusPercent(12195,30));
  assertEquals(70,DemonbaneRules.bonusPercent(3129,70));
 }
}
