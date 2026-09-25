package com.combatinsight;

import com.combatinsight.calculation.HudDisplayMode;
import com.combatinsight.calculation.HudBackgroundMode;
import com.combatinsight.live.LiveCombatSnapshot;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import org.junit.Test;

import static org.junit.Assert.*;

public class CombatInsightOverlayTest
{
	@Test
	public void compactToggleChangesFootprintAndKeepsSelectedRows() throws Exception
	{
		Settings settings = new Settings();
		CombatInsightOverlay overlay = overlay(settings);
		overlay.setClearChildren(false);
		Dimension compact = render(overlay);
		int rows = overlay.getPanelComponent().getChildren().size();
		settings.compact = false;
		Dimension classic = render(overlay);
		assertTrue(compact.width < classic.width);
		assertTrue(compact.height < classic.height);
		assertEquals(rows, overlay.getPanelComponent().getChildren().size());
		settings.compact = true;
		assertEquals(compact, render(overlay));
	}

	@Test
	public void backgroundPresetsRetainTheCustomChoice() throws Exception
	{
		Settings settings = new Settings();
		CombatInsightOverlay overlay = overlay(settings);
		render(overlay);
		assertEquals(settings.backgroundColor(), overlay.getPanelComponent().getBackgroundColor());
		settings.background = HudBackgroundMode.TEXT_ONLY;
		render(overlay);
		assertEquals(0, overlay.getPanelComponent().getBackgroundColor().getAlpha());
		settings.background = HudBackgroundMode.SUBTLE;
		render(overlay);
		assertTrue(overlay.getPanelComponent().getBackgroundColor().getAlpha() > 0);
		assertTrue(overlay.getPanelComponent().getBackgroundColor().getAlpha() < settings.backgroundColor().getAlpha());
		settings.background = HudBackgroundMode.CUSTOM;
		render(overlay);
		assertEquals(settings.backgroundColor(), overlay.getPanelComponent().getBackgroundColor());
	}

	@Test
	public void quietColorsKeepWarningsAndChangeFlashes() throws Exception
	{
		Settings settings = new Settings();
		settings.title = true;
		CombatInsightOverlay overlay = overlay(settings);
		BufferedImage image = image(overlay);
		assertFalse(hasColor(image, new Color(120, 220, 140)));
		assertTrue(hasColor(image, new Color(255, 190, 80)));
		settings.quiet = false;
		assertTrue(hasColor(image(overlay), new Color(120, 220, 140)));
		settings.quiet = true;
		Field available = LiveCombatSnapshot.class.getDeclaredField("maxHitAvailable");
		available.setAccessible(true);
		available.setBoolean(settings.snapshot, true);
		assertTrue("A quiet HUD must retain max-hit change feedback", hasColor(image(overlay), Color.MAGENTA));
	}

	@Test
	public void defaultWidthFollowsModeWithoutOverridingUserResize() throws Exception
	{
		Settings settings = new Settings();
		CombatInsightOverlay overlay = overlay(settings);
		settings.mode = HudDisplayMode.MINIMAL;
		Dimension minimal = render(overlay);
		settings.mode = HudDisplayMode.STANDARD;
		Dimension standard = render(overlay);
		assertTrue("Minimal should be narrower than Standard", minimal.width < standard.width);
		settings.mode = HudDisplayMode.ADVANCED;
		assertTrue("Advanced should accommodate its longer rows", render(overlay).width > standard.width);
		overlay.setPreferredSize(new Dimension(310, 0));
		settings.mode = HudDisplayMode.MINIMAL;
		assertEquals("Explicit user sizing takes precedence", 310, render(overlay).width);
	}

	@Test
	public void minimalModeKeepsStandardRowsHiddenAndTogglesSurviveModeChanges() throws Exception
	{
		Settings settings = new Settings();
		CombatInsightOverlay overlay = overlay(settings);
		overlay.setClearChildren(false);
		settings.mode = HudDisplayMode.MINIMAL;
		render(overlay);
		assertEquals(1, overlay.getPanelComponent().getChildren().size());
		settings.mode = HudDisplayMode.STANDARD;
		render(overlay);
		assertEquals(3, overlay.getPanelComponent().getChildren().size());
		settings.accuracy = false;
		render(overlay);
		assertEquals(2, overlay.getPanelComponent().getChildren().size());
		settings.mode = HudDisplayMode.MINIMAL;
		render(overlay);
		settings.mode = HudDisplayMode.STANDARD;
		render(overlay);
		assertEquals(2, overlay.getPanelComponent().getChildren().size());
	}

	private static CombatInsightOverlay overlay(Settings settings) throws Exception
	{
		CombatInsightPlugin plugin = new CombatInsightPlugin()
		{
			@Override boolean isHudVisible() { return true; }
			@Override LiveCombatSnapshot getSnapshot() { return settings.snapshot; }
			@Override Color getMaxHitChangeColor() { return Color.MAGENTA; }
		};
		Constructor<CombatInsightOverlay> constructor = CombatInsightOverlay.class
			.getDeclaredConstructor(CombatInsightPlugin.class, CombatInsightConfig.class);
		constructor.setAccessible(true);
		return constructor.newInstance(plugin, settings);
	}

	private static Dimension render(CombatInsightOverlay overlay)
	{
		BufferedImage image = new BufferedImage(800, 1000, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		try
		{
			graphics.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
			overlay.render(graphics); // RuneLite panels cache child dimensions between frames.
			return overlay.render(graphics);
		}
		finally { graphics.dispose(); }
	}

	private static BufferedImage image(CombatInsightOverlay overlay)
	{
		render(overlay);
		BufferedImage image = new BufferedImage(800, 1000, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		try
		{
			graphics.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
			overlay.render(graphics);
		}
		finally { graphics.dispose(); }
		return image;
	}

	private static boolean hasColor(BufferedImage image, Color color)
	{
		for (int y = 0; y < image.getHeight(); y++)
			for (int x = 0; x < image.getWidth(); x++)
				if (image.getRGB(x, y) == color.getRGB()) return true;
		return false;
	}

	private static final class Settings implements CombatInsightConfig
	{
		private HudDisplayMode mode = HudDisplayMode.STANDARD;
		private boolean accuracy = true;
		private boolean compact = true;
		private boolean quiet = true;
		private boolean title;
		private HudBackgroundMode background = HudBackgroundMode.CUSTOM;
		private LiveCombatSnapshot snapshot = LiveCombatSnapshot.empty();
		@Override public HudDisplayMode displayMode() { return mode; }
		@Override public boolean showAccuracy() { return accuracy; }
		@Override public boolean showLevels() { return false; }
		@Override public boolean showGearBonuses() { return false; }
		@Override public boolean showPrayer() { return false; }
		@Override public boolean compactHud() { return compact; }
		@Override public boolean quietHudColors() { return quiet; }
		@Override public boolean showTitle() { return title; }
		@Override public HudBackgroundMode hudBackgroundMode() { return background; }
		@Override public Color backgroundColor() { return new Color(40, 60, 80, 220); }
	}
}
