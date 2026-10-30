package com.combatinsight;

import com.combatinsight.calculation.BlowpipeDart;
import com.combatinsight.calculation.CombatStyleOverride;
import com.combatinsight.calculation.HudDisplayMode;
import com.combatinsight.calculation.HudBackgroundMode;
import com.combatinsight.calculation.HudDisplayDuration;
import com.combatinsight.calculation.MagicSpell;
import com.combatinsight.calculation.TargetDefenceDisplay;
import com.combatinsight.calculation.TargetMagicDisplay;
import java.awt.Color;
import com.combatinsight.live.EncounterMode;
import com.combatinsight.live.DefenceTrackingMode;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;
import net.runelite.client.config.Keybind;

@ConfigGroup("combatinsight")
public interface CombatInsightConfig extends Config
{
    @ConfigSection(name = "Defence tracking", description = "Calculation tracking; separate from target row and infobox visibility", position = 8, closedByDefault = true)
    String trackingSection = "trackingSection";

    @ConfigItem(keyName = "defenceTrackingMode", name = "Defence tracking mode",
        description = "Track my reductions estimates only your supported specials. Off uses base target stats. This does not import Party Hub reductions. Changing mode clears old estimates. Alt-right-click the HUD to clear tracked reductions.",
        position = 0, section = trackingSection)
    default DefenceTrackingMode defenceTrackingMode() { return DefenceTrackingMode.LOCAL; }

    @ConfigItem(keyName="showRemainingDefence",name="Count Defence down to cap",
        description="Show remaining reducible Defence: 0 means the minimum has been reached, not an actual Defence level of 0. Tooltip and Inspector retain actual levels. Local estimates only.",
        position=1,section=trackingSection)
    default boolean showRemainingDefence() { return true; }

    @ConfigItem(keyName = "dt2EncounterMode", name = "DT2 encounter mode",
        description = "Auto detects normal or Awakened from the active boss combat level. Quest NPCs remain separate. Vardorvis requires visible health.",
        position = 30, section = calculationSection)
    default EncounterMode dt2EncounterMode() { return EncounterMode.AUTO; }

    @ConfigItem(keyName = "showInputMarker", name = "Show input notice marker",
        description = "Add * to the Max hit label when calculation notices or unavailable results need attention. Read notices in Advanced mode (or hold the details key), or copy diagnostics from the HUD menu. Adds no row; no marker is not a guarantee of full mechanic support.",
        position = 31, section = effectsSection)
    default boolean showInputMarker() { return false; }

    @ConfigItem(keyName = "holdDetailsKey", name = "Hold for Advanced details",
        description = "While held, show your enabled Advanced rows. Release restores your selected mode. Unbound by default.",
        position = 30, section = hudSection)
    default Keybind holdDetailsKey() { return Keybind.NOT_SET; }

    @ConfigItem(keyName = "kandarinHardDiary", name = "Kandarin hard diary",
        description = "Enable the 10% relative enchanted-bolt proc bonus after completing the hard diary.",
        position = 31, section = calculationSection)
    default boolean kandarinHardDiary() { return false; }

    @ConfigItem(keyName = "automaticTobInputs", name = "Observe ToB party (experimental)",
        description = "Opt-in: use the largest visible in-raid roster, including dead players. Enable before entering and verify starting size in diagnostics. Reconnecting or enabling mid-raid can miss departed players; leave off and set starting size manually in that case. No Party Hub data is read.",
        position = 7, section = raidSection)
    default boolean automaticTobInputs() { return false; }

    @ConfigItem(keyName = "automaticCoxInputs", name = "Observe CoX party and mode",
        description = "Use valid in-raid scaled party size, Challenge Mode and highest combat level. Disable to use manual inputs. HP and Mining remain manual.",
        position = 6, section = raidSection)
    default boolean automaticCoxInputs() { return true; }

	@ConfigSection(
		name = "HUD basics",
		description = "Basic overlay visibility, layout, and timing",
		position = 0
	)
	String hudSection = "hudSection";

	@ConfigSection(
		name = "Minimal HUD rows",
		description = "Rows available in every HUD mode",
		position = 2,
		closedByDefault = true
	)
	String minimalRowsSection = "minimalRowsSection";

	@ConfigSection(
		name = "Standard HUD rows",
		description = "Extra rows available in Standard and Advanced modes",
		position = 3,
		closedByDefault = true
	)
	String standardRowsSection = "standardRowsSection";

	@ConfigSection(
		name = "Advanced HUD rows",
		description = "Extra rows available only in Advanced mode",
		position = 4,
		closedByDefault = true
	)
	String advancedRowsSection = "advancedRowsSection";

	@ConfigSection(
		name = "Special attack rows",
		description = "Rows for supported special attacks and locally tracked target effects",
		position = 5,
		closedByDefault = true
	)
	String specialRowsSection = "specialRowsSection";

	@ConfigSection(
		name = "Calculation inputs",
		description = "Extra inputs for mechanics the client cannot expose safely",
		position = 6,
		closedByDefault = true
	)
	String calculationSection = "calculationSection";

	@ConfigSection(
		name = "Appearance",
		description = "Layout, colors, animation, and preview",
		position = 1,
		closedByDefault = true
	)
	String effectsSection = "effectsSection";

	@ConfigSection(name = "Calculation inspector", description = "Detailed calculation breakdown outside the combat HUD", position = 10, closedByDefault = true)
	String inspectorSection = "inspectorSection";

	@ConfigSection(name = "Raid inputs", description = "Party scaling inputs; set before entering the raid", position = 7, closedByDefault = true)
	String raidSection = "raidSection";

	@Range(min = 1, max = 100)
	@ConfigItem(keyName = "coxPartySize", name = "CoX party size (fallback)", description = "Manual fallback when automatic CoX observation is off or unavailable. Include extra scaling players.", position = 0, section = raidSection)
	default int coxPartySize() { return 1; }

	@ConfigItem(keyName = "coxChallengeMode", name = "CoX CM (fallback)", description = "Manual fallback when automatic CoX observation is off or unavailable; enable for CM.", position = 1, section = raidSection)
	default boolean coxChallengeMode() { return false; }

	@Range(min = 60, max = 126)
	@ConfigItem(keyName = "coxHighestCombat", name = "CoX highest combat (fallback)", description = "Manual fallback when highest combat is not observed. Highest combat level at raid start; 126 assumes a max-combat teammate.", position = 2, section = raidSection)
	default int coxHighestCombat() { return 126; }

	@Range(min = 1, max = 99)
	@ConfigItem(keyName = "coxHighestHitpoints", name = "CoX highest Hitpoints", description = "Highest unboosted Hitpoints level in the party at raid start", position = 3, section = raidSection)
	default int coxHighestHitpoints() { return 99; }

	@Range(min = 1, max = 99)
	@ConfigItem(keyName = "coxAverageMining", name = "CoX average Mining", description = "Unboosted party Mining total divided by party size, rounded down; affects Guardian HP", position = 4, section = raidSection)
	default int coxAverageMining() { return 99; }

	@Range(min = 1, max = 5)
	@ConfigItem(keyName = "tobPartySize", name = "ToB starting party size", description = "Party size at raid start, including players who later die. Normal/Hard solos and duos use trio health scaling.", position = 5, section = raidSection)
	default int tobPartySize() { return 5; }

    @Range(min = 0, max = 8)
    @ConfigItem(keyName = "archivePartySize", name = "Archive starting party size",
        description = "Include players who later die or leave. 0 means unknown. Uses published HP table cells only; a matching boss HUD takes priority. Observed roster is diagnostic because joining mid-raid can miss departed members.",
        position = 10, section = raidSection)
    default int archivePartySize() { return 0; }

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
		section = effectsSection
	)
	default boolean showTitle()
	{
		return false;
	}

	@ConfigItem(
		keyName = "compactHud", name = "Compact HUD",
		description = "Use narrower default widths and less outer padding. A manually resized overlay keeps its chosen width.",
		position = 5, section = effectsSection
	)
	default boolean compactHud() { return true; }

	@ConfigItem(
		keyName = "quietHudColors", name = "Quiet HUD colors",
		description = "Use neutral colors for routine values; keep warnings and max-hit change flashes visible",
		position = 6, section = effectsSection
	)
	default boolean quietHudColors() { return true; }

	@ConfigItem(
		keyName = "hudBackgroundMode", name = "Background style",
		description = "Choose subtle, high contrast, or text only; Custom color restores your saved background color",
		position = 7, section = effectsSection
	)
	default HudBackgroundMode hudBackgroundMode() { return HudBackgroundMode.CUSTOM; }

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
		section = trackingSection
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
		section = trackingSection
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
		section = trackingSection
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
		section = trackingSection
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
		section = trackingSection
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
		section = trackingSection
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
		description = "Spell damage when casting or Combat type is forced to Magic. A saved spell never overrides a detected melee style; powered staves use their own spell.",
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
		description = "Tombs of Amascut raid invocation used for defence rolls and HP scaling; verify against your raid",
		position = 8,
		section = raidSection
	)
	default int toaInvocationLevel()
	{
		return 0;
	}

    @Range(min=0,max=8)
    @ConfigItem(keyName="toaPartySize", name="ToA party size", position=9, section=raidSection,
        description="Starting party size, 1-8. Zero means unknown and withholds ToA HP and ruby damage.")
    default int toaPartySize() { return 0; }

    @Range(min=-1,max=6)
    @ConfigItem(keyName="toaPathLevel", name="ToA current path level", position=10, section=raidSection,
        description="Level of the path you are currently fighting, 0-6. -1 means unknown. Wardens do not use this input.")
    default int toaPathLevel() { return -1; }

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
		description = "Show a compact ? notice. Read full explanations in Inspector or copied diagnostics.",
		position = 6,
		section = advancedRowsSection
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
		description = "Saved HUD background color, used when Background style is Custom color",
		position = 4,
		section = effectsSection
	)
	default Color backgroundColor()
	{
		return new Color(20, 20, 20, 205);
	}

    @ConfigItem(keyName = "enableInspector", name = "Enable calculation inspector",
        description = "Show the Inspect calculation action in the HUD menu", position = 0, section = inspectorSection)
    default boolean enableInspector() { return true; }

    @ConfigItem(keyName = "recordOcellusEncounter", name = "Record raid diagnostics",
        description = "Temporary local Archive test recorder, including Bork, Smorks and Ocellus. Enable before pull; stop after fight. Starting again clears the previous recording.", position = 1, section = inspectorSection)
    default boolean recordOcellusEncounter() { return false; }


    @ConfigItem(keyName = "showManualIndicator", name = "Show manual input indicator",
        description = "Show Manual only when a manual override is active in the current calculation", position = 8, section = effectsSection)
    default boolean showManualIndicator() { return false; }

    @ConfigItem(keyName = "previewHud", name = "Preview HUD outside combat",
        description = "Show explicitly labelled illustrative values while outside combat; real combat always takes priority", position = 9, section = effectsSection)
    default boolean previewHud() { return false; }

    @ConfigItem(keyName = "highlightSpecialAttack", name = "Highlight spec max",
        description = "Accent the Spec max label and value for designated special weapons; this is not a recommendation to use a special", position = 10, section = specialRowsSection)
    default boolean highlightSpecialAttack() { return true; }

    @ConfigItem(keyName = "specialHighlightColor", name = "Spec highlight color",
        description = "Color for available Spec max values on designated special weapons", position = 11, section = specialRowsSection)
    default Color specialHighlightColor() { return new Color(0x42D9FF); }

    @ConfigItem(keyName = "specDivider", name = "Divider before special rows",
        description = "Draw a subtle divider before the special attack rows", position = 12, section = specialRowsSection)
    default boolean specDivider() { return false; }
}
