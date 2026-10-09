package com.combatinsight;
import org.junit.Test;
import static org.junit.Assert.*;
import com.combatinsight.calculation.HudDisplayMode;
import com.combatinsight.live.LiveCombatSnapshot;
public class HeldDetailsTest {
 @Test public void releaseRestoresEveryConfiguredMode() {
  HeldDetails details=new HeldDetails();
  for(HudDisplayMode mode:HudDisplayMode.values()) {
   assertEquals(mode,details.resolve(mode)); details.press();
   assertEquals(HudDisplayMode.ADVANCED,details.resolve(mode)); details.clear();
   assertEquals(mode,details.resolve(mode));
  }
 }
 @Test public void emptyDiagnosticIsUsefulAndContainsNoAccountFields() {
  String report=LiveCombatSnapshot.empty().diagnosticReport();
  assertTrue(report.contains("Combat Insight " + BuildInfo.VERSION));
  assertTrue(report.contains("Logged in: false"));
  assertFalse(report.contains("username"));
 }
 @Test public void focusAndLogoutClearTemporaryMode() throws Exception {
  CombatInsightPlugin plugin=new CombatInsightPlugin();
  java.lang.reflect.Field field=CombatInsightPlugin.class.getDeclaredField("heldDetails");
  field.setAccessible(true); HeldDetails details=(HeldDetails)field.get(plugin);
  details.press();
  net.runelite.api.events.FocusChanged focus=new net.runelite.api.events.FocusChanged(); focus.setFocused(false);
  plugin.onFocusChanged(focus);
  assertEquals(HudDisplayMode.STANDARD,plugin.effectiveHudMode(HudDisplayMode.STANDARD));
  assertEquals(net.runelite.client.config.Keybind.NOT_SET,new CombatInsightConfig(){}.holdDetailsKey());
 }
}
