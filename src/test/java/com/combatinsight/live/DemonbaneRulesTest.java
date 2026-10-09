package com.combatinsight.live;
import org.junit.Test;
import static org.junit.Assert.*;
public class DemonbaneRulesTest {
 @Test public void dukeUsesSeventyPercentOfDemonbaneBonus() {
  assertEquals(49,DemonbaneRules.bonusPercent(12191,70));
  assertEquals(21,DemonbaneRules.bonusPercent(12195,30));
  assertEquals(70,DemonbaneRules.bonusPercent(3129,70));
 }
}
