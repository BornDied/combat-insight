package com.combatinsight;

import com.combatinsight.calculation.BlowpipeDart;
import com.combatinsight.calculation.CombatStyleOverride;
import com.combatinsight.calculation.HudDisplayMode;
import com.combatinsight.calculation.HudDisplayDuration;
import com.combatinsight.calculation.MagicSpell;
import com.combatinsight.calculation.TargetDefenceDisplay;
import com.combatinsight.calculation.TargetMagicDisplay;
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
		name = "Minimal HUD rows",
		description = "Rows available in every HUD mode",
		position = 1,
		closedByDefault = true
	)
	String minimalRowsSection = "minimalRowsSection";

	@ConfigSection(
		name = "Standard HUD rows",
		description = "Extra rows available in Standard and Advanced modes",
		position = 2,
		closedByDefault = true
	)
	String standardRowsSection = "standardRowsSection";

	@ConfigSection(
		name = "Advanced HUD rows",
		description = "Extra rows available only in Advanced mode",
		position = 3,
		closedByDefault = true
	)
	String advancedRowsSection = "advancedRowsSection";

	@ConfigSection(
		name = "Special attack rows",
		description = "Rows for supported special attacks and locally tracked target effects",
		position = 4,
		closedByDefault = true
	)
	String specialRowsSection = "specialRowsSection";

	@ConfigSection(
		name = "Calculation inputs",
		description = "Extra inputs for mechanics the client cannot expose safely",
		position = 5,
		closedByDefault = true
	)
	String calculationSection = "calculationSection";

	@ConfigSection(
		name = "Effects and advanced details",
		description = "Animation, calculation details, and conditional-mechanic notices",
		position = 6,
		closedByDefault = true
	)
	String effectsSection = "effectsSection";

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
		section = minimalRowsSection
	)
	default boolean showMaxHit()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showStyle",
		name = "Show attack style",
		description = "Display the current attack style in Standard and Advanced modes",
		position = 2,
		section = standardRowsSection
	)
	default boolean showStyle()
	{
		return false;
	}

	@ConfigItem(
		keyName = "showWeapon",
		name = "Show weapon or spell",
		description = "Display the detected weapon or selected manual spell in Standard and Advanced modes",
		position = 3,
		section = standardRowsSection
	)
	default boolean showWeapon()
	{
		return false;
	}

	@ConfigItem(
		keyName = "showTarget",
		name = "Show target",
		description = "Display the current target name and estimated hitpoints when available",
		position = 0,
		section = advancedRowsSection
	)
	default boolean showTarget()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showMaxSplit",
		name = "Show max split",
		description = "In Advanced mode, show the individual maximum hitsplats for supported multi-hit weapons",
		position = 1,
		section = advancedRowsSection
	)
	default boolean showMaxSplit()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showAccuracy",
		name = "Show hit chance",
		description = "Display the calculated chance for the current attack to pass the retained target's defence roll",
		position = 0,
		section = standardRowsSection
	)
	default boolean showAccuracy()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showDps",
		name = "Show DPS",
		description = "Display expected basic-attack damage per second in Standard and Advanced modes",
		position = 1,
		section = standardRowsSection
	)
	default boolean showDps()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showObservedAverage",
		name = "Show average hitsplat",
		description = "In Advanced mode, show the average of your actual hitsplats against the current NPC, including zero-damage hits; multi-hit attacks count each splat separately",
		position = 3,
		section = advancedRowsSection
	)
	default boolean showObservedAverage()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showAccuracyRolls",
		name = "Show accuracy rolls",
		description = "In Advanced mode, show your attack roll beside the target's defence roll",
		position = 4,
		section = advancedRowsSection
	)
	default boolean showAccuracyRolls()
	{
		return false;
	}

	@ConfigItem(
		keyName = "showDefenceType",
		name = "Show target defence",
		description = "In Advanced mode, show which target defence type the current attack checks",
		position = 5,
		section = advancedRowsSection
	)
	default boolean showDefenceType()
	{
		return false;
	}

	@ConfigItem(
		keyName = "showLevels",
		name = "Show boosted levels",
		description = "Display the current boosted level and its additional Advanced calculation detail",
		position = 4,
		section = standardRowsSection
	)
	default boolean showLevels()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showGearBonuses",
		name = "Show gear bonuses",
		description = "Display the equipped attack and damage bonuses in Standard and Advanced modes",
		position = 5,
		section = standardRowsSection
	)
	default boolean showGearBonuses()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showPrayer",
		name = "Show active prayer",
		description = "Display the active offensive prayer and its additional Advanced damage detail",
		position = 6,
		section = standardRowsSection
	)
	default boolean showPrayer()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showSpecMax",
		name = "Show spec max",
		description = "Display the supported weapon's special-attack maximum in Standard and Advanced modes",
		position = 0,
		section = specialRowsSection
	)
	default boolean showSpecMax()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showSpecChance",
		name = "Show spec chance",
		description = "Display the chance that at least one special-attack accuracy roll succeeds in Standard and Advanced modes",
		position = 1,
		section = specialRowsSection
	)
	default boolean showSpecChance()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showExpectedSpec",
		name = "Show average spec damage",
		description = "In Advanced mode, display average total damage for one special-attack use",
		position = 2,
		section = specialRowsSection
	)
	default boolean showExpectedSpec()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showSpecOnHit",
		name = "Show on-hit effect",
		description = "In Advanced mode, summarize the supported special attack's hit behaviour or target effect",
		position = 3,
		section = specialRowsSection
	)
	default boolean showSpecOnHit()
	{
		return true;
	}

	@ConfigItem(
		keyName = "targetDefenceDisplay",
		name = "Target Defence display",
		description = "Choose where locally tracked target Defence is displayed",
		position = 4,
		section = specialRowsSection
	)
	default TargetDefenceDisplay targetDefenceDisplay()
	{
		return TargetDefenceDisplay.INFOBOX;
	}

	@ConfigItem(
		keyName = "targetMagicDisplay",
		name = "Target Magic display",
		description = "Show tracked Magic stats only while your current combat style is Magic",
		position = 5,
		section = specialRowsSection
	)
	default TargetMagicDisplay targetMagicDisplay()
	{
		return TargetMagicDisplay.INFOBOX;
	}

	@ConfigItem(
		keyName = "targetDefenceTextColor",
		name = "Defence number color",
		description = "Color of the target Defence number in the movable infobox",
		position = 6,
		section = specialRowsSection
	)
	default Color targetDefenceTextColor()
	{
		return new Color(255, 152, 31);
	}

	@ConfigItem(
		keyName = "targetDefenceFlashColor",
		name = "Defence flash color",
		description = "Color briefly shown when a confirmed target Defence reduction lands",
		position = 7,
		section = specialRowsSection
	)
	default Color targetDefenceFlashColor()
	{
		return new Color(100, 235, 130);
	}

	@ConfigItem(
		keyName = "targetMagicTextColor",
		name = "Magic number color",
		description = "Color of the tracked target Magic stat in the movable Magic infobox",
		position = 8,
		section = specialRowsSection
	)
	default Color targetMagicTextColor()
	{
		return new Color(255, 220, 80);
	}

	@ConfigItem(
		keyName = "targetMagicFlashColor",
		name = "Magic flash color",
		description = "Color briefly shown when a confirmed Magic-related reduction lowers the displayed stat",
		position = 9,
		section = specialRowsSection
	)
	default Color targetMagicFlashColor()
	{
		return new Color(100, 235, 130);
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
		position = 2,
		section = advancedRowsSection
	)
	default boolean showAttackSpeed()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showWarnings",
		name = "Show calculation notices",
		description = "Explain missing inputs and mechanics that are not safe to calculate yet",
		position = 0,
		section = effectsSection
	)
	default boolean showWarnings()
	{
		return true;
	}

	@ConfigItem(
		keyName = "animateChanges",
		name = "Animate changes",
		description = "Briefly pulse max-hit changes and confirmed target Defence reductions",
		position = 1,
		section = effectsSection
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
