package com.combatinsight;

import com.combatinsight.calculation.SpecialAttackCalculator;
import com.combatinsight.calculation.SpecialAttackResult;
import com.combatinsight.calculation.SpecialAttackWeapon;
import com.combatinsight.live.LiveCombatSnapshot;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import net.runelite.client.ui.overlay.components.LineComponent;
import org.junit.Test;
import static org.junit.Assert.*;

public class HudSpecialRenderingTest
{
    @Test public void quietHudAccentsBothSpecLabelAndValue() throws Exception
    {
        SpecialAttackResult result = SpecialAttackCalculator.calculate(SpecialAttackWeapon.DRAGON_DAGGER,
            48, true, 24000, 8400, true, 0, false, false, false, false, false, false, 100, 100, 0, -1, false);
        LineComponent line = renderSpecial(result, new CombatInsightConfig() { });
        assertEquals(new Color(0x42D9FF), field(line, "leftColor"));
        assertEquals(new Color(0x42D9FF), field(line, "rightColor"));
    }

    @Test public void immuneSpecialKeepsExplicitWarningInsteadOfAccent() throws Exception
    {
        LineComponent line = renderSpecial(SpecialAttackResult.unavailable(SpecialAttackWeapon.VOIDWAKER, "Immune"),
            new CombatInsightConfig() { });
        assertEquals("Immune", field(line, "right"));
        assertEquals(new Color(255, 190, 80), field(line, "rightColor"));
        assertEquals(Color.WHITE, field(line, "leftColor"));
    }

    private static LineComponent renderSpecial(SpecialAttackResult result, CombatInsightConfig config) throws Exception
    {
        LiveCombatSnapshot snapshot = LiveCombatSnapshot.empty();
        Field special = LiveCombatSnapshot.class.getDeclaredField("specialAttackResult");
        special.setAccessible(true);
        special.set(snapshot, result);
        CombatInsightPlugin plugin = new CombatInsightPlugin() {
            @Override boolean isHudVisible() { return true; }
            @Override boolean isPreviewVisible() { return false; }
            @Override LiveCombatSnapshot getSnapshot() { return snapshot; }
        };
        Constructor<CombatInsightOverlay> ctor = CombatInsightOverlay.class.getDeclaredConstructor(CombatInsightPlugin.class, CombatInsightConfig.class);
        ctor.setAccessible(true);
        CombatInsightOverlay overlay = ctor.newInstance(plugin, config);
        overlay.setClearChildren(false);
        Graphics2D graphics = new BufferedImage(800, 1000, BufferedImage.TYPE_INT_ARGB).createGraphics();
        try { graphics.setFont(new Font(Font.DIALOG, Font.PLAIN, 12)); overlay.render(graphics); }
        finally { graphics.dispose(); }
        for (Object row : overlay.getPanelComponent().getChildren())
            if (row instanceof LineComponent && "Spec max".equals(field(row, "left"))) return (LineComponent)row;
        throw new AssertionError("Missing Spec max row");
    }

    private static Object field(Object instance, String name) throws Exception
    {
        Field field = instance.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(instance);
    }
}
