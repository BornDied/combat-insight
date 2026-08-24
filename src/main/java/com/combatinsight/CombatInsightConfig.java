package com.combatinsight;

import com.combatinsight.calculation.BlowpipeDart;
import com.combatinsight.calculation.CombatStyleOverride;
import com.combatinsight.calculation.HudDisplayMode;
import com.combatinsight.calculation.HudDisplayDuration;
import com.combatinsight.calculation.MagicSpell;
import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("combatinsight")
public interface CombatInsightConfig extends Config
{
	@ConfigSection(
		name = "HUD",
		description = "Basic overlay visibility, layout, and timing",
		position = 0
	)
	String hudSection = "hudSection";

	@ConfigSection(
		name = "Show and hide rows",
		description = "Choose which live combat rows are visible",
		position = 1,
		closedByDefault = true
	)
	String informationSection = "informationSection";

	@ConfigSection(
		name = "Calculation inputs",
		description = "Extra inputs for mechanics the client cannot expose safely",
		position = 2,
		closedByDefault = true
	)
	String calculationSection = "calculationSection";

	@ConfigSection(
		name = "Effects and advanced details",
		description = "Animation, calculation details, and conditional-mechanic notices",
		position = 3,
		closedByDefault = true
	)
	String advancedSection = "advancedSection";

	@ConfigItem(
		keyName = "showHud",
		name = "Show HUD",
		description = "Display the Combat Insight overlay",
		position = 0,
		section = hudSection
	)
	default boolean showHud()
	{
		return true;
	}

	@ConfigItem(
		keyName = "displayMode",
		name = "HUD mode",
		description = "Choose how much information the overlay displays",
		position = 1,
		section = hudSection
	)
	default HudDisplayMode displayMode()
	{
		return HudDisplayMode.STANDARD;
	}

	@ConfigItem(
		keyName = "displayDuration",
		name = "HUD duration",
		description = "How long the HUD remains visible after combat ends",
		position = 2,
		section = hudSection
	)
	default HudDisplayDuration displayDuration()
	{
		return HudDisplayDuration.PERMANENT;
	}

	@ConfigItem(
		keyName = "showTitle",
		name = "Show HUD title",
		description = "Display the Combat Insight title row inside the overlay",
		position = 3,
		section = hudSection
	)
	default boolean showTitle()
	{
		return false;
	}

	@ConfigItem(
		keyName = "showMaxHit",
		name = "Show max hit",
		description = "Display the calculated maximum hit",
		position = 0,
		section = informationSection
	)
	default boolean showMaxHit()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showStyle",
		name = "Show attack style",
		description = "Display the current attack style",
		position = 1,
		section = informationSection
	)
	default boolean showStyle()
	{
		return false;
	}

	@ConfigItem(
		keyName = "showWeapon",
		name = "Show weapon or spell",
		description = "Display the detected weapon or selected manual spell",
		position = 2,
		section = informationSection
	)
	default boolean showWeapon()
	{
		return false;
	}

	@ConfigItem(
		keyName = "showTarget",
		name = "Show target",
		description = "Display the current target name in Advanced mode",
		position = 3,
		section = informationSection
	)
	default boolean showTarget()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showAccuracy",
		name = "Show hit chance",
		description = "Display the calculated chance for the current attack to pass the retained target's defence roll",
		position = 4,
		section = informationSection
	)
	default boolean showAccuracy()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showDps",
		name = "Show DPS",
		description = "Display expected basic-attack damage per second in Standard and Advanced modes",
		position = 5,
		section = informationSection
	)
	default boolean showDps()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showObservedAverage",
		name = "Show average hitsplat",
		description = "In Advanced mode, show the average of your actual hitsplats against the current NPC, including zero-damage hits; multi-hit attacks count each splat separately",
		position = 6,
		section = informationSection
	)
	default boolean showObservedAverage()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showAccuracyRolls",
		name = "Show accuracy rolls",
		description = "In Advanced mode, show your attack roll beside the target's defence roll",
		position = 7,
		section = informationSection
	)
	default boolean showAccuracyRolls()
	{
		return false;
	}

	@ConfigItem(
		keyName = "showDefenceType",
		name = "Show target defence",
		description = "In Advanced mode, show which target defence type the current attack checks",
		position = 8,
		section = informationSection
	)
	default boolean showDefenceType()
	{
		return false;
	}

	@ConfigItem(
		keyName = "showLevels",
		name = "Show boosted levels",
		description = "Display the current boosted level and its Advanced calculation detail",
		position = 9,
		section = informationSection
	)
	default boolean showLevels()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showGearBonuses",
		name = "Show gear bonuses",
		description = "Display the equipped attack and damage bonuses",
		position = 10,
		section = informationSection
	)
	default boolean showGearBonuses()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showPrayer",
		name = "Show active prayer",
		description = "Display the active offensive prayer and its Advanced damage detail",
		position = 11,
		section = informationSection
	)
	default boolean showPrayer()
	{
		return true;
	}

	@ConfigItem(
		keyName = "combatStyleOverride",
		name = "Combat type",
		description = "Automatic is recommended; force a type for unusual weapons or manual spell casting",
		position = 0,
		section = calculationSection
	)
	default CombatStyleOverride combatStyleOverride()
	{
		return CombatStyleOverride.AUTO;
	}

	@ConfigItem(
		keyName = "blowpipeDart",
		name = "Blowpipe dart",
		description = "The dart currently stored in your blowpipe; RuneLite cannot read this from the worn ammo slot",
		position = 1,
		section = calculationSection
	)
	default BlowpipeDart blowpipeDart()
	{
		return BlowpipeDart.NOT_SET;
	}

	@ConfigItem(
		keyName = "magicSpell",
		name = "Manual magic spell",
		description = "Spell used for max hit when casting from a spellbook; powered staves are detected automatically",
		position = 2,
		section = calculationSection
	)
	default MagicSpell magicSpell()
	{
		return MagicSpell.NOT_SET;
	}

	@ConfigItem(
		keyName = "applySlayerBonus",
		name = "Target is Slayer task",
		description = "Apply the equipped Slayer helmet or black mask bonus to the current calculation",
		position = 3,
		section = calculationSection
	)
	default boolean applySlayerBonus()
	{
		return false;
	}

	@ConfigItem(
		keyName = "toaInvocationLevel",
		name = "ToA invocation",
		description = "Tombs of Amascut raid invocation used for target defence scaling; leave at 0 outside ToA",
		position = 4,
		section = calculationSection
	)
	default int toaInvocationLevel()
	{
		return 0;
	}

	@ConfigItem(
		keyName = "showAttackSpeed",
		name = "Show attack speed",
		description = "Display the weapon attack interval in ticks (Advanced mode)",
		position = 0,
		section = advancedSection
	)
	default boolean showAttackSpeed()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showWarnings",
		name = "Show calculation notices",
		description = "Explain missing inputs and mechanics that are not safe to calculate yet",
		position = 1,
		section = advancedSection
	)
	default boolean showWarnings()
	{
		return true;
	}

	@ConfigItem(
		keyName = "animateChanges",
		name = "Animate changes",
		description = "Briefly pulse the max hit green when it rises and red when it falls",
		position = 2,
		section = advancedSection
	)
	default boolean animateChanges()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		keyName = "backgroundColor",
		name = "Background color",
		description = "HUD background color",
		position = 4,
		section = hudSection
	)
	default Color backgroundColor()
	{
		return new Color(20, 20, 20, 205);
	}
}
