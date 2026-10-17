package com.combatinsight;

import com.combatinsight.calculation.HudDisplayMode;
import com.combatinsight.live.LiveCombatSnapshot;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import java.util.stream.Collectors;
import net.runelite.client.ui.overlay.components.LineComponent;
import org.junit.Test;
import static org.junit.Assert.*;

public class HudPreviewTest
{
    @Test public void minimalPreviewIsExplicitAndRespectsMaxToggle() throws Exception
    {
        CombatInsightConfig config = new CombatInsightConfig() {
            @Override public HudDisplayMode displayMode() { return HudDisplayMode.MINIMAL; }
            @Override public boolean showMaxHit() { return false; }
        };
        CombatInsightOverlay overlay = preview(config);
        assertEquals("PREVIEW", label((LineComponent)overlay.getPanelComponent().getChildren().get(0)));
        assertEquals(1, overlay.getPanelComponent().getChildren().size());
    }

    @Test public void previewRespectsStandardRowsAndClearlyLabelsIllustrativeValues() throws Exception
    {
        CombatInsightOverlay overlay = preview(new CombatInsightConfig() {
            @Override public boolean showDps() { return false; }
        });
        String labels = overlay.getPanelComponent().getChildren().stream()
            .filter(c -> c instanceof LineComponent).map(c -> label((LineComponent)c)).collect(Collectors.joining(","));
        assertTrue(labels.contains("PREVIEW"));
        assertTrue(labels.contains("Hit chance"));
        assertTrue(labels.contains("Spec max"));
        assertFalse(labels.contains("DPS"));
    }

    private static String label(LineComponent line)
    {
        try {
            java.lang.reflect.Field field = LineComponent.class.getDeclaredField("left");
            field.setAccessible(true);
            return (String) field.get(line);
        } catch (ReflectiveOperationException e) { throw new AssertionError(e); }
    }

    private CombatInsightOverlay preview(CombatInsightConfig config) throws Exception
    {
        CombatInsightPlugin plugin = new CombatInsightPlugin() {
            @Override boolean isHudVisible() { return false; }
            @Override boolean isPreviewVisible() { return true; }
            @Override LiveCombatSnapshot getSnapshot() { return LiveCombatSnapshot.empty(); }
        };
        Constructor<CombatInsightOverlay> ctor = CombatInsightOverlay.class.getDeclaredConstructor(CombatInsightPlugin.class, CombatInsightConfig.class);
        ctor.setAccessible(true);
        CombatInsightOverlay overlay = ctor.newInstance(plugin, config);
        overlay.setClearChildren(false);
        Graphics2D graphics = new BufferedImage(800,1000,BufferedImage.TYPE_INT_ARGB).createGraphics();
        try { graphics.setFont(new Font(Font.DIALOG,Font.PLAIN,12)); overlay.render(graphics); overlay.render(graphics); }
        finally { graphics.dispose(); }
        return overlay;
    }
}
