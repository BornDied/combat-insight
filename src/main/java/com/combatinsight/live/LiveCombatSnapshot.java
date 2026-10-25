package com.combatinsight.live;

import com.combatinsight.BuildInfo;

import com.combatinsight.calculation.BlowpipeDart;
import com.combatinsight.calculation.AttackType;
import com.combatinsight.calculation.CombatPrediction;
import com.combatinsight.calculation.CombatStyle;
import com.combatinsight.calculation.CombatStyleOverride;
import com.combatinsight.calculation.DamageRoll;
import com.combatinsight.calculation.MagicSpell;
import com.combatinsight.calculation.MultiHitResult;
import com.combatinsight.calculation.MultiHitWeapon;
import com.combatinsight.calculation.Obligator;
import com.combatinsight.calculation.ZoryaTome;
import com.combatinsight.calculation.Rondache;
import com.combatinsight.calculation.SpecialAttackCalculator;
import com.combatinsight.calculation.SoulreaperSpecial;
import com.combatinsight.calculation.ContextSpecialAttack;
import com.combatinsight.calculation.HitTransform;
import com.combatinsight.calculation.BoltEffect;
import com.combatinsight.calculation.SpecialAttackResult;
import com.combatinsight.calculation.SpecialAttackWeapon;
import com.combatinsight.calculation.TargetProfile;
import com.combatinsight.calculation.WeaponCategory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import net.runelite.api.Client;
import net.runelite.api.EnumID;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.GameState;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.ParamID;
import net.runelite.api.Prayer;
import net.runelite.api.Skill;
import net.runelite.api.StructComposition;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.game.ItemEquipmentStats;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStats;

/**
 * Read-only combat data collected from the live RuneLite client.
 *
 * This adapter deliberately distinguishes between an exact basic result and a
 * result that needs an input the game client does not expose. It is better for
 * the HUD to say "Set spell" than to show a confident melee number while the
 * player is using Magic.
 */
public final class LiveCombatSnapshot
{
	private static final int DEFAULT_ATTACK_SPEED_TICKS = 4;
	private static final int GLOBAL_DAMAGE_CAP = 200;

	private final boolean loggedIn;
	private final CombatStyle combatStyle;
	private final AttackType attackType;
	private final int boostedAttack;
	private final int boostedStrength;
	private final int boostedRanged;
	private final int boostedMagic;
	private final int meleeAttackBonus;
	private final int meleeStrengthBonus;
	private final int rangedAttackBonus;
	private final int rangedStrengthBonus;
	private final int magicAttackBonus;
	private final float magicDamagePercent;
	private final int attackSpeedTicks;
	private final int styleDamageBonus;
	private final int effectiveDamageLevel;
	private final int magicBaseMaxHit;
	private final String styleName;
	private final String weaponOrSpellName;
	private final String prayerName;
	private final double damagePrayerMultiplier;
	private final boolean prayerAffectsCurrentStyle;
	private final boolean slayerHelmetEquipped;
	private final boolean hybridAtlatl;
	private final boolean combatDummy;
	private final String targetName;
	private final int targetId;
	private final int targetCurrentHitpoints;
	private final int targetMaximumHitpoints;
	private final boolean maxHitAvailable;
	private final int maxHit;
	private final String maxHitStatusText;
	private final TargetResult targetResult;
	private final SpecialAttackResult specialAttackResult;
	private final TargetEffectSnapshot targetEffects;
	private final int baseTargetMagicDefenceRoll;
	private final int currentTargetMagicDefenceRoll;
	private final boolean targetImmune;
	private final List<String> warnings;
    private final String diagnosticInputs;
    private final boolean activeManualOverride;
    private final List<String> appliedEffects;

	private LiveCombatSnapshot(
		boolean loggedIn,
		CombatStyle combatStyle,
		AttackType attackType,
		int boostedAttack,
		int boostedStrength,
		int boostedRanged,
		int boostedMagic,
		int meleeAttackBonus,
		int meleeStrengthBonus,
		int rangedAttackBonus,
		int rangedStrengthBonus,
		int magicAttackBonus,
		float magicDamagePercent,
		int attackSpeedTicks,
		int styleDamageBonus,
		int effectiveDamageLevel,
		int magicBaseMaxHit,
		String styleName,
		String weaponOrSpellName,
		String prayerName,
		double damagePrayerMultiplier,
		boolean prayerAffectsCurrentStyle,
		boolean slayerHelmetEquipped,
		boolean hybridAtlatl,
		boolean combatDummy,
		String targetName,
		int targetId,
		int targetCurrentHitpoints,
		int targetMaximumHitpoints,
		boolean maxHitAvailable,
		int maxHit,
		String maxHitStatusText,
		TargetResult targetResult,
		SpecialAttackResult specialAttackResult,
		TargetEffectSnapshot targetEffects,
		int baseTargetMagicDefenceRoll,
		int currentTargetMagicDefenceRoll,
		boolean targetImmune,
		List<String> warnings, String diagnosticInputs, boolean activeManualOverride, List<String> appliedEffects)
	{
		this.diagnosticInputs = diagnosticInputs;
        this.activeManualOverride = activeManualOverride;
        this.appliedEffects = Collections.unmodifiableList(new ArrayList<>(appliedEffects));
		this.loggedIn = loggedIn;
		this.combatStyle = combatStyle;
		this.attackType = attackType;
		this.boostedAttack = boostedAttack;
		this.boostedStrength = boostedStrength;
		this.boostedRanged = boostedRanged;
		this.boostedMagic = boostedMagic;
		this.meleeAttackBonus = meleeAttackBonus;
		this.meleeStrengthBonus = meleeStrengthBonus;
		this.rangedAttackBonus = rangedAttackBonus;
		this.rangedStrengthBonus = rangedStrengthBonus;
		this.magicAttackBonus = magicAttackBonus;
		this.magicDamagePercent = magicDamagePercent;
		this.attackSpeedTicks = attackSpeedTicks;
		this.styleDamageBonus = styleDamageBonus;
		this.effectiveDamageLevel = effectiveDamageLevel;
		this.magicBaseMaxHit = magicBaseMaxHit;
		this.styleName = styleName;
		this.weaponOrSpellName = weaponOrSpellName;
		this.prayerName = prayerName;
		this.damagePrayerMultiplier = damagePrayerMultiplier;
		this.prayerAffectsCurrentStyle = prayerAffectsCurrentStyle;
		this.slayerHelmetEquipped = slayerHelmetEquipped;
		this.hybridAtlatl = hybridAtlatl;
		this.combatDummy = combatDummy;
		this.targetName = targetName;
		this.targetId = targetId;
		this.targetCurrentHitpoints = targetCurrentHitpoints;
		this.targetMaximumHitpoints = targetMaximumHitpoints;
		this.maxHitAvailable = maxHitAvailable;
		this.maxHit = maxHit;
		this.maxHitStatusText = maxHitStatusText;
		this.targetResult = targetResult;
		this.specialAttackResult = specialAttackResult;
		this.targetEffects = targetEffects;
		this.baseTargetMagicDefenceRoll = baseTargetMagicDefenceRoll;
		this.currentTargetMagicDefenceRoll = currentTargetMagicDefenceRoll;
		this.targetImmune = targetImmune;
		this.warnings = Collections.unmodifiableList(new ArrayList<>(warnings));
	}

	public static LiveCombatSnapshot empty()
	{
		return new LiveCombatSnapshot(
			false,
			CombatStyle.MELEE,
			AttackType.UNKNOWN,
			0, 0, 0, 0,
			0, 0, 0, 0, 0, 0.0f,
			DEFAULT_ATTACK_SPEED_TICKS,
			0, 0, 0,
			"Unavailable",
			"None",
			"None",
			1.0,
			false,
			false,
			false,
			false,
			"",
			-1,
			-1,
			0,
			false,
			0,
			"Unavailable",
			TargetResult.noTarget(),
			SpecialAttackResult.noWeapon(),
			TargetEffectSnapshot.empty(),
			-1,
			-1,
			false,
			Collections.emptyList(), "No active inputs", false, Collections.emptyList());
	}

	public static LiveCombatSnapshot capture(
		Client client,
		ItemManager itemManager,
		CombatStyleOverride combatStyleOverride,
		BlowpipeDart blowpipeDart,
		MagicSpell manualSpell,
		boolean applySlayerBonus,
		int toaInvocationLevel,
		int retainedTargetId,
		String retainedTargetName,
		int targetHealthRatio,
		int targetHealthScale,
		TargetEffectSnapshot targetEffectSnapshot,
		boolean targetHasLocalHits)
	{
		return capture(
			client,
			itemManager,
			combatStyleOverride,
			blowpipeDart,
			manualSpell,
			applySlayerBonus,
			toaInvocationLevel,
			retainedTargetId,
			retainedTargetName,
			targetHealthRatio,
			targetHealthScale,
			targetEffectSnapshot,
			targetHasLocalHits,
			null);
	}

	public static LiveCombatSnapshot capture(
		Client client,
		ItemManager itemManager,
		CombatStyleOverride combatStyleOverride,
		BlowpipeDart blowpipeDart,
		MagicSpell manualSpell,
		boolean applySlayerBonus,
		int toaInvocationLevel,
		int retainedTargetId,
		String retainedTargetName,
		int targetHealthRatio,
		int targetHealthScale,
		TargetEffectSnapshot targetEffectSnapshot,
		boolean targetHasLocalHits,
		CombatDummy selectedCombatDummy)
	{
		return capture(client, itemManager, combatStyleOverride, blowpipeDart, manualSpell,
			applySlayerBonus, toaInvocationLevel, retainedTargetId, retainedTargetName,
			targetHealthRatio, targetHealthScale, targetEffectSnapshot, targetHasLocalHits,
			selectedCombatDummy, RaidScaling.DEFAULT);
	}

	public static LiveCombatSnapshot capture(
		Client client, ItemManager itemManager, CombatStyleOverride combatStyleOverride,
		BlowpipeDart blowpipeDart, MagicSpell manualSpell, boolean applySlayerBonus,
		int toaInvocationLevel, int retainedTargetId, String retainedTargetName,
		int targetHealthRatio, int targetHealthScale, TargetEffectSnapshot targetEffectSnapshot,
		boolean targetHasLocalHits, CombatDummy selectedCombatDummy, RaidScaling raidScaling)
	{
        return capture(client, itemManager, combatStyleOverride, blowpipeDart, manualSpell,
            applySlayerBonus, toaInvocationLevel, retainedTargetId, retainedTargetName,
            targetHealthRatio, targetHealthScale, targetEffectSnapshot, targetHasLocalHits,
            selectedCombatDummy, raidScaling, EncounterContext.UNKNOWN, false);
    }

    public static LiveCombatSnapshot capture(
        Client client, ItemManager itemManager, CombatStyleOverride combatStyleOverride,
        BlowpipeDart blowpipeDart, MagicSpell manualSpell, boolean applySlayerBonus,
        int toaInvocationLevel, int retainedTargetId, String retainedTargetName,
        int targetHealthRatio, int targetHealthScale, TargetEffectSnapshot targetEffectSnapshot,
        boolean targetHasLocalHits, CombatDummy selectedCombatDummy, RaidScaling raidScaling,
        EncounterContext encounterContext, boolean kandarinDiary)
    {
        return capture(client, itemManager, combatStyleOverride, blowpipeDart, manualSpell,
            applySlayerBonus, toaInvocationLevel, retainedTargetId, retainedTargetName,
            targetHealthRatio, targetHealthScale, targetEffectSnapshot, targetHasLocalHits,
            selectedCombatDummy, raidScaling, encounterContext, kandarinDiary, DefenceTrackingMode.LOCAL);
    }

    public static LiveCombatSnapshot capture(
        Client client, ItemManager itemManager, CombatStyleOverride combatStyleOverride,
        BlowpipeDart blowpipeDart, MagicSpell manualSpell, boolean applySlayerBonus,
        int toaInvocationLevel, int retainedTargetId, String retainedTargetName,
        int targetHealthRatio, int targetHealthScale, TargetEffectSnapshot targetEffectSnapshot,
        boolean targetHasLocalHits, CombatDummy selectedCombatDummy, RaidScaling raidScaling,
        EncounterContext encounterContext, boolean kandarinDiary, DefenceTrackingMode trackingMode)
    {
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return empty();
		}

		EquipmentState equipment = readEquipment(client, itemManager);
        equipment.soulreaperStacks = client.getVarpValue(VarPlayerID.SOULREAPER_STACKS);
        int tomeStacks = client.getVarpValue(5926); // ZORYAS_TOME_STACKS, RuneLite gameval
        equipment.tomeEmpowered = equipment.weaponNameLower.contains("zorya's tome") && tomeStacks > 0;
		StyleState style = getStyleState(client, equipment, manualSpell);
		if (combatStyleOverride != CombatStyleOverride.AUTO)
		{
			style = getForcedStyle(client, combatStyleOverride, equipment);
		}
		PrayerState prayer = getPrayerState(client);
		List<String> appliedEffects = new ArrayList<>();
        int soulStrengthBonus = SoulreaperRules.strengthBonus(client,equipment.weaponNameLower,client.getBoostedSkillLevel(Skill.STRENGTH));
        if(style.combatStyle == CombatStyle.MELEE && soulStrengthBonus > 0)
            appliedEffects.add("Soulreaper: " + equipment.soulreaperStacks + " Soul Stacks; +" + soulStrengthBonus + " effective Strength (basic attacks)");
		List<String> warnings = new ArrayList<>(CalculationInputNotes.forSelection(
            combatStyleOverride, manualSpell, style.combatStyle, equipment.usesSpellbookMagic(),
            equipment.isBlowpipe(), blowpipeDart));

		boolean combatDummy = selectedCombatDummy != null;
		int targetId = combatDummy ? selectedCombatDummy.getNpcId() : retainedTargetId;
		String targetName = combatDummy
			? selectedCombatDummy.getDisplayName()
			: retainedTargetName == null ? "" : retainedTargetName;
		if (targetId >= 0 && targetName.trim().isEmpty())
		{
			targetName = "NPC #" + targetId;
		}
		TargetProfile target = combatDummy
			? selectedCombatDummy.getTargetProfile()
			: targetId < 0 ? null : raidScaling.applyTombs(raidScaling.apply(EncounterResolver.resolve(targetId, encounterContext)),toaInvocationLevel);
		String raidNotice = raidScaling.notice(target);
		if (!raidNotice.isEmpty()) warnings.add(raidNotice);
        String encounterNotice = EncounterResolver.notice(targetId, encounterContext);
        if (!combatDummy && !encounterNotice.isEmpty()) warnings.add(encounterNotice);
        int archiveMaximumHp=ArchiveHealth.maximum(targetId,encounterContext.archive.manualPartySize);
        if (!combatDummy && ArchiveEncounterRules.isArchive(targetId) && encounterContext.observedMaximumHp<=0 && archiveMaximumHp<=0)
            warnings.add("Archive HP scaling unavailable; HP-dependent damage withheld unless a published party table or the boss HUD identifies this target's health.");
        if(!combatDummy && ArchiveEncounterRules.isArchive(targetId) && archiveMaximumHp>0 && encounterContext.observedMaximumHp<=0)
            warnings.add("Archive HP: published table; manual starting party="+encounterContext.archive.manualPartySize);
        if (BoltEffect.enchanted(equipment.ammoNameLower))
            warnings.add("Bolt effects: " + equipment.ammoNameLower + "; Kandarin hard diary " + (kandarinDiary ? "on" : "off")
                + (equipment.ammoNameLower.contains("ruby") ? "; current target HP is estimated" : ""));

		boolean targetDataAvailable = target != null && !target.isAmbiguous();
		TargetEffectSnapshot liveTargetEffects = !combatDummy && targetEffectSnapshot != null
			&& targetEffectSnapshot.appliesTo(targetId)
			? targetEffectSnapshot
			: TargetEffectSnapshot.empty();
        if (!combatDummy && trackingMode == DefenceTrackingMode.OFF)
            liveTargetEffects = TargetEffectSnapshot.unmodified(target).withTektonHistory(
                liveTargetEffects.isTektonDefenceSpecialAttempted(),
                liveTargetEffects.isTektonDefenceSpecialAttempted());
        boolean guardianMagicUnknown = (targetId == 16659 || targetId == 16660)
            && liveTargetEffects.hasTargetStats()
            && (liveTargetEffects.getCurrentMagic() != liveTargetEffects.getBaseMagic()
                || liveTargetEffects.getCurrentMagicDefence() != liveTargetEffects.getBaseMagicDefence());
        if (guardianMagicUnknown)
        {
            targetDataAvailable = false;
            liveTargetEffects = TargetEffectSnapshot.empty();
        }
		int targetMaximumHitpoints = encounterContext.observedMaximumHp>0 ? encounterContext.observedMaximumHp
            : targetDataAvailable ? (ArchiveEncounterRules.isArchive(targetId) ? archiveMaximumHp : Math.max(0,target.getHitpoints())) : 0;
		int baseTargetMagicDefenceRoll = targetDataAvailable
			? targetMagicDefenceRoll(target, targetId, toaInvocationLevel)
			: -1;
		if (targetDataAvailable && liveTargetEffects.hasTargetStats())
		{
			target = liveTargetEffects.applyTo(target);
		}
		int currentTargetMagicDefenceRoll = targetDataAvailable
			? targetMagicDefenceRoll(target, targetId, toaInvocationLevel)
			: -1;
		int targetCurrentHitpoints = encounterContext.observedMaximumHp>0 ? encounterContext.observedCurrentHp
            : estimateTargetCurrentHitpoints(targetHealthRatio,targetHealthScale,targetMaximumHitpoints);
		if (TargetMechanics.isInTombsOfAmascut(targetId))
		{
			warnings.add("ToA invocation input: " + Math.max(0, toaInvocationLevel));
            warnings.add("ToA "+raidScaling.tombsInputs()+"; HP is estimated from the visible health bar");
            if(targetMaximumHitpoints<=0) warnings.add("Set ToA party size and current path level for HP-dependent damage");
		}
		if (targetId >= 0 && target == null)
		{
			warnings.add("No bundled combat data for NPC #" + targetId);
		}
		else if (target != null && target.isAmbiguous())
		{
			warnings.add("This NPC ID has multiple defensive variants");
		}

		int boostedAttack = client.getBoostedSkillLevel(Skill.ATTACK);
		int boostedStrength = client.getBoostedSkillLevel(Skill.STRENGTH);
		int boostedRanged = client.getBoostedSkillLevel(Skill.RANGED);
		int boostedMagic = client.getBoostedSkillLevel(Skill.MAGIC);
		int rangedStrengthBonus = equipment.rangedStrength;
		int attackSpeedTicks = getAttackSpeedTicks(equipment, style, manualSpell, targetId);
		int effectiveDamageLevel = 0;
		int magicBaseMaxHit = 0;
		int specialMagicMaximumHit = -1;
		int minimumHit = 0;
		int maxHit = 0;
        int fangTrueMaximum = -1;
        int ralosTrueMaximum = -1;
		boolean maxHitAvailable = true;
		String maxHitStatusText = "";
		String weaponOrSpellName = equipment.weaponName;

		boolean slayerInputApplies = slayerInputApplies(applySlayerBonus, selectedCombatDummy);
		boolean eligibleSlayerGear = equipment.hasSlayerDamageBonus(style.combatStyle, equipment.isAtlatl());
		boolean useSlayerBonus = slayerInputApplies && eligibleSlayerGear;
		boolean slayerInputUsedByWeapon = equipment.isEnhancedSlayerStaff()
			&& manualSpell == MagicSpell.MAGIC_DART
			&& slayerInputApplies;
		boolean eligibleSlayerAccuracyGear = equipment.hasSlayerAccuracyBonus(style.combatStyle);
		boolean useAccuracySlayerBonus = slayerInputApplies
			&& eligibleSlayerAccuracyGear;
		if (applySlayerBonus && combatDummy && !slayerInputApplies)
		{
			warnings.add("Slayer bonus does not apply to this dummy");
		}
		else if (slayerInputApplies && !eligibleSlayerGear && !slayerInputUsedByWeapon)
		{
			warnings.add(style.combatStyle == CombatStyle.MELEE
				? "Slayer bonus needs an eligible helm or mask"
				: "Ranged/Magic Slayer bonus needs an imbued helm or mask");
		}
		else if (!applySlayerBonus && eligibleSlayerGear)
		{
			warnings.add("Slayer bonus is off in Calculation inputs");
		}
		else if (slayerInputApplies)
		{
			warnings.add("Slayer target is being manually assumed");
		}
		if (slayerInputApplies && equipment.isAtlatl()
			&& eligibleSlayerGear && !eligibleSlayerAccuracyGear)
		{
			warnings.add("Atlatl accuracy needs an imbued helm or mask");
		}

		switch (style.combatStyle)
		{
			case MELEE:
				effectiveDamageLevel = applyDamagePrayer(
					boostedStrength,
					prayer.meleeStrengthMultiplier,
					prayer.meleeStrengthMinimumOne)
					+ SoulreaperRules.strengthBonus(client, equipment.weaponNameLower, boostedStrength)
					+ style.damageStyleBonus + 8;
				if (equipment.hasMeleeVoid())
				{
					effectiveDamageLevel = (int) Math.floor(effectiveDamageLevel * 1.10);
                    appliedEffects.add("Melee Void: effective Strength x1.10");
				}

				maxHit = baseMaxHit(effectiveDamageLevel, equipment.meleeStrength);
				if (targetDataAvailable && equipment.hasApplicableMeleeSalve(target))
				{
					maxHit = scale(maxHit, equipment.getSalveNumerator(), equipment.getSalveDenominator());
				}
				else if (useSlayerBonus)
				{
					maxHit = scale(maxHit, 7, 6);
				}
				if (equipment.hasFullDharok())
				{
					int currentHitpoints = client.getBoostedSkillLevel(Skill.HITPOINTS);
					int maximumHitpoints = client.getRealSkillLevel(Skill.HITPOINTS);
					int missingHitpoints = Math.max(0, maximumHitpoints - currentHitpoints);
					double dharokModifier = 1.0
						+ (missingHitpoints / 100.0) * (maximumHitpoints / 100.0);
					maxHit = (int) Math.floor(maxHit * dharokModifier);
				}
				maxHit = (int) Math.floor(maxHit * equipment.getObsidianDamageModifier());
				if (targetDataAvailable)
				{
					maxHit = applyMeleeTargetDamage(maxHit, equipment, target);
				}
				if (style.attackType == AttackType.CRUSH && equipment.inquisitorPoints > 0)
				{
					maxHit = scale(maxHit, 200 + equipment.inquisitorPoints, 200);
				}
				if (equipment.isFang())
				{
                    fangTrueMaximum = maxHit;
					int shrink = maxHit * 3 / 20;
					minimumHit = shrink;
					maxHit -= shrink;
				}
				if (equipment.weaponNameLower.contains("obligator") && targetDataAvailable)
				{
					maxHit = Obligator.maximum(maxHit, target.getSize(), equipment.isObligatorThrust());
					appliedEffects.add(equipment.isObligatorThrust()
						? "Obligator Thrust: size bonus, highest of 2–4 damage rolls"
						: "Obligator Smash: one damage roll, 3 ticks");
				}
				break;

			case RANGED:
				if (equipment.isBlowpipe())
				{
					if (!blowpipeDart.isSet())
					{
						maxHitAvailable = false;
						maxHitStatusText = "Set dart";
						warnings.add("Choose the stored blowpipe dart in Calculation inputs");
					}
					else
					{
						rangedStrengthBonus += blowpipeDart.getRangedStrength();
						weaponOrSpellName = equipment.weaponName + " + " + blowpipeDart;
					}
				}

				int rangedDamageLevel = equipment.isAtlatl() ? boostedStrength : boostedRanged;
				int rangedDamageBonus = equipment.isAtlatl() ? equipment.meleeStrength : rangedStrengthBonus;
				effectiveDamageLevel = applyDamagePrayer(
					rangedDamageLevel,
					prayer.rangedStrengthMultiplier,
					prayer.rangedStrengthMinimumOne)
					+ style.damageStyleBonus + 8;
				double rangedVoidModifier = equipment.getRangedVoidModifier();
				if (rangedVoidModifier > 1.0)
				{
					effectiveDamageLevel = (int) Math.floor(effectiveDamageLevel * rangedVoidModifier);
                    appliedEffects.add("Ranged Void: effective Ranged x" + rangedVoidModifier);
				}

				if (maxHitAvailable)
				{
					maxHit = baseMaxHit(effectiveDamageLevel, rangedDamageBonus);
					if (equipment.getCrystalDamageModifier() > 1.0)
					{
						maxHit = (int) Math.floor(maxHit * equipment.getCrystalDamageModifier());
					}
					if (targetDataAvailable && equipment.hasApplicableRangedSalve(target))
					{
						maxHit = scale(maxHit, equipment.getSalveNumerator(), equipment.getSalveDenominator());
					}
					else if (useSlayerBonus)
					{
						maxHit = equipment.isAtlatl() ? scale(maxHit, 7, 6) : scale(maxHit, 23, 20);
					}
					if (targetDataAvailable)
					{
						maxHit = applyRangedTargetDamage(maxHit, equipment, target);
					}
				}

                if(equipment.weaponNameLower.contains("tonalztics of ralos") && maxHitAvailable)
                {
                    ralosTrueMaximum=maxHit;
                    maxHit-=maxHit/4;
                }
				if (equipment.isTwistedBow())
				{
					if (!targetDataAvailable)
					{
						maxHitAvailable = false;
						maxHitStatusText = targetStatus(targetId, target);
						warnings.add("Twisted bow needs a supported target Magic level");
					}
					else
					{
						maxHit = twistedBowScale(maxHit, target, false);
					}
				}
				break;

			case MAGIC:
			default:
				MagicBase magicBase = getMagicBase(
					equipment.weaponNameLower,
					boostedMagic,
					manualSpell,
					targetDataAvailable,
					slayerInputApplies);
				magicBaseMaxHit = magicBase.baseMaxHit;
				if (magicBase.poweredWeapon)
				{
					weaponOrSpellName = equipment.weaponName;
				}
				else if (manualSpell.isSet())
				{
					weaponOrSpellName = manualSpell.toString();
				}

				if (!magicBase.supported)
				{
					maxHitAvailable = false;
					maxHitStatusText = magicBase.statusText;
					warnings.add(magicBase.warning);
					break;
				}

				if (!magicBase.poweredWeapon && manualSpell.getSpellType() == MagicSpell.SpellType.BOLT
					&& equipment.chaosGauntlets)
				{
					magicBaseMaxHit += 3;
				}

                int chargeBonus=SpellEquipmentRules.chargeBonus(manualSpell,equipment.capeName,
                    client.getVarpValue(VarPlayerID.MAGEARENA_CHARGE)>0,!magicBase.poweredWeapon);
                magicBaseMaxHit+=chargeBonus;
                if(chargeBonus>0) appliedEffects.add("Charge: matching god cape, +10 base damage");

                int elementalUpgrade = magicBase.poweredWeapon ? 0 : ElementalUpgrade.bonus(client, manualSpell);
                if (elementalUpgrade > 0)
                {
                    magicBaseMaxHit += elementalUpgrade;
                    appliedEffects.add("Elemental fragment: +2 base Magic max hit (" + manualSpell.getElementName() + ")");
                }

				float targetAdjustedMagicDamage = equipment.magicDamage;
				if (magicBase.shadow)
				{
					int shadowFactor = TargetMechanics.isInTombsOfAmascut(targetId) ? 4 : 3;
					targetAdjustedMagicDamage = Math.min(100.0f, targetAdjustedMagicDamage * shadowFactor);
                    appliedEffects.add("Shadow: equipment magic damage x" + shadowFactor + " (100% cap)");
				}
				double additiveMagicDamage = targetAdjustedMagicDamage / 100.0
					+ prayer.magicDamagePercent / 100.0;
				if (equipment.hasEliteMageVoid())
				{
					additiveMagicDamage += 0.05;
                    appliedEffects.add("Elite magic Void: +5% magic damage");
				}
				if (!magicBase.poweredWeapon && manualSpell.getSpellbook() == MagicSpell.Spellbook.STANDARD
					&& equipment.smokeStaff)
				{
					additiveMagicDamage += 0.10;
				}
				if (!magicBase.poweredWeapon && manualSpell.getSpellbook() == MagicSpell.Spellbook.ANCIENT)
				{
					additiveMagicDamage += equipment.virtusPieces * 0.03;
				}

				int elementalBaseMax = magicBaseMaxHit;
				if (targetDataAvailable && equipment.hasApplicableMagicSalve(target))
				{
					additiveMagicDamage += equipment.salveEnhanced ? 0.20 : 0.15;
				}

				int specialMagicBase = -1;
				if (equipment.weaponNameLower.contains("eye of ayak"))
				{
					specialMagicBase = scale(magicBaseMaxHit, 13, 10);
				}
				else if (equipment.weaponNameLower.contains("accursed sceptre"))
				{
					specialMagicBase = scale(magicBaseMaxHit, 3, 2);
				}

				maxHit = (int) Math.floor(magicBaseMaxHit * (1.0 + additiveMagicDamage));
				if (specialMagicBase >= 0)
				{
					specialMagicMaximumHit = (int) Math.floor(
						specialMagicBase * (1.0 + additiveMagicDamage));
				}
				if (!(targetDataAvailable && equipment.hasApplicableMagicSalve(target)) && useSlayerBonus)
				{
					maxHit = scale(maxHit, 23, 20);
					if (specialMagicMaximumHit >= 0)
					{
						specialMagicMaximumHit = scale(specialMagicMaximumHit, 23, 20);
					}
				}
				if (targetDataAvailable)
				{
					maxHit = applyMagicTargetDamage(maxHit, equipment, target);
					if (specialMagicMaximumHit >= 0)
					{
						specialMagicMaximumHit = applyMagicTargetDamage(
							specialMagicMaximumHit, equipment, target);
					}
					if (!magicBase.poweredWeapon && manualSpell.isSet()
						&& manualSpell.getElementName().equals(target.getWeaknessElement()))
					{
						maxHit += elementalBaseMax * target.getWeaknessSeverity() / 100;
					}
				}
                int beforeTome=maxHit;
                maxHit=SpellEquipmentRules.damage(maxHit,manualSpell,equipment.shieldId,!magicBase.poweredWeapon);
                if(maxHit!=beforeTome) appliedEffects.add("Charged matching elemental tome: damage x1.1");
				if (equipment.weaponNameLower.contains("dawnbringer"))
				{
					maxHit = Math.max(2, maxHit / 2);
				}
                if (equipment.weaponNameLower.contains("zorya's tome"))
                {
                    specialMagicMaximumHit = ZoryaTome.empoweredMaximum(maxHit);
                    if (equipment.tomeEmpowered)
                    {
                        maxHit = specialMagicMaximumHit;
                        appliedEffects.add("Zorya's tome: " + tomeStacks + " empowered attacks remaining; damage x1.6, accuracy x2.5, 2 ticks");
                    }
                }
				effectiveDamageLevel = boostedMagic;

				if (magicBase.sanguinesti)
				{
					warnings.add("DPS includes Sanguinesti's 20% extra 8-damage proc");
				}
				if (!magicBase.poweredWeapon && !manualSpell.getElementName().isEmpty())
				{
					if (!targetDataAvailable)
					{
						warnings.add("Elemental weakness needs supported target data");
					}
					else if (!manualSpell.getElementName().equals(target.getWeaknessElement())
						&& !target.getWeaknessElement().isEmpty())
					{
						warnings.add("Target is weak to " + target.getWeaknessElement() + " spells");
					}
				}
				if (!magicBase.poweredWeapon && manualSpell.getSpellType() == MagicSpell.SpellType.TARGET_DEPENDENT)
				{
					warnings.add("This spell has a target-dependent modifier");
				}
				break;
		}

        String equipmentStatus = ArchiveEquipmentRules.unresolved(equipment.weaponNameLower, equipment.ammoNameLower, tomeStacks);
        if (equipment.weaponNameLower.contains("obligator")
            && (equipment.obligatorStyleIndex < 0 || equipment.obligatorStyleIndex > 2))
            equipmentStatus = "Obligator style unavailable";
        if (!equipmentStatus.isEmpty())
        {
            maxHitAvailable = false;
            maxHitStatusText = equipmentStatus;
            warnings.add(equipmentStatus);
        }
        if (equipment.rondacheEquipped)
        {
            int charges = client.getVarbitValue(16438);
            int shieldMaximum = Rondache.baseMaximum(charges);
            if (shieldMaximum >= 0)
                appliedEffects.add("Rondache: " + charges + " charges; base shield-bash max " + shieldMaximum
                    + " before target mitigation. Separate from mainhand special.");
            warnings.add(shieldMaximum < 0 ? "Rondache charges unavailable"
                : "Rondache shield-bash accuracy/DPS pending; charge maximum is in Inspector");
        }
        String archiveStatus = guardianMagicUnknown ? "Guardian Magic drain pending"
            : combatDummy ? "" : ArchiveEncounterRules.unresolved(targetId, style.attackType);
        if(targetId==16640 && targetDataAvailable)
        {
            archiveStatus="";
            appliedEffects.add(encounterContext.borkStunned ? "Bork: observed stun; armour 0, guaranteed melee accuracy"
                : "Bork: Smorks HP quartile "+encounterContext.borkQuartile+"; armour "+target.getFlatArmour());
        }
        if(targetDataAvailable && (targetId==16676 || targetId>=16678 && targetId<=16681)) archiveStatus="";
        if (archiveStatus.isEmpty() && !combatDummy)
            archiveStatus = RaidDamageRules.unresolvedEncounter(targetId);
        if(!combatDummy && targetId==16652 && encounterContext.archive.ezekielEnraged)
            archiveStatus="Ezekiel enrage damage curve unverified";
        boolean moiaShieldUnknown=!combatDummy && targetId==16667 && client.getVarbitValue(16418)>0;
        if(moiaShieldUnknown)archiveStatus="Moia shadow shield damage unverified";
        if(!combatDummy && targetId==16662 && style.combatStyle==CombatStyle.MAGIC
            && equipment.usesSpellbookMagic() && "earth".equals(manualSpell.getElementName()))
            archiveStatus="Inert guardian earth weakness unverified";
        boolean enchantedMaximumUnknown=!combatDummy && style.attackType.isRanged()
            && TargetMechanics.isGuaranteedMaximum(targetId,style.attackType)
            && equipment.weaponNameLower.contains("crossbow") && BoltEffect.enchanted(equipment.ammoNameLower);
        if(enchantedMaximumUnknown)archiveStatus="Guaranteed maximum with enchanted bolts unverified";
        boolean impBuffUnknown=!combatDummy && targetId>=16643 && targetId<=16650 && encounterContext.archive.impBuffUncertain;
        if(impBuffUnknown)archiveStatus="Imp next-attack buff unverified";
        if (!combatDummy && TargetMechanics.isWardenP2(targetId) && !style.attackType.isMelee()
            && (encounterContext==null || encounterContext.protectionMask<0))
            archiveStatus="Warden protection unavailable";
        if (!combatDummy && TargetMechanics.isWardenCore(targetId) && !style.attackType.isMelee())
            archiveStatus="Warden core non-melee stats pending";
        if (!archiveStatus.isEmpty())
        {
            maxHitAvailable = false;
            maxHitStatusText = archiveStatus;
            warnings.add(archiveStatus);
        }
        boolean archiveAncientImmune = !combatDummy && style.combatStyle == CombatStyle.MAGIC
            && ArchiveEncounterRules.ancientImmune(targetId, equipment.usesSpellbookMagic(), manualSpell);
		addConditionalWeaponWarnings(equipment, style.attackType, target, targetDataAvailable, warnings);
		if (targetDataAvailable && isTargetImmune(targetId, style, equipment, target))
		{
			warnings.add("Target is immune to " + style.attackType.toString().toLowerCase(Locale.ROOT));
		}
		if (equipment.salveEquipped && !targetDataAvailable)
		{
			warnings.add("Salve bonuses need supported target attributes");
		}

        HitTransform encounterDamage = combatDummy ? HitTransform.IDENTITY : RaidDamageRules.transform(
            targetId, style.attackType, RaidDamageRules.bypassesIceDemon(style.combatStyle, equipment.weaponNameLower,
                equipment.usesSpellbookMagic() && "fire".equals(manualSpell.getElementName()),
                equipment.usesSpellbookMagic() && manualSpell.isDemonbane()),
            equipment.weaponNameLower, client.getRealSkillLevel(Skill.MINING));

        if(!combatDummy && targetId==16640 && style.attackType==AttackType.MAGIC && targetDataAvailable)
            encounterDamage=HitTransform.flatReduction(Math.max(0,target.getFlatArmour()-8));
        if (!combatDummy && targetDataAvailable && !isTargetImmune(targetId,style,equipment,target))
        {
            if (TargetMechanics.isZulrah(targetId)) appliedEffects.add("Zulrah: each hitsplat above 50 rerolls 45–50");
            if (EncounterResolver.isNightmarePillar(targetId) && style.attackType == AttackType.MAGIC)
                appliedEffects.add("Nightmare pillar: magic damage x2 per hitsplat");
        }
		maxHit = Math.min(GLOBAL_DAMAGE_CAP, Math.max(0, maxHit));
		minimumHit = Math.min(maxHit, Math.max(0, minimumHit));
		int displayedAttackBonus = equipment.attackBonus(style.attackType);
		float displayedMagicDamage = equipment.magicDamage;
		if (style.combatStyle == CombatStyle.MAGIC && equipment.isShadow() && targetDataAvailable)
		{
			int shadowFactor = TargetMechanics.isInTombsOfAmascut(targetId) ? 4 : 3;
			displayedAttackBonus *= shadowFactor;
			displayedMagicDamage = Math.min(100.0f, displayedMagicDamage * shadowFactor);
		}
		TargetResult targetResult = calculateTargetResult(
			style,
			equipment,
			prayer,
			targetId,
			target,
			boostedAttack,
			boostedRanged,
			boostedMagic,
			manualSpell,
			useAccuracySlayerBonus,
			toaInvocationLevel,
			minimumHit,
			maxHit,
			maxHitAvailable,
			maxHitStatusText,
			attackSpeedTicks,
			combatDummy, encounterDamage, targetCurrentHitpoints, kandarinDiary, client.getBoostedSkillLevel(Skill.HITPOINTS));
		boolean targetImmune = archiveAncientImmune || targetDataAvailable && isTargetImmune(targetId, style, equipment, target);
        if (archiveAncientImmune || targetId==16670 && style.combatStyle==CombatStyle.MAGIC
            && equipment.usesSpellbookMagic() && manualSpell==MagicSpell.CRUMBLE_UNDEAD)
        { targetResult = TargetResult.immune(0, 0, 0);targetImmune=true; }
        if (!archiveStatus.isEmpty()) targetResult = TargetResult.unavailable(archiveStatus);
        if(!combatDummy && targetId==16677 && encounterContext.ocellusFinalPhase)
        {
            maxHitAvailable=false;maxHitStatusText="Ocellus final-phase damage/timing unverified";
            targetResult=TargetResult.unavailable(maxHitStatusText);
        }
        if(!combatDummy && targetId==16677 && encounterContext.ocellusAirborne && style.combatStyle==CombatStyle.MELEE)
        { targetResult=TargetResult.immune(0,0,0);targetImmune=true; }
        if (!combatDummy && encounterContext!=null && encounterContext.protects(style.attackType)
            && (TargetMechanics.isWardenP2(targetId) || targetId==16674 || targetId==16677))
        {
            targetResult=TargetResult.immune(0,0,0);
            targetImmune=true;
        }
        boolean voidwaker = equipment.weaponNameLower.contains("voidwaker");
        boolean soulreaper = equipment.weaponNameLower.equals("soulreaper axe") || equipment.weaponNameLower.equals("soulreaper axe (o)");
        int specialBaseMaximum = ralosTrueMaximum>=0 ? ralosTrueMaximum : fangTrueMaximum >= 0 ? fangTrueMaximum : maxHit;
        if (voidwaker || soulreaper)
        {
            StyleState meleeStyle = getStyleState(client,equipment,MagicSpell.NOT_SET);
            specialBaseMaximum = voidwakerBaseMaximum(equipment,prayer,boostedStrength,
                meleeStyle.combatStyle == CombatStyle.MELEE ? meleeStyle.damageStyleBonus : 0,
                targetDataAvailable ? target : null,
                slayerInputApplies && equipment.hasSlayerDamageBonus(CombatStyle.MELEE,false));
            if (soulreaper && style.attackType == AttackType.CRUSH && equipment.inquisitorPoints > 0)
                specialBaseMaximum = scale(specialBaseMaximum,200+equipment.inquisitorPoints,200);
        }
		SpecialAttackResult specialAttackResult = calculateSpecialAttackResult(
			style,
			equipment,
			prayer,
			targetId,
			targetName,
			target,
			targetDataAvailable,
			liveTargetEffects,
			boostedAttack,
			boostedRanged,
			boostedMagic,
			manualSpell,
			useAccuracySlayerBonus,
			toaInvocationLevel,
			specialBaseMaximum,
			voidwaker || maxHitAvailable,
			specialMagicMaximumHit,
			targetHasLocalHits,
			combatDummy, targetCurrentHitpoints, kandarinDiary, client.getBoostedSkillLevel(Skill.HITPOINTS));

        if (guardianMagicUnknown)
        {
            SpecialAttackWeapon special = SpecialAttackWeapon.forWeapon(equipment.weaponNameLower);
            if (special != null) specialAttackResult = SpecialAttackResult.unavailable(special, archiveStatus);
        }

        if(moiaShieldUnknown && specialAttackResult.getWeapon()!=null)
            specialAttackResult=SpecialAttackResult.unavailable(specialAttackResult.getWeapon(),"Moia shadow shield damage unverified");
        if(impBuffUnknown && specialAttackResult.getWeapon()!=null)
            specialAttackResult=SpecialAttackResult.unavailable(specialAttackResult.getWeapon(),"Imp next-attack buff unverified");
        if(!combatDummy && targetId==16652 && encounterContext.archive.ezekielEnraged && specialAttackResult.getWeapon()!=null)
            specialAttackResult=SpecialAttackResult.unavailable(specialAttackResult.getWeapon(),"Ezekiel enrage damage curve unverified");
        if(!combatDummy && targetId==16677 && specialAttackResult.getWeapon()!=null)
        {
            if(encounterContext.ocellusFinalPhase)
                specialAttackResult=SpecialAttackResult.unavailable(specialAttackResult.getWeapon(),"Ocellus final-phase damage/timing unverified");
            if(encounterContext.ocellusAirborne && specialNeedsMeleeRange(specialAttackResult.getWeapon()))
                specialAttackResult=SpecialAttackResult.immune(specialAttackResult.getWeapon());
        }
        if(!combatDummy && specialAttackResult.getWeapon()!=null && encounterContext.protects(specialAttackResult.getWeapon().getAttackType())
            && (TargetMechanics.isWardenP2(targetId) || targetId==16674 || targetId==16677))
            specialAttackResult=SpecialAttackResult.immune(specialAttackResult.getWeapon());
		return new LiveCombatSnapshot(
			true,
			style.combatStyle,
			style.attackType,
			boostedAttack,
			boostedStrength,
			boostedRanged,
			boostedMagic,
			style.combatStyle == CombatStyle.MELEE ? displayedAttackBonus : equipment.bestMeleeAttackBonus(),
			equipment.meleeStrength,
			style.combatStyle == CombatStyle.RANGED ? displayedAttackBonus : equipment.rangedAttack,
			rangedStrengthBonus,
			style.combatStyle == CombatStyle.MAGIC ? displayedAttackBonus : equipment.magicAttack,
			displayedMagicDamage,
			attackSpeedTicks,
			style.damageStyleBonus,
			effectiveDamageLevel,
			magicBaseMaxHit,
			style.name,
			weaponOrSpellName,
			prayer.name,
			prayer.damageMultiplier(style.combatStyle),
			prayer.affects(style.combatStyle),
			equipment.slayerHelmet || equipment.blackMask,
			equipment.isAtlatl(),
			combatDummy,
			targetName,
			targetId,
			targetCurrentHitpoints,
			targetMaximumHitpoints,
			maxHitAvailable,
			maxHit,
			maxHitStatusText,
			targetResult,
			specialAttackResult,
			liveTargetEffects,
			baseTargetMagicDefenceRoll,
			currentTargetMagicDefenceRoll,
			targetImmune,
			warnings, "Equipment slot:ID " + equipment.itemIds
                + "; active ammo=" + equipment.ammoNameLower + "; " + raidScaling.inputSummary()
                + "; ToA invocation=" + toaInvocationLevel + "; Slayer toggle=" + applySlayerBonus
                + "; style override=" + combatStyleOverride + "; manual spell=" + manualSpell
                + "; blowpipe dart=" + blowpipeDart + "; Kandarin diary=" + kandarinDiary
                + "; defence tracking=" + trackingMode + "; Party Hub import=none"
                + "; DT2 mode=" + encounterContext.mode + "; observed target combat=" + encounterContext.combatLevel
                + "; NPC size="+encounterContext.npcSize+"; protection="+encounterContext.protectionMask
                + "; Bork stunned="+encounterContext.borkStunned+"; Smorks quartile="+encounterContext.borkQuartile
                + "; Ocellus airborne="+encounterContext.ocellusAirborne+"; final phase="+encounterContext.ocellusFinalPhase
                + "; Archive observed roster="+encounterContext.archive.partySize+"; manual starting party="+encounterContext.archive.manualPartySize+"; Ezekiel enrage="+encounterContext.archive.ezekielEnraged
                + "; Imp buff unresolved="+encounterContext.archive.impBuffUncertain
                + "; observed boss HP="+encounterContext.observedCurrentHp+"/"+encounterContext.observedMaximumHp,
                combatStyleOverride != CombatStyleOverride.AUTO || combatDummy
                    || (style.combatStyle == CombatStyle.MAGIC && equipment.usesSpellbookMagic() && manualSpell.isSet())
                    || (style.combatStyle == CombatStyle.RANGED && equipment.isBlowpipe() && blowpipeDart.isSet())
                    || (slayerInputApplies && (useSlayerBonus || useAccuracySlayerBonus))
                    || (!combatDummy && ArchiveEncounterRules.isArchive(targetId) && encounterContext.archive.manualPartySize>0)
                    || (encounterContext.mode != EncounterMode.AUTO && targetId >= 12191 && targetId <= 12228)
                    || (targetDataAvailable && (target.hasAttribute("xerician") || (target.hasAttribute("theatre_of_blood") && raidScaling.hasManualTheatreSize())
                        || TargetMechanics.isInTombsOfAmascut(targetId))), appliedEffects);
	}

	private static SpecialAttackResult calculateSpecialAttackResult(
		StyleState currentStyle,
		EquipmentState equipment,
		PrayerState prayer,
		int targetId,
		String targetName,
		TargetProfile target,
		boolean targetDataAvailable,
		TargetEffectSnapshot targetEffects,
		int boostedAttack,
		int boostedRanged,
		int boostedMagic,
		MagicSpell manualSpell,
		boolean useAccuracySlayerBonus,
		int toaInvocationLevel,
		int baseMaximumHit,
		boolean baseMaximumAvailable,
		int specialMagicMaximumHit,
		boolean targetHasLocalHits,
		boolean combatDummy, int currentHp, boolean diary, int playerHp)
	{
        targetDataAvailable = targetDataAvailable && target != null && !target.isAmbiguous();
		SpecialAttackWeapon weapon = SpecialAttackWeapon.forWeapon(equipment.weaponNameLower);
        if (!combatDummy && TargetMechanics.isWardenP2(targetId) && weapon!=null)
            return SpecialAttackResult.unavailable(weapon,"Warden P2 special distribution pending");
        if (!combatDummy && TargetMechanics.isWardenCore(targetId) && weapon!=null && !weapon.getAttackType().isMelee())
            return SpecialAttackResult.unavailable(weapon,"Warden core non-melee stats pending");
        if (weapon == null) return SpecialAttackResult.noWeapon();
        // Reach belongs to the weapon, independently of the special's damage type.
        if(!combatDummy && specialNeedsMeleeRange(weapon)
            && (targetId==16667 || target!=null && target.hasAttribute("flying")
                && (TargetMechanics.isVespula(targetId) || equipment.weaponCategory!=WeaponCategory.POLEARM)))
            return SpecialAttackResult.immune(weapon);
        if(!combatDummy && weapon==SpecialAttackWeapon.ZARYTE_CROSSBOW
            && TargetMechanics.isGuaranteedMaximum(targetId,AttackType.RANGED_HEAVY)
            && BoltEffect.enchanted(equipment.ammoNameLower))
            return SpecialAttackResult.unavailable(weapon,"Guaranteed maximum with enchanted bolts unverified");
        if(weapon==SpecialAttackWeapon.TONALZTICS_OF_RALOS && equipment.weaponNameLower.contains("uncharged"))
            return SpecialAttackResult.unavailable(weapon,"Charge Ralos");
        if (weapon != SpecialAttackWeapon.VOIDWAKER && currentStyle.combatStyle != weapon.getCombatStyle())
            return SpecialAttackResult.unavailable(weapon,"Special requires " + weapon.getCombatStyle().toString().toLowerCase(Locale.ROOT) + " inputs");
		String archiveStatus = combatDummy ? "" : ArchiveEncounterRules.unresolved(targetId, weapon.getAttackType());
        if(targetDataAvailable && (targetId==16640 || targetId==16676 || targetId>=16678 && targetId<=16681)) archiveStatus="";
        if (archiveStatus.isEmpty() && !combatDummy)
            archiveStatus = RaidDamageRules.unresolvedEncounter(targetId);
        if (!archiveStatus.isEmpty()) return SpecialAttackResult.unavailable(weapon, archiveStatus);
        String raidDamageStatus = combatDummy ? "" : RaidDamageRules.unavailableReason(
			targetId, weapon.getAttackType(), false);
		if (!raidDamageStatus.isEmpty()) return SpecialAttackResult.unavailable(weapon, raidDamageStatus);

        HitTransform specialTransform = combatDummy ? HitTransform.IDENTITY
            : RaidDamageRules.transform(targetId,weapon.getAttackType(),false,equipment.weaponNameLower,1);
        if(targetId==16640 && targetDataAvailable && weapon.getAttackType()==AttackType.MAGIC)
            specialTransform=HitTransform.flatReduction(Math.max(0,target.getFlatArmour()-8));
        int baseDefenceFloor=targetDataAvailable ? TargetEffectRules.defenceFloor(targetEffects.hasTargetStats()
            ? target.withCombatStats(targetEffects.getBaseDefence(),targetEffects.getBaseMagic(),targetEffects.getBaseMagicDefence()) : target) : 0;
		int attackRoll = 0;
		int defenceRoll = 0;
		boolean specialImmune = false;
		if (targetDataAvailable)
		{
			AttackType offensiveAttackType = specialOffensiveAttackType(
				weapon,
				currentStyle.combatStyle,
				currentStyle.attackType);
			AttackType defenceAttackType = specialDefenceAttackType(
				weapon,
				currentStyle.attackType);
			StyleState offensiveSpecialStyle = new StyleState(
				weapon.getCombatStyle(),
				offensiveAttackType,
				currentStyle.name,
				currentStyle.accuracyStyleBonus,
				currentStyle.damageStyleBonus);
			StyleState defensiveSpecialStyle = new StyleState(
				weapon.getCombatStyle(),
				defenceAttackType,
				currentStyle.name,
				currentStyle.accuracyStyleBonus,
				currentStyle.damageStyleBonus);
			if (!combatDummy)
			{
				AccuracyRolls accuracyRolls = calculateAccuracyRolls(
					offensiveSpecialStyle,
					defenceAttackType,
					equipment,
					prayer,
					targetId,
					target,
					boostedAttack,
					boostedRanged,
					boostedMagic,
					manualSpell,
					useAccuracySlayerBonus,
					toaInvocationLevel);
				attackRoll = accuracyRolls.attackRoll;
				defenceRoll = accuracyRolls.defenceRoll;
			}
			specialImmune = isTargetImmune(
				targetId,
				defensiveSpecialStyle,
				equipment,
				target);
		}

        boolean tektonReduction = TargetEffectRules.isTekton(targetId, targetName)
            && (weapon == SpecialAttackWeapon.DRAGON_WARHAMMER || weapon == SpecialAttackWeapon.ELDER_MAUL);
        if (tektonReduction && !targetEffects.isTektonSpecialHistoryKnown())
            return SpecialAttackResult.unavailable(weapon, "Tekton first-special history unknown");
        boolean firstTektonReduction = tektonReduction && !targetEffects.isTektonDefenceSpecialAttempted();
		boolean guaranteedMaximum = combatDummy || targetDataAvailable
			&& TargetMechanics.isGuaranteedMaximum(targetId, weapon.getAttackType());
		int specialMaximumHitOverride = -1;
		if (weapon == SpecialAttackWeapon.EYE_OF_AYAK
			|| weapon == SpecialAttackWeapon.ACCURSED_SCEPTRE || weapon == SpecialAttackWeapon.ZORYAS_TOME)
		{
			specialMaximumHitOverride = specialMagicMaximumHit;
		}
		else if (weapon == SpecialAttackWeapon.SEERCULL
			|| weapon == SpecialAttackWeapon.MAGIC_SHORTBOW
			|| weapon == SpecialAttackWeapon.MAGIC_LONGBOW)
		{
			specialMaximumHitOverride = ammoOnlyRangedSpecialMaximumHit(
				boostedRanged,
				equipment.ammoRangedStrength);
		}
        if (weapon == SpecialAttackWeapon.ZARYTE_CROSSBOW)
        {
            if (!targetDataAvailable || !baseMaximumAvailable) return SpecialAttackResult.unavailable(weapon, "Select resolved target");
            if (specialImmune) return SpecialAttackResult.unavailable(weapon, "Immune");
            if (equipment.ammoNameLower.contains("ruby") && playerHp<=10) return SpecialAttackResult.unavailable(weapon, "Ruby: player HP too low");
            double accuracy=combatDummy || guaranteedMaximum ? 1 : CombatPrediction.hitChance(attackRoll*2,defenceRoll);
            BoltEffect.Result bolt=BoltEffect.calculate(equipment.ammoNameLower,baseMaximumHit,boostedRanged,accuracy,
                currentHp,target.getFlatArmour(),true,true,diary,target.hasAttribute("undead"),
                target.hasAttribute("dragon") || target.hasAttribute("fiery"), specialTransform);
            return bolt==null ? SpecialAttackResult.unavailable(weapon, "Bolt effect or HP unavailable") : bolt.special();
        }

        if (weapon == SpecialAttackWeapon.SOULREAPER_AXE)
            return SoulreaperSpecial.calculate(baseMaximumHit,baseMaximumAvailable,attackRoll,defenceRoll,
                targetDataAvailable,targetDataAvailable ? target.getFlatArmour() : 0,specialImmune,
                combatDummy || guaranteedMaximum,guaranteedMaximum,equipment.soulreaperStacks,specialTransform);

        if (ContextSpecialAttack.supports(weapon))
        {
            if (!baseMaximumAvailable) return SpecialAttackResult.unavailable(weapon, "Max hit unavailable");
            if (!targetDataAvailable) return SpecialAttackResult.unavailable(weapon, "Select resolved target");
            if (specialImmune) return SpecialAttackResult.unavailable(weapon, "Immune");
            return ContextSpecialAttack.calculate(weapon, baseMaximumHit, attackRoll, defenceRoll,
                target.getSize(), target.getFlatArmour(), currentStyle.attackType == AttackType.STAB,
                TargetMechanics.isInTombsOfAmascut(targetId), target.hasAttribute("demon"),
                combatDummy || guaranteedMaximum,
                AmmunitionRules.accurateDamageFloor(equipment.weaponNameLower, equipment.ammoNameLower) == 3, specialTransform);
        }

		SpecialAttackResult result = SpecialAttackCalculator.calculate(
			weapon,
			baseMaximumHit,
			baseMaximumAvailable,
			attackRoll,
			defenceRoll,
			targetDataAvailable,
			targetDataAvailable && weapon.getAttackType()!=AttackType.MAGIC ? target.getFlatArmour() : 0,
			specialImmune,
			combatDummy || firstTektonReduction || guaranteedMaximum
                || targetId==16640 && target.getFlatArmour()==0 && weapon.getAttackType().isMelee(),
			guaranteedMaximum,
			equipment.hasTwoArrowsEquipped(),
			equipment.hasDragonArrows(),
			equipment.hasArrowEquipped(),
			targetDataAvailable ? target.getDefenceLevel() : 0,
			targetDataAvailable ? target.getMagicLevel() : 0,
			baseDefenceFloor,
			specialMaximumHitOverride,
			!targetHasLocalHits,
            AmmunitionRules.accurateDamageFloor(equipment.weaponNameLower, equipment.ammoNameLower) == 3, specialTransform);
        return result;
	}

    /** Voidwaker damage is melee-derived even when the basic HUD is forced to Magic. */
    private static int voidwakerBaseMaximum(EquipmentState equipment, PrayerState prayer,
        int strength, int stanceBonus, TargetProfile target, boolean slayer)
    {
        int effective = applyDamagePrayer(strength,prayer.meleeStrengthMultiplier,
            prayer.meleeStrengthMinimumOne) + stanceBonus + 8;
        if (equipment.hasMeleeVoid()) effective = (int)Math.floor(effective * 1.10);
        int maximum = baseMaxHit(effective,equipment.meleeStrength);
        if (target != null && equipment.hasApplicableMeleeSalve(target))
            maximum = scale(maximum,equipment.getSalveNumerator(),equipment.getSalveDenominator());
        else if (slayer) maximum = scale(maximum,7,6);
        return Math.min(GLOBAL_DAMAGE_CAP,Math.max(0,maximum));
    }

	static AttackType specialOffensiveAttackType(
		SpecialAttackWeapon weapon,
		CombatStyle currentCombatStyle,
		AttackType currentAttackType)
	{
		if (weapon != null
			&& weapon.getCombatStyle() == currentCombatStyle
			&& currentAttackType != null
			&& currentAttackType != AttackType.UNKNOWN)
		{
			return currentAttackType;
		}
		return weapon == null ? AttackType.UNKNOWN : weapon.getAttackType();
	}

	static AttackType specialDefenceAttackType(
		SpecialAttackWeapon weapon,
		AttackType currentAttackType)
	{
		if ((weapon == SpecialAttackWeapon.BURNING_CLAWS || weapon == SpecialAttackWeapon.OSMUMTENS_FANG
            || weapon == SpecialAttackWeapon.SOULREAPER_AXE)
			&& (currentAttackType == AttackType.STAB
				|| currentAttackType == AttackType.SLASH
                || weapon == SpecialAttackWeapon.SOULREAPER_AXE && currentAttackType == AttackType.CRUSH))
		{
			return currentAttackType;
		}
		return weapon == null ? AttackType.UNKNOWN : weapon.getAttackType();
	}

	static int ammoOnlyRangedSpecialMaximumHit(int boostedRanged, int ammoRangedStrength)
	{
		long numerator = (long) (Math.max(0, boostedRanged) + 10)
			* (Math.max(0, ammoRangedStrength) + 64) + 320L;
		return (int) Math.max(0L, numerator / 640L);
	}

	private static TargetResult calculateTargetResult(
		StyleState style,
		EquipmentState equipment,
		PrayerState prayer,
		int targetId,
		TargetProfile target,
		int boostedAttack,
		int boostedRanged,
		int boostedMagic,
		MagicSpell manualSpell,
		boolean useAccuracySlayerBonus,
		int toaInvocationLevel,
		int minimumHit,
		int maximumHit,
		boolean maxHitAvailable,
		String maxHitStatus,
		int attackSpeedTicks,
		boolean combatDummy, HitTransform encounterDamage, int currentHp, boolean diary, int playerHp)
	{
		if (targetId < 0)
		{
			return TargetResult.noTarget();
		}
		if (target == null)
		{
			return TargetResult.unavailable("Target data unavailable");
		}
		if (target.isAmbiguous())
		{
			return TargetResult.unavailable("Target variant needed");
		}
		if (style.attackType == AttackType.UNKNOWN)
		{
			return TargetResult.unavailable("Attack type unavailable");
		}
		if (combatDummy)
		{
			return calculateDummyTargetResult(
				equipment,
				target,
				minimumHit,
				maximumHit,
				maxHitAvailable,
				maxHitStatus,
				attackSpeedTicks);
		}

		AccuracyRolls accuracyRolls = calculateAccuracyRolls(
			style,
			style.attackType,
			equipment,
			prayer,
			targetId,
			target,
			boostedAttack,
			boostedRanged,
			boostedMagic,
			manualSpell,
			useAccuracySlayerBonus,
			toaInvocationLevel);
		int attackRoll = accuracyRolls.attackRoll;
        if (style.combatStyle == CombatStyle.MAGIC && equipment.tomeEmpowered)
            attackRoll = ZoryaTome.empoweredAccuracy(attackRoll);
		int defenceRoll = accuracyRolls.defenceRoll;
		int effectiveAccuracy = accuracyRolls.effectiveAccuracy;

		if (isTargetImmune(targetId, style, equipment, target))
		{
			return TargetResult.immune(attackRoll, defenceRoll, effectiveAccuracy);
		}

		double chance;
		boolean guaranteedMaxHit = TargetMechanics.isGuaranteedMaximum(targetId, style.attackType);
		boolean fangAccuracy = equipment.isFang() && style.attackType == AttackType.STAB;
        if (guaranteedMaxHit || TargetMechanics.isWardenP2(targetId)
            || targetId==16640 && style.attackType.isMelee() && target.getFlatArmour()==0)
        {
            chance = 1.0;
		}
		else if (fangAccuracy && TargetMechanics.isInTombsOfAmascut(targetId))
		{
			double normalChance = CombatPrediction.hitChance(attackRoll, defenceRoll);
			chance = 1.0 - (1.0 - normalChance) * (1.0 - normalChance);
		}
		else if (fangAccuracy)
		{
			chance = CombatPrediction.fangHitChance(attackRoll, defenceRoll);
		}
		else
		{
			chance = CombatPrediction.hitChance(attackRoll, defenceRoll);
		}

		if (!maxHitAvailable)
		{
			return TargetResult.accuracyOnly(
				attackRoll,
				defenceRoll,
				effectiveAccuracy,
				chance,
				maxHitStatus == null || maxHitStatus.isEmpty() ? "Max hit unavailable" : maxHitStatus);
		}
		if (equipment.hasUnsupportedMultiHitDps())
		{
			return TargetResult.accuracyOnly(
				attackRoll,
				defenceRoll,
				effectiveAccuracy,
				chance,
				"Multi-hit DPS unavailable");
		}

        if (TargetMechanics.isWardenP2(targetId) && style.combatStyle!=CombatStyle.MELEE)
        {
            // Wiki: max attack roll minus one third of the scaled NPC defence roll.
            int delta=Math.max(0,attackRoll-defenceRoll/3);
            int percent=15+(int)Math.min(25,25L*delta/42000);
            minimumHit=maximumHit*percent/100;
            maximumHit=maximumHit*(percent+20)/100;
        }

        if (style.combatStyle == CombatStyle.RANGED && equipment.weaponNameLower.contains("crossbow")
            && BoltEffect.enchanted(equipment.ammoNameLower))
        {
            if(guaranteedMaxHit)return TargetResult.unavailable("Guaranteed maximum with enchanted bolts unverified");
            BoltEffect.Result bolt = (equipment.ammoNameLower.contains("ruby") && playerHp <= 10) ? null
                : BoltEffect.calculate(equipment.ammoNameLower,minimumHit,maximumHit,boostedRanged,chance,currentHp,
                    target.getFlatArmour(),equipment.weaponNameLower.contains("zaryte"),false,diary,
                    target.hasAttribute("undead"),target.hasAttribute("dragon") || target.hasAttribute("fiery"), encounterDamage);
            if(bolt==null) return TargetResult.accuracyOnly(attackRoll,defenceRoll,effectiveAccuracy,chance,
                "Bolt effect or HP unavailable");
            return TargetResult.complete(attackRoll,defenceRoll,effectiveAccuracy,bolt.chance,bolt.maximum,"",
                bolt.chance>0 ? bolt.expected/bolt.chance : 0,bolt.expected/(Math.max(1,attackSpeedTicks)*.6),attackSpeedTicks);
        }

        if (style.combatStyle == CombatStyle.RANGED
            && AmmunitionRules.accurateDamageFloor(equipment.weaponNameLower, equipment.ammoNameLower) == 3)
            encounterDamage = encounterDamage.withAccurateFloor(3);

		MultiHitWeapon multiHitWeapon = MultiHitWeapon.forWeapon(equipment.weaponNameLower);
		int targetMaximumHit;
		String maximumHitSplit = "";
		double averageSuccessfulHit;
		double damagePerAttack;
		double expectedAttackSpeedTicks = Math.max(1, attackSpeedTicks);
		if (style.combatStyle == CombatStyle.MELEE && equipment.weaponNameLower.contains("obligator"))
		{
			MultiHitResult result = Obligator.calculate(maximumHit, target.getFlatArmour(), chance,
				target.defenceBonus(AttackType.STAB), target.defenceBonus(AttackType.SLASH),
				target.defenceBonus(AttackType.CRUSH), equipment.isObligatorThrust(), guaranteedMaxHit, encounterDamage);
			targetMaximumHit = result.getTotalMaximumHit();
			averageSuccessfulHit = result.getAverageDamageOnSuccessfulAttack();
			damagePerAttack = result.getExpectedDamagePerAttack();
			expectedAttackSpeedTicks = result.getExpectedAttackSpeedTicks();
		}
		else if (multiHitWeapon != null)
		{
			MultiHitResult multiHit = multiHitWeapon.calculate(
				minimumHit,
				maximumHit,
				target.getFlatArmour(),
				chance,
				target.getSize(),
				attackSpeedTicks,
				guaranteedMaxHit,
				equipment.hasFullBloodMoonSet(), encounterDamage);
			targetMaximumHit = multiHit.getTotalMaximumHit();
			maximumHitSplit = formatHitSplit(multiHit.getMaximumHits());
			averageSuccessfulHit = multiHit.getAverageDamageOnSuccessfulAttack();
			damagePerAttack = multiHit.getExpectedDamagePerAttack();
			expectedAttackSpeedTicks = multiHit.getExpectedAttackSpeedTicks();
		}
		else
		{
			boolean applyFlatArmour = style.combatStyle != CombatStyle.MAGIC;
			int flatArmour = applyFlatArmour ? target.getFlatArmour() : 0;
			targetMaximumHit = maximumHit <= 0
				? 0
				: encounterDamage.maximum(maximumHit, flatArmour);
            averageSuccessfulHit = maximumHit <= 0 ? 0 : encounterDamage.average(
                guaranteedMaxHit ? maximumHit : minimumHit, maximumHit, flatArmour);

			if (equipment.isSanguinesti())
			{
				averageSuccessfulHit = encounterDamage.averageWithProc(guaranteedMaxHit ? maximumHit : minimumHit,
                    maximumHit, flatArmour, .2, 8);
                targetMaximumHit = encounterDamage.maxForHit(DamageRoll.maximumSuccessfulHit(maximumHit,flatArmour)+8);
			}
			damagePerAttack = averageSuccessfulHit * chance;
		}
		double damagePerSecond = damagePerAttack / (expectedAttackSpeedTicks * 0.6);
		return TargetResult.complete(
			attackRoll,
			defenceRoll,
			effectiveAccuracy,
			chance,
			targetMaximumHit,
			maximumHitSplit,
			averageSuccessfulHit,
			damagePerSecond,
			expectedAttackSpeedTicks);
	}

	private static TargetResult calculateDummyTargetResult(
		EquipmentState equipment,
		TargetProfile target,
		int minimumHit,
		int maximumHit,
		boolean maxHitAvailable,
		String maxHitStatus,
		int attackSpeedTicks)
	{
		if (!maxHitAvailable)
		{
			return TargetResult.dummy(
				-1,
				"",
				maxHitStatus == null || maxHitStatus.isEmpty()
					? "Max hit unavailable"
					: maxHitStatus);
		}

		MultiHitWeapon multiHitWeapon = MultiHitWeapon.forWeapon(equipment.weaponNameLower);
		if (multiHitWeapon == null)
		{
			return TargetResult.dummy(Math.max(0, maximumHit), "", "Dummy target");
		}

		MultiHitResult multiHit = multiHitWeapon.calculate(
			minimumHit,
			maximumHit,
			0,
			1.0,
			target.getSize(),
			attackSpeedTicks,
			true,
			equipment.hasFullBloodMoonSet());
		return TargetResult.dummy(
			multiHit.getTotalMaximumHit(),
			formatHitSplit(multiHit.getMaximumHits()),
			"Dummy target");
	}

	private static AccuracyRolls calculateAccuracyRolls(
		StyleState style,
		AttackType defenceAttackType,
		EquipmentState equipment,
		PrayerState prayer,
		int targetId,
		TargetProfile target,
		int boostedAttack,
		int boostedRanged,
		int boostedMagic,
		MagicSpell manualSpell,
		boolean useAccuracySlayerBonus,
		int toaInvocationLevel)
	{
		int boostedAccuracyLevel;
		double accuracyPrayer;
		double voidMultiplier;
		int baseOffset;
		switch (style.combatStyle)
		{
			case RANGED:
				boostedAccuracyLevel = boostedRanged;
				accuracyPrayer = prayer.rangedAccuracyMultiplier;
				voidMultiplier = equipment.hasRangedVoid() ? 1.10 : 1.0;
				baseOffset = 8;
				break;
			case MAGIC:
				boostedAccuracyLevel = boostedMagic;
				accuracyPrayer = prayer.magicAccuracyMultiplier;
				voidMultiplier = equipment.hasMageVoid() ? 1.45 : 1.0;
				baseOffset = 9;
				break;
			case MELEE:
			default:
				boostedAccuracyLevel = boostedAttack;
				accuracyPrayer = prayer.meleeAccuracyMultiplier;
				voidMultiplier = equipment.hasMeleeVoid() ? 1.10 : 1.0;
				baseOffset = 8;
				break;
		}

		int effectiveAccuracy = CombatPrediction.effectiveAccuracyLevel(
			boostedAccuracyLevel,
			accuracyPrayer,
			style.accuracyStyleBonus,
			voidMultiplier,
			baseOffset);
		int selectedAttackBonus = equipment.attackBonus(style.attackType);
		if (style.combatStyle == CombatStyle.MAGIC && equipment.isShadow())
		{
			selectedAttackBonus *= TargetMechanics.isInTombsOfAmascut(targetId) ? 4 : 3;
		}
		int baseAttackRoll = effectiveAccuracy * (selectedAttackBonus + 64);
		int attackRoll = applyTargetAccuracyModifiers(
			baseAttackRoll,
			style,
			equipment,
			target,
			manualSpell,
			useAccuracySlayerBonus);
        if(TargetMechanics.isWardenP2(targetId) && equipment.isTwistedBow() && style.combatStyle==CombatStyle.RANGED)
            attackRoll=twistedBowScale(attackRoll,target,true);
        int defenceRoll = CombatPrediction.defenceRoll(
            target,
            defenceAttackType,
			TargetMechanics.magicUsesDefenceLevel(targetId));
		if (TargetMechanics.scalesWithToaInvocation(targetId) && toaInvocationLevel > 0)
		{
			defenceRoll = scale(defenceRoll, 250 + Math.max(0, toaInvocationLevel), 250);
		}
		return new AccuracyRolls(attackRoll, defenceRoll, effectiveAccuracy);
	}

	private static int applyTargetAccuracyModifiers(
		int baseAttackRoll,
		StyleState style,
		EquipmentState equipment,
		TargetProfile target,
		MagicSpell manualSpell,
		boolean useAccuracySlayerBonus)
	{
		int attackRoll = baseAttackRoll;
		String weapon = equipment.weaponNameLower;
		switch (style.combatStyle)
		{
			case MELEE:
				if (equipment.hasApplicableMeleeSalve(target))
				{
					attackRoll = scale(attackRoll, equipment.getSalveNumerator(), equipment.getSalveDenominator());
				}
				else if (useAccuracySlayerBonus)
				{
					attackRoll = scale(attackRoll, 7, 6);
				}
				if (equipment.isObsidianWeapon() && equipment.hasFullObsidian())
				{
					attackRoll += baseAttackRoll / 10;
				}
				if (containsAny(weapon, "arclight", "emberlight") && target.hasAttribute("demon"))
				{
					attackRoll += scale(attackRoll, DemonbaneRules.bonusPercent(target.getId(), 70), 100);
				}
				else if (containsAny(weapon, "silverlight", "darklight") && target.hasAttribute("demon"))
				{
					attackRoll += scale(attackRoll, DemonbaneRules.bonusPercent(target.getId(), 60), 100);
				}
				if (containsAny(weapon, "bone claws", "burning claws") && target.hasAttribute("demon"))
				{
					attackRoll += scale(attackRoll, DemonbaneRules.bonusPercent(target.getId(), 5), 100);
				}
				if (weapon.contains("dragon hunter lance") && target.hasAttribute("dragon"))
				{
					attackRoll = scale(attackRoll, 6, 5);
				}
				else if (weapon.contains("dragon hunter wand") && target.hasAttribute("dragon"))
				{
					attackRoll = scale(attackRoll, 7, 4);
				}
				if (weapon.contains("keris partisan of breaching") && target.hasAttribute("kalphite"))
				{
					attackRoll = scale(attackRoll, 133, 100);
				}
				if (weapon.contains("granite hammer") && target.hasAttribute("golem"))
				{
					attackRoll = scale(attackRoll, 13, 10);
				}
				if (style.attackType == AttackType.CRUSH && equipment.inquisitorPoints > 0)
				{
					attackRoll = scale(attackRoll, 200 + equipment.inquisitorPoints, 200);
				}
				break;

			case RANGED:
				if (equipment.getCrystalAccuracyModifier() > 1.0)
				{
					attackRoll = (int) Math.floor(attackRoll * equipment.getCrystalAccuracyModifier());
				}
				if (equipment.hasApplicableRangedSalve(target))
				{
					attackRoll = scale(attackRoll, equipment.getSalveNumerator(), equipment.getSalveDenominator());
				}
				else if (useAccuracySlayerBonus)
				{
					attackRoll = scale(attackRoll, 23, 20);
				}
				if (equipment.isTwistedBow())
				{
					attackRoll = twistedBowScale(attackRoll, target, true);
				}
				if (weapon.contains("dragon hunter crossbow") && target.hasAttribute("dragon"))
				{
					attackRoll = scale(attackRoll, 13, 10);
				}
				if (weapon.contains("scorching bow") && target.hasAttribute("demon"))
				{
					attackRoll += scale(attackRoll, DemonbaneRules.bonusPercent(target.getId(), 30), 100);
				}
				break;

			case MAGIC:
			default:
				int additivePercent = 0;
				boolean salveApplies = equipment.hasApplicableMagicSalve(target);
				if (salveApplies)
				{
					additivePercent += equipment.salveEnhanced ? 20 : 15;
				}
				if (equipment.smokeStaff && manualSpell.getSpellbook() == MagicSpell.Spellbook.STANDARD)
				{
					additivePercent += 10;
				}
				if (additivePercent != 0)
				{
					attackRoll = scale(attackRoll, 100 + additivePercent, 100);
				}
				if (target.hasAttribute("dragon"))
				{
					if (weapon.contains("dragon hunter crossbow"))
					{
						attackRoll = scale(attackRoll, 13, 10);
					}
					else if (weapon.contains("dragon hunter lance"))
					{
						attackRoll = scale(attackRoll, 6, 5);
					}
					else if (weapon.contains("dragon hunter wand"))
					{
						attackRoll = scale(attackRoll, 7, 4);
					}
				}
				if (!salveApplies && useAccuracySlayerBonus)
				{
					attackRoll = scale(attackRoll, 23, 20);
				}
				if (equipment.usesSpellbookMagic()
                    && manualSpell.isDemonbane()
					&& target.hasAttribute("demon"))
				{
					attackRoll += scale(attackRoll, DemonbaneRules.bonusPercent(target.getId(), 20), 100);
				}
                attackRoll=SpellEquipmentRules.accuracy(attackRoll,manualSpell,equipment.shieldId,equipment.usesSpellbookMagic());
				if (equipment.usesSpellbookMagic()
                    && manualSpell.isSet()
					&& manualSpell.getElementName().equals(target.getWeaknessElement()))
				{
					attackRoll += baseAttackRoll * target.getWeaknessSeverity() / 100;
				}
				break;
		}
		return attackRoll;
	}

	private static int applyMeleeTargetDamage(
		int maxHit,
		EquipmentState equipment,
		TargetProfile target)
	{
		String weapon = equipment.weaponNameLower;
		maxHit = applyWildernessDamage(maxHit, weapon, CombatStyle.MELEE, target);
		if (containsAny(weapon, "arclight", "emberlight") && target.hasAttribute("demon"))
		{
			maxHit += scale(maxHit, DemonbaneRules.bonusPercent(target.getId(), 70), 100);
		}
		else if (containsAny(weapon, "silverlight", "darklight") && target.hasAttribute("demon"))
		{
			maxHit += scale(maxHit, DemonbaneRules.bonusPercent(target.getId(), 60), 100);
		}
		if (containsAny(weapon, "bone claws", "burning claws") && target.hasAttribute("demon"))
		{
			maxHit += scale(maxHit, DemonbaneRules.bonusPercent(target.getId(), 5), 100);
		}
		if (weapon.contains("dragon hunter lance") && target.hasAttribute("dragon"))
		{
			maxHit = scale(maxHit, 6, 5);
		}
		else if (weapon.contains("dragon hunter wand") && target.hasAttribute("dragon"))
		{
			maxHit = scale(maxHit, 7, 5);
		}
		if (weapon.contains("keris") && target.hasAttribute("kalphite"))
		{
			maxHit = weapon.contains("of amascut")
				? scale(maxHit, 115, 100)
				: scale(maxHit, 133, 100);
		}
		if (weapon.contains("barronite mace") && target.hasAttribute("golem"))
		{
			maxHit = scale(maxHit, 23, 20);
		}
		if (weapon.contains("granite hammer") && target.hasAttribute("golem"))
		{
			maxHit = scale(maxHit, 13, 10);
		}
		if (weapon.contains("leaf-bladed battleaxe") && target.hasAttribute("leafy"))
		{
			maxHit = scale(maxHit, 47, 40);
		}
		if (weapon.contains("colossal blade"))
		{
			maxHit += Math.min(target.getSize() * 2, 10);
		}
		if (equipment.isRatBoneWeapon() && target.hasAttribute("rat"))
		{
			maxHit += 10;
		}
		return maxHit;
	}

	private static int applyRangedTargetDamage(
		int maxHit,
		EquipmentState equipment,
		TargetProfile target)
	{
		String weapon = equipment.weaponNameLower;
		maxHit = applyWildernessDamage(maxHit, weapon, CombatStyle.RANGED, target);
		if (weapon.contains("dragon hunter crossbow") && target.hasAttribute("dragon"))
		{
			maxHit = scale(maxHit, 5, 4);
		}
		if (weapon.contains("scorching bow") && target.hasAttribute("demon"))
		{
			maxHit += scale(maxHit, DemonbaneRules.bonusPercent(target.getId(), 30), 100);
		}
		if (equipment.isRatBoneWeapon() && target.hasAttribute("rat"))
		{
			maxHit += 10;
		}
		return maxHit;
	}

	private static int applyMagicTargetDamage(
		int maxHit,
		EquipmentState equipment,
		TargetProfile target)
	{
		String weapon = equipment.weaponNameLower;
		maxHit = applyWildernessDamage(maxHit, weapon, CombatStyle.MAGIC, target);
		if (target.hasAttribute("dragon"))
		{
			if (weapon.contains("dragon hunter crossbow"))
			{
				return scale(maxHit, 5, 4);
			}
			if (weapon.contains("dragon hunter lance"))
			{
				return scale(maxHit, 6, 5);
			}
			if (weapon.contains("dragon hunter wand"))
			{
				return scale(maxHit, 7, 5);
			}
		}
		return maxHit;
	}

	private static int twistedBowScale(int value, TargetProfile target, boolean accuracy)
	{
		int cap = target.hasAttribute("xerician") ? 350 : 250;
		int magic = Math.min(cap, Math.max(target.getMagicLevel(), target.getOffensiveMagic()));
		int factor = accuracy ? 10 : 14;
		int base = accuracy ? 140 : 250;
		int clamp = accuracy ? 140 : 250;
		int second = (3 * magic - factor) / 100;
		int inner = (3 * magic / 10) - 10 * factor;
		int third = inner * inner / 100;
		int bonus = Math.max(0, Math.min(clamp, base + second - third));
		return scale(value, bonus, 100);
	}

	private static int getAttackSpeedTicks(
		EquipmentState equipment,
		StyleState style,
		MagicSpell manualSpell,
		int targetId)
	{
		int ticks;
		if (style.combatStyle == CombatStyle.MAGIC && equipment.tomeEmpowered)
        {
            ticks = 2;
        }
        else if (style.combatStyle == CombatStyle.MELEE && equipment.weaponNameLower.contains("obligator"))
		{
			ticks = equipment.isObligatorThrust() ? 5 : 3;
		}
		else if (style.combatStyle == CombatStyle.MAGIC && equipment.usesSpellbookMagic())
		{
			ticks = equipment.weaponNameLower.contains("harmonised nightmare staff")
				&& manualSpell.getSpellbook() == MagicSpell.Spellbook.STANDARD
				? 4 : equipment.weaponNameLower.contains("twinflame staff") ? 6 : 5;
		}
		else if (style.combatStyle == CombatStyle.RANGED
			&& ("Rapid".equals(style.name) || "Rapid (forced)".equals(style.name) || "Flare".equals(style.name)))
		{
			ticks = equipment.attackSpeedTicks - 1;
		}
		else
		{
			ticks = equipment.attackSpeedTicks;
		}
		if (targetId == 7223 && equipment.isRatBoneWeapon())
		{
			ticks = 1;
		}
		return Math.max(1, ticks);
	}

	private static int applyDamagePrayer(int level, double multiplier, boolean minimumOne)
	{
		if (minimumOne && multiplier > 1.0 && level <= 20)
		{
			return level + 1;
		}
		return (int) Math.floor(level * multiplier);
	}

	private static int scale(int value, int numerator, int denominator)
	{
		return denominator == 0 ? value : (int) ((long) value * numerator / denominator);
	}

	static int applyWildernessDamage(
		int maximumHit,
		String weaponName,
		CombatStyle combatStyle,
		TargetProfile target)
	{
		if (target == null || !target.hasAttribute("wilderness"))
		{
			return maximumHit;
		}

		String weapon = weaponName == null ? "" : weaponName.toLowerCase(Locale.ROOT);
		boolean applies = combatStyle == CombatStyle.MELEE
			? containsAny(weapon, "viggora's chainmace", "ursine chainmace")
			: combatStyle == CombatStyle.RANGED
				? containsAny(weapon, "craw's bow", "webweaver bow")
				: containsAny(weapon, "thammaron's sceptre", "accursed sceptre");
		return applies ? scale(maximumHit, 3, 2) : maximumHit;
	}

	private static String targetStatus(int targetId, TargetProfile target)
	{
		if (targetId < 0)
		{
			return "Select target";
		}
		if (target == null)
		{
			return "Target unavailable";
		}
		return target.isAmbiguous() ? "Target variant" : "Target needed";
	}

    private static boolean specialNeedsMeleeRange(SpecialAttackWeapon weapon)
    { return weapon.getCombatStyle()==CombatStyle.MELEE || weapon==SpecialAttackWeapon.VOIDWAKER; }

	private static boolean isTargetImmune(
		int targetId,
		StyleState style,
		EquipmentState equipment,
		TargetProfile target)
	{
		if (style.attackType.isMelee()
			&& TargetMechanics.isZulrah(targetId)
			&& equipment.weaponCategory == WeaponCategory.POLEARM)
		{
			return false;
		}
		if (TargetMechanics.isImmune(targetId, style.attackType))
		{
			return true;
		}
		if (TargetMechanics.isGuardian(targetId))
		{
			return !style.attackType.isMelee() || equipment.weaponCategory != WeaponCategory.PICKAXE;
		}
		if (equipment.isRatBoneWeapon() && !target.hasAttribute("rat"))
		{
			return true;
		}
		if (target.hasAttribute("leafy") && !equipment.isLeafBladedWeapon())
		{
			return true;
		}
		if (target.hasAttribute("vampyre3") && !equipment.hasTierThreeVampyrebane())
		{
			return true;
		}
		if (target.hasAttribute("vampyre2")
			&& !equipment.hasTierTwoVampyrebane()
			&& !equipment.efaritaysAid
			&& !equipment.hasApplicableSilverWeapon(style.combatStyle))
		{
			return true;
		}
		if (!style.attackType.isMelee())
		{
			return false;
		}
		if (TargetMechanics.isNonSalamanderMeleeImmune(targetId)
			&& equipment.weaponCategory != WeaponCategory.SALAMANDER)
		{
			return true;
		}
		if (target.hasAttribute("flying"))
		{
			return TargetMechanics.isVespula(targetId)
				|| (equipment.weaponCategory != WeaponCategory.POLEARM
					&& equipment.weaponCategory != WeaponCategory.SALAMANDER);
		}
		return false;
	}

	private static EquipmentState readEquipment(Client client, ItemManager itemManager)
	{
		EquipmentState result = new EquipmentState();
		ItemContainer worn = client.getItemContainer(InventoryID.WORN);
		if (worn == null)
		{
			return result;
		}

		Item[] items = worn.getItems();
		int weaponSlot = EquipmentInventorySlot.WEAPON.getSlotIdx();
		if (weaponSlot >= 0 && weaponSlot < items.length)
		{
			Item weapon = items[weaponSlot];
			if (weapon != null && weapon.getId() != -1)
			{
				ItemComposition composition = client.getItemDefinition(weapon.getId());
				String itemName = composition == null || composition.getName() == null
					? ""
					: composition.getName();
				ItemStats stats = itemManager.getItemStats(weapon.getId());
				ItemEquipmentStats equipment = EquipmentStatsFallback.resolve(weapon.getId(), stats == null ? null : stats.getEquipment());
				result.weaponId = weapon.getId();
				result.weaponName = itemName.isEmpty() ? "Unknown weapon" : itemName;
				result.weaponNameLower = itemName.toLowerCase(Locale.ROOT);
				result.weaponCategory = WeaponCategoryDatabase.find(weapon.getId());
				result.weaponStats = equipment;
				if (equipment != null && equipment.getAspeed() > 0)
				{
					result.attackSpeedTicks = equipment.getAspeed();
				}
			}
		}

		AmmunitionState wornAmmo = AmmunitionState.empty();
		for (int slot = 0; slot < items.length; slot++)
		{
			Item item = items[slot];
			if (item == null || item.getId() == -1)
			{
				continue;
			}

            result.itemIds += slot + ":" + item.getId() + " ";
			ItemComposition composition = client.getItemDefinition(item.getId());
			String itemName = composition == null || composition.getName() == null
				? ""
				: composition.getName();
			String lowerName = itemName.toLowerCase(Locale.ROOT);
			if (slot == EquipmentInventorySlot.AMMO.getSlotIdx())
			{
				wornAmmo = readAmmunition(item, lowerName, itemManager);
				continue;
			}

			ItemStats stats = itemManager.getItemStats(item.getId());
			ItemEquipmentStats equipment = EquipmentStatsFallback.resolve(item.getId(), stats == null ? null : stats.getEquipment());
            if(slot==EquipmentInventorySlot.SHIELD.getSlotIdx()) result.shieldId=item.getId();
            if(slot==EquipmentInventorySlot.CAPE.getSlotIdx()) result.capeName=lowerName;
            result.inspectItemName(lowerName);
			addEquipmentBonuses(result, equipment);
		}

		AmmunitionState quiverAmmo = result.dizanasQuiverEquipped
			? readQuiverAmmunition(client, itemManager)
			: AmmunitionState.empty();
		AmmunitionRules.Source ammoSource = AmmunitionRules.selectSource(
			result.weaponNameLower,
			wornAmmo.nameLower,
			quiverAmmo.nameLower);
		AmmunitionState activeAmmo = ammoSource == AmmunitionRules.Source.WORN
			? wornAmmo
			: ammoSource == AmmunitionRules.Source.QUIVER
				? quiverAmmo
				: AmmunitionState.empty();
		if (activeAmmo.isPresent())
		{
			result.ammoNameLower = activeAmmo.nameLower;
			result.ammoQuantity = activeAmmo.quantity;
			result.ammoRangedStrength = activeAmmo.equipmentStats == null
				? 0
				: activeAmmo.equipmentStats.getRstr();
			result.inspectItemName(activeAmmo.nameLower);
			addEquipmentBonuses(result, activeAmmo.equipmentStats);

			boolean sunfireActive = result.permanentDizanasSunfire
				|| (result.dizanasQuiverEquipped
					&& client.getVarbitValue(VarbitID.WORN_ITEMS_DIZANAS_QUIVER_CHARGED) > 0);
			if (sunfireActive
				&& AmmunitionRules.sunfireBonusApplies(result.weaponNameLower, activeAmmo.nameLower))
			{
				result.rangedAttack += 10;
				result.rangedStrength += 1;
			}
		}

		return result;
	}

	private static AmmunitionState readQuiverAmmunition(Client client, ItemManager itemManager)
	{
		int[] values = readQuiverAmmunitionValues(client);
		int itemId = values[0];
		int quantity = values[1];
		if (itemId < 0 || quantity <= 0)
		{
			return AmmunitionState.empty();
		}

		ItemComposition composition = client.getItemDefinition(itemId);
		String itemName = composition == null || composition.getName() == null
			? ""
			: composition.getName().toLowerCase(Locale.ROOT);
		ItemStats stats = itemManager.getItemStats(itemId);
		return new AmmunitionState(
			itemName,
			quantity,
			EquipmentStatsFallback.resolve(itemId, stats == null ? null : stats.getEquipment()));
	}

	static int[] readQuiverAmmunitionValues(Client client)
	{
		return new int[]
		{
			client.getVarpValue(VarPlayerID.DIZANAS_QUIVER_TEMP_AMMO),
			client.getVarpValue(VarPlayerID.DIZANAS_QUIVER_TEMP_AMMO_AMOUNT)
		};
	}

	private static AmmunitionState readAmmunition(
		Item item,
		String itemNameLower,
		ItemManager itemManager)
	{
		if (item == null || item.getId() == -1 || itemNameLower == null || itemNameLower.isEmpty())
		{
			return AmmunitionState.empty();
		}
		ItemStats stats = itemManager.getItemStats(item.getId());
		return new AmmunitionState(
			itemNameLower,
			Math.max(0, item.getQuantity()),
			EquipmentStatsFallback.resolve(item.getId(), stats == null ? null : stats.getEquipment()));
	}

	private static void addEquipmentBonuses(
		EquipmentState result,
		ItemEquipmentStats equipment)
	{
		if (equipment == null)
		{
			return;
		}
		result.stabAttack += equipment.getAstab();
		result.slashAttack += equipment.getAslash();
		result.crushAttack += equipment.getAcrush();
		result.meleeStrength += equipment.getStr();
		result.rangedAttack += equipment.getArange();
		result.rangedStrength += equipment.getRstr();
		result.magicAttack += equipment.getAmagic();
		result.magicDamage += equipment.getMdmg();
	}

	private static StyleState getStyleState(
		Client client,
		EquipmentState equipment,
		MagicSpell manualSpell)
	{
		int styleIndex = client.getVarpValue(VarPlayerID.COM_MODE);
		if (equipment.weaponNameLower.contains("obligator"))
			return getObligatorStyle(equipment, styleIndex, false);
		String runeLiteStyle = getRuneLiteAttackStyle(client, styleIndex);

		if (equipment.weaponCategory == WeaponCategory.SALAMANDER && styleIndex >= 0 && styleIndex <= 2)
		{
			if (styleIndex == 0)
			{
				return new StyleState(CombatStyle.MELEE, AttackType.SLASH, "Scorch", 0, 3);
			}
			if (styleIndex == 1)
			{
				return new StyleState(CombatStyle.RANGED, AttackType.RANGED_MIXED, "Flare", 0, 0);
			}
			return new StyleState(CombatStyle.MAGIC, AttackType.MAGIC, "Blaze", 0, 0);
		}
		if (equipment.hasBuiltInMagicAttack())
		{
			boolean longrange = "Longrange".equals(runeLiteStyle) || styleIndex == 2 || styleIndex == 3;
			return new StyleState(
				CombatStyle.MAGIC,
				AttackType.MAGIC,
				longrange ? "Longrange" : "Built-in cast",
				longrange ? 0 : 2,
				0);
		}
		if ("Ranging".equals(runeLiteStyle) || "Longrange".equals(runeLiteStyle))
		{
			String name = "Longrange".equals(runeLiteStyle)
				? "Longrange"
				: styleIndex == 0 ? "Accurate" : "Rapid";
			int accurateBonus = "Accurate".equals(name) ? 3 : 0;
			return new StyleState(
				CombatStyle.RANGED,
				resolveAttackType(equipment, CombatStyle.RANGED, styleIndex),
				name,
				accurateBonus,
				accurateBonus);
		}
		if ("Casting".equals(runeLiteStyle) || "Defensive Casting".equals(runeLiteStyle))
		{
			return new StyleState(
				CombatStyle.MAGIC,
				AttackType.MAGIC,
				runeLiteStyle,
				"Casting".equals(runeLiteStyle) && equipment.hasBuiltInMagicAttack() ? 2 : 0,
				0);
		}
		if ("Accurate".equals(runeLiteStyle)
			|| "Aggressive".equals(runeLiteStyle)
			|| "Controlled".equals(runeLiteStyle)
			|| "Defensive".equals(runeLiteStyle))
		{
			int accuracyBonus = "Accurate".equals(runeLiteStyle) ? 3
				: "Controlled".equals(runeLiteStyle) ? 1 : 0;
			int damageBonus = "Aggressive".equals(runeLiteStyle) ? 3
				: "Controlled".equals(runeLiteStyle) ? 1 : 0;
			return new StyleState(
				CombatStyle.MELEE,
				resolveAttackType(equipment, CombatStyle.MELEE, styleIndex),
				runeLiteStyle,
				accuracyBonus,
				damageBonus);
		}

		if (looksLikeRangedWeapon(equipment))
		{
			String name = styleIndex == 0 ? "Accurate" : styleIndex == 3 ? "Longrange" : "Rapid";
			int accurateBonus = styleIndex == 0 ? 3 : 0;
			return new StyleState(
				CombatStyle.RANGED,
				resolveAttackType(equipment, CombatStyle.RANGED, styleIndex),
				name,
				accurateBonus,
				accurateBonus);
		}
		if (looksLikeMagicWeapon(equipment))
		{
			return new StyleState(
				CombatStyle.MAGIC,
				AttackType.MAGIC,
				styleIndex == 1 ? "Defensive Casting" : "Casting",
				styleIndex == 1 ? 0 : 2,
				0);
		}

		String meleeName = styleIndex == 1 ? "Aggressive"
			: styleIndex == 2 ? "Controlled"
			: styleIndex == 3 ? "Defensive" : "Accurate";
		int meleeBonus = styleIndex == 1 ? 3 : styleIndex == 2 ? 1 : 0;
		int meleeAccuracyBonus = styleIndex == 0 ? 3 : styleIndex == 2 ? 1 : 0;
		return new StyleState(
			CombatStyle.MELEE,
			resolveAttackType(equipment, CombatStyle.MELEE, styleIndex),
			meleeName,
			meleeAccuracyBonus,
			meleeBonus);
	}

	private static StyleState getForcedStyle(
		Client client,
		CombatStyleOverride override,
		EquipmentState equipment)
	{
		int styleIndex = client.getVarpValue(VarPlayerID.COM_MODE);
		if (override == CombatStyleOverride.MELEE && equipment.weaponNameLower.contains("obligator"))
			return getObligatorStyle(equipment, styleIndex, true);
		switch (override)
		{
			case RANGED:
				String rangedName = styleIndex == 0 ? "Accurate" : styleIndex == 3 ? "Longrange" : "Rapid";
				int rangedBonus = styleIndex == 0 ? 3 : 0;
				return new StyleState(
					CombatStyle.RANGED,
					resolveAttackType(equipment, CombatStyle.RANGED, styleIndex),
					rangedName + " (forced)",
					rangedBonus,
					rangedBonus);
			case MAGIC:
				boolean poweredAccurate = equipment.hasBuiltInMagicAttack()
					&& styleIndex != 2 && styleIndex != 3;
				return new StyleState(
					CombatStyle.MAGIC,
					AttackType.MAGIC,
					"Casting (forced)",
					poweredAccurate ? 2 : 0,
					0);
			case MELEE:
			default:
				String meleeName = styleIndex == 1 ? "Aggressive"
					: styleIndex == 2 ? "Controlled"
					: styleIndex == 3 ? "Defensive" : "Accurate";
				int meleeBonus = styleIndex == 1 ? 3 : styleIndex == 2 ? 1 : 0;
				int meleeAccuracyBonus = styleIndex == 0 ? 3 : styleIndex == 2 ? 1 : 0;
				return new StyleState(
					CombatStyle.MELEE,
					resolveAttackType(equipment, CombatStyle.MELEE, styleIndex),
					meleeName + " (forced)",
					meleeAccuracyBonus,
					meleeBonus);
		}
	}

	private static StyleState getObligatorStyle(EquipmentState equipment, int styleIndex, boolean forced)
	{
		equipment.obligatorStyleIndex = styleIndex;
		boolean accurate = styleIndex == 0;
		String name = styleIndex == 0 ? "Thrust (Accurate)" : styleIndex == 1 ? "Thrust (Aggressive)"
			: styleIndex == 2 ? "Smash (Aggressive)" : "Unknown Obligator style";
		return new StyleState(CombatStyle.MELEE, AttackType.CRUSH, name + (forced ? " (forced)" : ""),
			accurate ? 3 : 0, styleIndex == 1 || styleIndex == 2 ? 3 : 0);
	}

	private static AttackType resolveAttackType(
		EquipmentState equipment,
		CombatStyle combatStyle,
		int styleIndex)
	{
		if (combatStyle == CombatStyle.MAGIC)
		{
			return AttackType.MAGIC;
		}
		AttackType categoryType = equipment.weaponCategory.attackType(styleIndex);
		if (combatStyle == CombatStyle.RANGED)
		{
			return categoryType.isRanged() ? categoryType : equipment.fallbackRangedType();
		}
		return categoryType.isMelee() ? categoryType : equipment.bestMeleeType();
	}

	private static String getRuneLiteAttackStyle(Client client, int attackStyleIndex)
	{
		try
		{
			int weaponType = client.getVarbitValue(VarbitID.COMBAT_WEAPON_CATEGORY);
			int weaponStyleEnum = client.getEnum(EnumID.WEAPON_STYLES).getIntValue(weaponType);
			if (weaponStyleEnum == -1)
			{
				if (weaponType == 22)
				{
					String[] blueMoonStyles = {
						"Accurate", "Aggressive", null, "Defensive", "Casting", "Defensive Casting"
					};
					if (attackStyleIndex == 4)
					{
						attackStyleIndex += client.getVarbitValue(VarbitID.AUTOCAST_DEFMODE);
					}
					return attackStyleIndex >= 0 && attackStyleIndex < blueMoonStyles.length
						? blueMoonStyles[attackStyleIndex] : "Other";
				}
				return "Other";
			}

			int[] styleStructs = client.getEnum(weaponStyleEnum).getIntVals();
			if (attackStyleIndex == 4)
			{
				attackStyleIndex += client.getVarbitValue(VarbitID.AUTOCAST_DEFMODE);
			}
			if (attackStyleIndex < 0 || attackStyleIndex >= styleStructs.length)
			{
				return "Other";
			}

			StructComposition style = client.getStructComposition(styleStructs[attackStyleIndex]);
			String name = style.getStringValue(ParamID.ATTACK_STYLE_NAME);
			if (attackStyleIndex == 5 && "Defensive".equals(name))
			{
				return "Defensive Casting";
			}
			return name;
		}
		catch (RuntimeException ex)
		{
			return "Other";
		}
	}

	private static boolean looksLikeRangedWeapon(EquipmentState equipment)
	{
		String name = equipment.weaponNameLower;
		if (containsAny(name, "bow", "crossbow", "blowpipe", "chinchompa", "ballista",
			"dart", "knife", "javelin", "thrownaxe"))
		{
			return true;
		}
		return equipment.weaponStats != null
			&& equipment.weaponStats.getArange() > Math.max(equipment.weaponStats.getAmagic(),
				Math.max(equipment.weaponStats.getAstab(),
					Math.max(equipment.weaponStats.getAslash(), equipment.weaponStats.getAcrush())));
	}

	private static boolean looksLikeMagicWeapon(EquipmentState equipment)
	{
		String name = equipment.weaponNameLower;
		if (containsAny(name, "trident", "sanguinesti", "tumeken's shadow", "warped sceptre",
			"thammaron's sceptre", "accursed sceptre"))
		{
			return true;
		}
		return equipment.weaponStats != null
			&& equipment.weaponStats.getAmagic() > equipment.weaponStats.getArange()
			&& equipment.weaponStats.getAmagic() > Math.max(equipment.weaponStats.getAstab(),
				Math.max(equipment.weaponStats.getAslash(), equipment.weaponStats.getAcrush()));
	}

	private static PrayerState getPrayerState(Client client)
	{
		PrayerState state = new PrayerState();

		// RuneLite gameval PRAYER_ZEAL, distinct from PRAYER_ZEAL_UNLOCKED (16578).
		// Read the active varbit directly until ZEAL is present in the stable Prayer API.
		if (client.getVarbitValue(16577) == 1)
		{
			state.melee("Zeal", 1.25, 1.28, false);
		}
		else if (client.isPrayerActive(Prayer.PIETY))
		{
			state.melee("Piety", 1.20, 1.23, false);
		}
		else if (client.isPrayerActive(Prayer.CHIVALRY))
		{
			state.melee("Chivalry", 1.15, 1.18, false);
		}
		else if (client.isPrayerActive(Prayer.RP_DECIMATE))
		{
			state.melee("Decimate", 1.25, 1.27, false);
		}
		else if (client.isPrayerActive(Prayer.RP_ANCIENT_STRENGTH))
		{
			state.melee("Ancient Strength", 1.20, 1.20, false);
		}
		else if (client.isPrayerActive(Prayer.RP_TRINITAS))
		{
			state.melee("Trinitas", 1.15, 1.15, false);
		}
		else
		{
			if (client.isPrayerActive(Prayer.INCREDIBLE_REFLEXES))
			{
				state.meleeAccuracy("Incredible Reflexes", 1.15);
			}
			else if (client.isPrayerActive(Prayer.IMPROVED_REFLEXES))
			{
				state.meleeAccuracy("Improved Reflexes", 1.10);
			}
			else if (client.isPrayerActive(Prayer.CLARITY_OF_THOUGHT))
			{
				state.meleeAccuracy("Clarity of Thought", 1.05);
			}

			if (client.isPrayerActive(Prayer.ULTIMATE_STRENGTH))
			{
				state.meleeStrength("Ultimate Strength", 1.15, false);
			}
			else if (client.isPrayerActive(Prayer.SUPERHUMAN_STRENGTH))
			{
				state.meleeStrength("Superhuman Strength", 1.10, false);
			}
			else if (client.isPrayerActive(Prayer.BURST_OF_STRENGTH))
			{
				state.meleeStrength("Burst of Strength", 1.05, true);
			}
		}

		if (client.isPrayerActive(Prayer.RIGOUR))
		{
			state.ranged("Rigour", 1.20, 1.23, false);
		}
		else if (client.isPrayerActive(Prayer.RP_ANNIHILATE))
		{
			state.ranged("Annihilate", 1.25, 1.27, false);
		}
		else if (client.isPrayerActive(Prayer.RP_ANCIENT_SIGHT))
		{
			state.ranged("Ancient Sight", 1.20, 1.20, false);
		}
		else if (client.isPrayerActive(Prayer.RP_TRINITAS))
		{
			state.ranged("Trinitas", 1.15, 1.15, false);
		}
		else if (client.isPrayerActive(Prayer.DEADEYE))
		{
			state.ranged("Deadeye", 1.18, 1.18, false);
		}
		else if (client.isPrayerActive(Prayer.EAGLE_EYE))
		{
			state.ranged("Eagle Eye", 1.15, 1.15, false);
		}
		else if (client.isPrayerActive(Prayer.HAWK_EYE))
		{
			state.ranged("Hawk Eye", 1.10, 1.10, false);
		}
		else if (client.isPrayerActive(Prayer.SHARP_EYE))
		{
			state.ranged("Sharp Eye", 1.05, 1.05, true);
		}

		if (client.isPrayerActive(Prayer.AUGURY))
		{
			state.magic("Augury", 1.25, 4.0);
		}
		else if (client.isPrayerActive(Prayer.RP_VAPORISE))
		{
			state.magic("Vaporise", 1.25, 4.0);
		}
		else if (client.isPrayerActive(Prayer.RP_ANCIENT_WILL))
		{
			state.magic("Ancient Will", 1.20, 0.0);
		}
		else if (client.isPrayerActive(Prayer.RP_TRINITAS))
		{
			state.magic("Trinitas", 1.15, 0.0);
		}
		else if (client.isPrayerActive(Prayer.MYSTIC_VIGOUR))
		{
			state.magic("Mystic Vigour", 1.18, 3.0);
		}
		else if (client.isPrayerActive(Prayer.MYSTIC_MIGHT))
		{
			state.magic("Mystic Might", 1.15, 2.0);
		}
		else if (client.isPrayerActive(Prayer.MYSTIC_LORE))
		{
			state.magic("Mystic Lore", 1.10, 1.0);
		}
		else if (client.isPrayerActive(Prayer.MYSTIC_WILL))
		{
			state.magic("Mystic Will", 1.05, 0.0);
		}

		state.finish();
		return state;
	}

	private static MagicBase getMagicBase(
		String weaponName,
		int boostedMagic,
		MagicSpell manualSpell,
		boolean targetDataAvailable,
		boolean useSlayerBonus)
	{
        if (weaponName.contains("zorya's tome"))
        {
            return weaponName.contains("uncharged")
                ? MagicBase.unsupported("Uncharged", "Zorya's tome must be charged")
                : MagicBase.powered(ZoryaTome.baseMaximum(boostedMagic), false);
        }
		if (weaponName.contains("tumeken's shadow"))
		{
			return targetDataAvailable
				? MagicBase.shadow(Math.max(1, boostedMagic / 3 + 1))
				: MagicBase.unsupported("Select target", "Tumeken's shadow needs a supported target for its 3x or 4x multiplier");
		}
		if (weaponName.contains("thammaron's sceptre"))
		{
			return MagicBase.powered(Math.max(1, boostedMagic / 3 - 8), false);
		}
		if (weaponName.contains("accursed sceptre"))
		{
			return MagicBase.powered(Math.max(1, boostedMagic / 3 - 6), false);
		}
		if (weaponName.contains("trident of the swamp"))
		{
			return MagicBase.powered(Math.max(4, boostedMagic / 3 - 2), false);
		}
		if (weaponName.contains("trident of the seas"))
		{
			return MagicBase.powered(Math.max(1, boostedMagic / 3 - 5), false);
		}
		if (weaponName.contains("sanguinesti staff"))
		{
			return MagicBase.powered(Math.max(6, boostedMagic / 3), true);
		}
		if (weaponName.contains("warped sceptre"))
		{
			return MagicBase.powered(Math.max(1, (8 * boostedMagic + 96) / 37), false);
		}
		if (weaponName.contains("bone staff"))
		{
			return MagicBase.powered(Math.max(1, boostedMagic / 3 - 5) + 10, false);
		}
		if (weaponName.contains("eye of ayak"))
		{
			return MagicBase.powered(Math.max(1, boostedMagic / 3 - 6), false);
		}
		if (weaponName.contains("starter staff"))
		{
			return MagicBase.powered(8, false);
		}
		if (weaponName.contains("dawnbringer"))
		{
			return MagicBase.powered(Math.max(1, boostedMagic / 3 - 2), false);
		}
		if (weaponName.contains("crystal staff (basic)") || weaponName.contains("corrupted staff (basic)"))
		{
			return MagicBase.powered(23, false);
		}
		if (weaponName.contains("crystal staff (attuned)") || weaponName.contains("corrupted staff (attuned)"))
		{
			return MagicBase.powered(31, false);
		}
		if (weaponName.contains("crystal staff (perfected)") || weaponName.contains("corrupted staff (perfected)"))
		{
			return MagicBase.powered(39, false);
		}
		if (weaponName.contains("swamp lizard"))
		{
			return MagicBase.powered(salamanderMaxHit(boostedMagic, 56), false);
		}
		if (weaponName.contains("orange salamander"))
		{
			return MagicBase.powered(salamanderMaxHit(boostedMagic, 59), false);
		}
		if (weaponName.contains("red salamander"))
		{
			return MagicBase.powered(salamanderMaxHit(boostedMagic, 77), false);
		}
		if (weaponName.contains("black salamander"))
		{
			return MagicBase.powered(salamanderMaxHit(boostedMagic, 92), false);
		}
		if (weaponName.contains("tecu salamander"))
		{
			return MagicBase.powered(salamanderMaxHit(boostedMagic, 104), false);
		}
		if (!manualSpell.isSet())
		{
			return MagicBase.unsupported("Set spell", "Choose the active spell in Calculation inputs");
		}
		if (manualSpell == MagicSpell.MAGIC_DART)
		{
			int dartMax = weaponName.contains("slayer's staff (e)") && useSlayerBonus
				? 13 + boostedMagic / 6
				: 10 + boostedMagic / 10;
			return MagicBase.manual(dartMax);
		}
		return MagicBase.manual(manualSpell.getBaseMaxHit());
	}

	private static int salamanderMaxHit(int magicLevel, int hiddenStrengthBonus)
	{
		return (magicLevel * (hiddenStrengthBonus + 64) + 320) / 640;
	}

	private static void addConditionalWeaponWarnings(
		EquipmentState equipment,
		AttackType attackType,
		TargetProfile target,
		boolean targetDataAvailable,
		List<String> warnings)
	{
		String weapon = equipment.weaponNameLower;
		if (containsAny(weapon, "venator bow", "chinchompa"))
		{
			warnings.add("Multi-hit total needs target size and attack behaviour");
		}

		if (shouldShowTargetTypeWarning(weapon, targetDataAvailable))
		{
			warnings.add("Weapon damage modifier needs the target type");
		}
		if (!(targetDataAvailable && target.hasAttribute("wilderness"))
			&& containsAny(weapon, "craw's bow", "webweaver bow", "viggora's chainmace",
			"ursine chainmace", "thammaron's sceptre", "accursed sceptre"))
		{
			warnings.add("Wilderness weapon modifier needs location data");
		}
		if (weapon.contains("salamander"))
		{
			warnings.add("Salamander fuel and style formulas need a dedicated input");
		}
		if (shouldShowInquisitorAttackTypeWarning(equipment.inquisitorPoints, attackType))
		{
			warnings.add("Inquisitor damage only applies on a crush attack type");
		}
	}

	static boolean shouldShowTargetTypeWarning(String weaponName, boolean targetDataAvailable)
	{
		String weapon = weaponName == null ? "" : weaponName.toLowerCase(Locale.ROOT);
		return !targetDataAvailable && containsAny(weapon,
			"dragon hunter lance", "dragon hunter crossbow", "arclight", "emberlight",
			"barronite mace", "keris", "leaf-bladed", "rat bone", "scorching bow");
	}

	static boolean shouldShowInquisitorAttackTypeWarning(int inquisitorPoints, AttackType attackType)
	{
		return inquisitorPoints > 0 && attackType != AttackType.CRUSH;
	}

	static boolean slayerInputApplies(boolean applySlayerBonus, CombatDummy combatDummy)
	{
		return applySlayerBonus
			&& (combatDummy == null || combatDummy.getTargetProfile().isSlayerMonster());
	}

	static int targetMagicDefenceRoll(TargetProfile target, int targetId, int toaInvocationLevel)
	{
		if (target == null || target.isAmbiguous())
		{
			return -1;
		}
		int roll = CombatPrediction.defenceRoll(
			target,
			AttackType.MAGIC,
			TargetMechanics.magicUsesDefenceLevel(targetId));
		return TargetMechanics.scalesWithToaInvocation(targetId) && toaInvocationLevel > 0
			? scale(roll, 250 + Math.max(0, toaInvocationLevel), 250)
			: roll;
	}

	private static int baseMaxHit(int effectiveLevel, int damageBonus)
	{
		return (int) Math.floor(0.5 + effectiveLevel * (damageBonus + 64) / 640.0);
	}

	private static boolean containsAny(String value, String... fragments)
	{
		for (String fragment : fragments)
		{
			if (value.contains(fragment))
			{
				return true;
			}
		}
		return false;
	}

	private static String formatHitSplit(int[] hits)
	{
		StringBuilder text = new StringBuilder();
		for (int hit : hits)
		{
			if (text.length() > 0)
			{
				text.append(" / ");
			}
			text.append(hit);
		}
		return text.toString();
	}

	public boolean isLoggedIn()
	{
		return loggedIn;
	}

    /** A notice marker is informational, not a guarantee of complete mechanic coverage. */
    public boolean hasInputNotices()
    {
        return loggedIn && (!warnings.isEmpty() || !maxHitAvailable
            || (hasTarget() && !combatDummy && (!isAccuracyAvailable() || !isDpsAvailable()))
            || (hasSupportedSpecialAttack() && !isSpecialMaximumAvailable()));
    }

    public String getDiagnosticInputs() { return diagnosticInputs; }
    public boolean hasActiveManualOverride() { return loggedIn && activeManualOverride; }
    public SpecialAttackWeapon getSpecialAttackWeapon() { return specialAttackResult.getWeapon(); }
    /** Partial trace: these entries are recorded where the corresponding rule runs. */
    public List<String> getAppliedEffects() { return appliedEffects; }

    /** No account names, chat, world, location or other players are collected. */
    public String diagnosticReport()
    {
        return "Combat Insight " + BuildInfo.VERSION + " diagnostic\n"
            + "Logged in: " + loggedIn + "\n"
            + "Target: " + targetName + " (" + targetId + ")\n"
            + "Target HP estimate: " + targetCurrentHitpoints + "/" + targetMaximumHitpoints + "\n"
            + "Weapon/spell: " + weaponOrSpellName + "\n"
            + "Style: " + styleName + " / " + attackType + "\n"
            + "Boosted Atk/Str/Rng/Mag: " + boostedAttack + "/" + boostedStrength + "/" + boostedRanged + "/" + boostedMagic + "\n"
            + "Prayer: " + prayerName + "\n"
            + "Bonuses melee attack/str/ranged attack/str/magic attack/damage%: "
            + meleeAttackBonus + "/" + meleeStrengthBonus + "/" + rangedAttackBonus + "/" + rangedStrengthBonus + "/" + magicAttackBonus + "/" + magicDamagePercent + "\n"
            + "Max: " + getMaxHitText() + "; hit chance: " + getHitChanceText() + "; DPS: " + getDpsText() + "\n"
            + "Rolls: " + getRollText() + "; attack ticks: " + attackSpeedTicks + "\n"
            + "Special max/chance/average: " + getSpecialMaximumText(true) + "/" + getSpecialHitChanceText() + "/" + getSpecialExpectedDamageText() + "\n"
            + "Local target Defence/Magic/magic defence: " + getTargetDefenceText() + "/" + getTargetMagicText() + "/" + getTargetMagicDefenceText() + "\n"
            + "Availability: max=" + isMaxHitAvailable() + ", accuracy=" + isAccuracyAvailable()
            + ", DPS=" + isDpsAvailable() + "; reasons are shown in the result fields above\n"
            + "Inputs: " + diagnosticInputs + "\n"
            + "Notices: " + String.join("; ", warnings) + "\n";
    }

	public CombatStyle getCombatStyle()
	{
		return combatStyle;
	}

	public AttackType getAttackType()
	{
		return attackType;
	}

	public String getCombatStyleName()
	{
		switch (combatStyle)
		{
			case RANGED:
				return "Ranged";
			case MAGIC:
				return "Magic";
			default:
				return "Melee";
		}
	}

	public int getBoostedAttack()
	{
		return boostedAttack;
	}

	public int getBoostedStrength()
	{
		return boostedStrength;
	}

	public int getMeleeAttackBonus()
	{
		return meleeAttackBonus;
	}

	public int getMeleeStrengthBonus()
	{
		return meleeStrengthBonus;
	}

	public int getAttackSpeedTicks()
	{
		return attackSpeedTicks;
	}

	public String getAttackSpeedText()
	{
		double expectedTicks = targetResult.expectedAttackSpeedTicks;
		if (expectedTicks > 0.0 && Math.abs(expectedTicks - attackSpeedTicks) > 0.0001)
		{
			return String.format(Locale.ROOT, "%.2f ticks avg", expectedTicks);
		}
		return attackSpeedTicks + " ticks";
	}

	public int getStyleStrengthBonus()
	{
		return styleDamageBonus;
	}

	public String getStyleName()
	{
		return styleName;
	}

	public String getWeaponOrSpellName()
	{
		return weaponOrSpellName;
	}

	public String getPrayerName()
	{
		return prayerName;
	}

	public boolean isPrayerAffectsCurrentStyle()
	{
		return prayerAffectsCurrentStyle;
	}

	public boolean isSlayerHelmetEquipped()
	{
		return slayerHelmetEquipped;
	}

	public int getEffectiveStrength()
	{
		return effectiveDamageLevel;
	}

	public int getEffectiveDamageLevel()
	{
		return effectiveDamageLevel;
	}

	public int getMagicBaseMaxHit()
	{
		return magicBaseMaxHit;
	}

	public int getMaxHit()
	{
		return targetResult.dpsAvailable && targetResult.maximumHit >= 0
			? targetResult.maximumHit
			: maxHit;
	}

	public boolean isMaxHitAvailable()
	{
		return maxHitAvailable;
	}

	public boolean isTargetImmune()
	{
		return targetImmune;
	}

	public String getMaxHitText()
	{
		return targetImmune ? "Immune" : maxHitAvailable ? Integer.toString(getMaxHit()) : maxHitStatusText;
	}

	public boolean hasMultiHitSplit()
	{
		return !targetResult.maximumHitSplit.isEmpty();
	}

	public String getMultiHitSplitText()
	{
		return targetResult.maximumHitSplit;
	}

	public boolean hasSupportedSpecialAttack()
	{
		return specialAttackResult.hasSupportedWeapon();
	}

	public boolean isSpecialMaximumAvailable()
	{
		return specialAttackResult.isMaximumAvailable();
	}

	public String getSpecialMaximumText(boolean includeSplit)
	{
		return specialAttackResult.getMaximumHitText(includeSplit);
	}

	public boolean isSpecialAccuracyAvailable()
	{
		return specialAttackResult.isAccuracyAvailable();
	}

	public String getSpecialHitChanceText()
	{
		return specialAttackResult.getHitChanceText();
	}

	public boolean isSpecialExpectedDamageAvailable()
	{
		return specialAttackResult.isExpectedDamageAvailable();
	}

	public String getSpecialExpectedDamageText()
	{
		return specialAttackResult.getExpectedDamageText();
	}

	public String getSpecialOnHitText()
	{
		return specialAttackResult.getOnHitText();
	}

	public boolean hasLiveTargetEffects()
	{
		return targetEffects.hasTargetStats();
	}

	public boolean hasTrackedTargetEffect()
	{
		return targetEffects.hasTrackedEffect();
	}

	public boolean hasTrackedTargetDefence()
	{
		return targetEffects.hasTrackedDefence();
	}

	public String getTargetDefenceText()
	{
		return targetEffects.getDefenceText();
	}

	public boolean hasTrackedTargetMagic()
	{
		return targetEffects.hasTrackedMagic();
	}

	public String getTargetMagicText()
	{
		return targetEffects.getMagicText();
	}

	public boolean hasTrackedTargetMagicDefence()
	{
		return targetEffects.hasTrackedMagicDefence();
	}

	public String getTargetMagicDefenceText()
	{
		return targetEffects.getMagicDefenceText();
	}

	public int getBaseTargetMagicDefenceRoll()
	{
		return baseTargetMagicDefenceRoll;
	}

	public int getCurrentTargetMagicDefenceRoll()
	{
		return currentTargetMagicDefenceRoll;
	}

	public boolean isMagicDefenceRollUsingDefenceLevel()
	{
		return TargetMechanics.magicUsesDefenceLevel(targetId);
	}

	public boolean isMultiHitWeapon()
	{
		return MultiHitWeapon.forWeapon(weaponOrSpellName) != null;
	}

	public String getObservedHitLabel()
	{
		return isMultiHitWeapon() ? "Avg hitsplat" : "Avg hit";
	}

	public String getLevelLabel()
	{
		switch (combatStyle)
		{
			case RANGED:
				return "Ranged level";
			case MAGIC:
				return "Magic level";
			default:
				return "Attack / Strength";
		}
	}

	public String getLevelText()
	{
		switch (combatStyle)
		{
			case RANGED:
				return Integer.toString(boostedRanged);
			case MAGIC:
				return Integer.toString(boostedMagic);
			default:
				return boostedAttack + " / " + boostedStrength;
		}
	}

	public String getGearBonusLabel()
	{
		if (hybridAtlatl)
		{
			return "Ranged atk / melee str";
		}
		switch (combatStyle)
		{
			case RANGED:
				return shortAttackType() + " atk / str";
			case MAGIC:
				return "Magic atk / dmg";
			default:
				return shortAttackType() + " atk / str";
		}
	}

	private String shortAttackType()
	{
		switch (attackType)
		{
			case RANGED_LIGHT:
				return "Light ranged";
			case RANGED_STANDARD:
				return "Standard ranged";
			case RANGED_HEAVY:
				return "Heavy ranged";
			case RANGED_MIXED:
				return "Mixed ranged";
			default:
				return attackType.toString();
		}
	}

	public String getGearBonusText()
	{
		if (hybridAtlatl)
		{
			return signed(rangedAttackBonus) + " / " + signed(meleeStrengthBonus);
		}
		switch (combatStyle)
		{
			case RANGED:
				return signed(rangedAttackBonus) + " / " + signed(rangedStrengthBonus);
			case MAGIC:
				return signed(magicAttackBonus) + " / "
					+ String.format(Locale.ROOT, "%+.1f%%", magicDamagePercent);
			default:
				return signed(meleeAttackBonus) + " / " + signed(meleeStrengthBonus);
		}
	}

	public String getEffectiveLevelLabel()
	{
		switch (combatStyle)
		{
			case RANGED:
				return "Effective ranged";
			case MAGIC:
				return "Base magic hit";
			default:
				return "Effective strength";
		}
	}

	public String getEffectiveLevelText()
	{
		if (combatStyle == CombatStyle.MAGIC)
		{
			return maxHitAvailable ? Integer.toString(magicBaseMaxHit) : "Input needed";
		}
		return Integer.toString(effectiveDamageLevel);
	}

	public String prayerMultiplierText()
	{
		if (combatStyle == CombatStyle.MAGIC)
		{
			return String.format(Locale.ROOT, "+%.0f%%", (damagePrayerMultiplier - 1.0) * 100.0);
		}
		return String.format(Locale.ROOT, "%.0f%%", damagePrayerMultiplier * 100.0);
	}

	public boolean hasTarget()
	{
		return targetId >= 0 || !targetName.isEmpty();
	}

	public boolean isCombatDummy()
	{
		return combatDummy;
	}

	public String getTargetName()
	{
		return targetName;
	}

	public int getTargetId()
	{
		return targetId;
	}

	public boolean hasTargetHealth()
	{
		return targetCurrentHitpoints >= 0 && targetMaximumHitpoints > 0;
	}

	public String getTargetHealthText()
	{
		return hasTargetHealth()
			? String.format(Locale.ROOT, "%,d / %,d", targetCurrentHitpoints, targetMaximumHitpoints)
			: "";
	}

	public boolean isAccuracyAvailable()
	{
		return targetResult.accuracyAvailable;
	}

	public String getHitChanceText()
	{
		return targetResult.accuracyAvailable
			? String.format(Locale.ROOT, "%.1f%%", targetResult.hitChance * 100.0)
			: targetResult.accuracyStatus;
	}

	public boolean isDpsAvailable()
	{
		return targetResult.dpsAvailable;
	}

	public String getDpsText()
	{
		return targetResult.dpsAvailable
			? String.format(Locale.ROOT, "%.2f", targetResult.damagePerSecond)
			: targetResult.dpsStatus;
	}

	public String getRollText()
	{
		if (!targetResult.accuracyAvailable)
		{
			return targetResult.accuracyStatus;
		}
		return String.format(
			Locale.ROOT,
			"%,d / %,d",
			targetResult.attackRoll,
			targetResult.defenceRoll);
	}

	public String getDefenceTypeText()
	{
		return attackType == AttackType.UNKNOWN ? "Unknown" : attackType.toString();
	}

	public String getAverageHitText()
	{
		return targetResult.dpsAvailable
			? String.format(Locale.ROOT, "%.2f", targetResult.averageSuccessfulHit)
			: targetResult.dpsStatus;
	}

	public List<String> getWarnings()
	{
		return warnings;
	}

	private static String signed(int value)
	{
		return value >= 0 ? "+" + value : Integer.toString(value);
	}

	/**
	 * Reverses the health-ratio calculation used by the game server and returns
	 * the midpoint of the possible live-hitpoint range. The client does not
	 * receive an NPC's exact live hitpoints, so unavailable or invalid inputs
	 * deliberately return -1 instead of presenting a misleading value.
	 */
	static int estimateTargetCurrentHitpoints(int healthRatio, int healthScale, int maximumHitpoints)
	{
		if (healthRatio < 0 || healthScale <= 0 || healthRatio > healthScale || maximumHitpoints <= 0)
		{
			return -1;
		}
		if (healthRatio == 0)
		{
			return 0;
		}

		long minimum = 1;
		long maximum = maximumHitpoints;
		if (healthScale > 1)
		{
			long denominator = healthScale - 1L;
			if (healthRatio > 1)
			{
				minimum = ((long) maximumHitpoints * (healthRatio - 1L) + healthScale - 2L)
					/ denominator;
			}
			maximum = ((long) maximumHitpoints * healthRatio - 1L) / denominator;
			maximum = Math.min(maximum, maximumHitpoints);
		}

		return (int) ((minimum + maximum + 1L) / 2L);
	}

	private static final class AccuracyRolls
	{
		private final int attackRoll;
		private final int defenceRoll;
		private final int effectiveAccuracy;

		private AccuracyRolls(int attackRoll, int defenceRoll, int effectiveAccuracy)
		{
			this.attackRoll = attackRoll;
			this.defenceRoll = defenceRoll;
			this.effectiveAccuracy = effectiveAccuracy;
		}
	}

	private static final class StyleState
	{
		private final CombatStyle combatStyle;
		private final AttackType attackType;
		private final String name;
		private final int accuracyStyleBonus;
		private final int damageStyleBonus;

		private StyleState(
			CombatStyle combatStyle,
			AttackType attackType,
			String name,
			int accuracyStyleBonus,
			int damageStyleBonus)
		{
			this.combatStyle = combatStyle;
			this.attackType = attackType;
			this.name = name;
			this.accuracyStyleBonus = accuracyStyleBonus;
			this.damageStyleBonus = damageStyleBonus;
		}
	}

	private static final class PrayerState
	{
		private final List<String> names = new ArrayList<>();
		private String name = "None";
		private double meleeAccuracyMultiplier = 1.0;
		private double meleeStrengthMultiplier = 1.0;
		private double rangedAccuracyMultiplier = 1.0;
		private double rangedStrengthMultiplier = 1.0;
		private double magicAccuracyMultiplier = 1.0;
		private double magicDamagePercent;
		private boolean meleeStrengthMinimumOne;
		private boolean rangedStrengthMinimumOne;

		private void melee(String prayerName, double accuracy, double strength, boolean minimumOne)
		{
			addName(prayerName);
			meleeAccuracyMultiplier = Math.max(meleeAccuracyMultiplier, accuracy);
			meleeStrengthMultiplier = Math.max(meleeStrengthMultiplier, strength);
			meleeStrengthMinimumOne |= minimumOne;
		}

		private void meleeAccuracy(String prayerName, double multiplier)
		{
			addName(prayerName);
			meleeAccuracyMultiplier = Math.max(meleeAccuracyMultiplier, multiplier);
		}

		private void meleeStrength(String prayerName, double multiplier, boolean minimumOne)
		{
			addName(prayerName);
			meleeStrengthMultiplier = Math.max(meleeStrengthMultiplier, multiplier);
			meleeStrengthMinimumOne |= minimumOne;
		}

		private void ranged(String prayerName, double accuracy, double strength, boolean minimumOne)
		{
			addName(prayerName);
			rangedAccuracyMultiplier = Math.max(rangedAccuracyMultiplier, accuracy);
			rangedStrengthMultiplier = Math.max(rangedStrengthMultiplier, strength);
			rangedStrengthMinimumOne |= minimumOne;
		}

		private void magic(String prayerName, double accuracy, double damagePercent)
		{
			addName(prayerName);
			magicAccuracyMultiplier = Math.max(magicAccuracyMultiplier, accuracy);
			magicDamagePercent = Math.max(magicDamagePercent, damagePercent);
		}

		private void addName(String prayerName)
		{
			if (!names.contains(prayerName))
			{
				names.add(prayerName);
			}
		}

		private void finish()
		{
			name = names.isEmpty() ? "None" : String.join(" + ", names);
		}

		private double damageMultiplier(CombatStyle style)
		{
			switch (style)
			{
				case RANGED:
					return rangedStrengthMultiplier;
				case MAGIC:
					return 1.0 + magicDamagePercent / 100.0;
				default:
					return meleeStrengthMultiplier;
			}
		}

		private boolean affects(CombatStyle style)
		{
			return damageMultiplier(style) > 1.0;
		}
	}

	private static final class TargetResult
	{
		private final boolean accuracyAvailable;
		private final boolean dpsAvailable;
		private final String accuracyStatus;
		private final String dpsStatus;
		private final int attackRoll;
		private final int defenceRoll;
		private final int effectiveAccuracyLevel;
		private final double hitChance;
		private final int maximumHit;
		private final String maximumHitSplit;
		private final double averageSuccessfulHit;
		private final double damagePerSecond;
		private final double expectedAttackSpeedTicks;

		private TargetResult(
			boolean accuracyAvailable,
			boolean dpsAvailable,
			String accuracyStatus,
			String dpsStatus,
			int attackRoll,
			int defenceRoll,
			int effectiveAccuracyLevel,
			double hitChance,
			int maximumHit,
			String maximumHitSplit,
			double averageSuccessfulHit,
			double damagePerSecond,
			double expectedAttackSpeedTicks)
		{
			this.accuracyAvailable = accuracyAvailable;
			this.dpsAvailable = dpsAvailable;
			this.accuracyStatus = accuracyStatus;
			this.dpsStatus = dpsStatus;
			this.attackRoll = attackRoll;
			this.defenceRoll = defenceRoll;
			this.effectiveAccuracyLevel = effectiveAccuracyLevel;
			this.hitChance = hitChance;
			this.maximumHit = maximumHit;
			this.maximumHitSplit = maximumHitSplit;
			this.averageSuccessfulHit = averageSuccessfulHit;
			this.damagePerSecond = damagePerSecond;
			this.expectedAttackSpeedTicks = expectedAttackSpeedTicks;
		}

		private static TargetResult noTarget()
		{
			return unavailable("Select target");
		}

		private static TargetResult unavailable(String status)
		{
			return new TargetResult(
				false, false, status, status, 0, 0, 0, 0.0,
				-1, "", 0.0, 0.0, 0.0);
		}

		private static TargetResult immune(int attackRoll, int defenceRoll, int effectiveAccuracyLevel)
		{
			return new TargetResult(
				true, true, "", "", attackRoll, defenceRoll, effectiveAccuracyLevel, 0.0,
				0, "", 0.0, 0.0, 0.0);
		}

		private static TargetResult accuracyOnly(
			int attackRoll,
			int defenceRoll,
			int effectiveAccuracyLevel,
			double hitChance,
			String dpsStatus)
		{
			return new TargetResult(
				true, false, "", dpsStatus, attackRoll, defenceRoll,
				effectiveAccuracyLevel, hitChance, -1, "", 0.0, 0.0, 0.0);
		}

		private static TargetResult dummy(
			int maximumHit,
			String maximumHitSplit,
			String status)
		{
			return new TargetResult(
				false, false, status, status, 0, 0, 0, 0.0,
				maximumHit, maximumHitSplit, 0.0, 0.0, 0.0);
		}

		private static TargetResult complete(
			int attackRoll,
			int defenceRoll,
			int effectiveAccuracyLevel,
			double hitChance,
			int maximumHit,
			String maximumHitSplit,
			double averageSuccessfulHit,
			double damagePerSecond,
			double expectedAttackSpeedTicks)
		{
			return new TargetResult(
				true, true, "", "", attackRoll, defenceRoll,
				effectiveAccuracyLevel, hitChance, maximumHit, maximumHitSplit,
				averageSuccessfulHit, damagePerSecond, expectedAttackSpeedTicks);
		}
	}

	private static final class MagicBase
	{
		private final boolean supported;
		private final boolean poweredWeapon;
		private final boolean sanguinesti;
		private final boolean shadow;
		private final int baseMaxHit;
		private final String statusText;
		private final String warning;

		private MagicBase(
			boolean supported,
			boolean poweredWeapon,
			boolean sanguinesti,
			boolean shadow,
			int baseMaxHit,
			String statusText,
			String warning)
		{
			this.supported = supported;
			this.poweredWeapon = poweredWeapon;
			this.sanguinesti = sanguinesti;
			this.shadow = shadow;
			this.baseMaxHit = baseMaxHit;
			this.statusText = statusText;
			this.warning = warning;
		}

		private static MagicBase powered(int baseMaxHit, boolean sanguinesti)
		{
			return new MagicBase(true, true, sanguinesti, false, baseMaxHit, "", "");
		}

		private static MagicBase shadow(int baseMaxHit)
		{
			return new MagicBase(true, true, false, true, baseMaxHit, "", "");
		}

		private static MagicBase manual(int baseMaxHit)
		{
			return new MagicBase(true, false, false, false, baseMaxHit, "", "");
		}

		private static MagicBase unsupported(String statusText, String warning)
		{
			return new MagicBase(false, false, false, false, 0, statusText, warning);
		}
	}

	private static final class AmmunitionState
	{
		private static final AmmunitionState EMPTY = new AmmunitionState("", 0, null);

		private final String nameLower;
		private final int quantity;
		private final ItemEquipmentStats equipmentStats;

		private AmmunitionState(
			String nameLower,
			int quantity,
			ItemEquipmentStats equipmentStats)
		{
			this.nameLower = nameLower == null ? "" : nameLower;
			this.quantity = Math.max(0, quantity);
			this.equipmentStats = equipmentStats;
		}

		private static AmmunitionState empty()
		{
			return EMPTY;
		}

		private boolean isPresent()
		{
			return !nameLower.isEmpty() && quantity > 0;
		}
	}

	private static final class EquipmentState
	{
		private int stabAttack;
		private int slashAttack;
		private int crushAttack;
		private int meleeStrength;
		private int rangedAttack;
		private int rangedStrength;
		private int ammoRangedStrength;
        private String itemIds = "";
		private int magicAttack;
		private float magicDamage;
		private int attackSpeedTicks = DEFAULT_ATTACK_SPEED_TICKS;
		private int weaponId = -1;
		private String weaponName = "Unarmed";
		private String weaponNameLower = "unarmed";
		private String ammoNameLower = "";
		private int ammoQuantity;
		private WeaponCategory weaponCategory = WeaponCategory.UNARMED;
		private ItemEquipmentStats weaponStats;
		private boolean dizanasQuiverEquipped;
		private boolean permanentDizanasSunfire;

		private boolean slayerHelmet;
		private boolean slayerHelmetImbued;
		private boolean blackMask;
		private boolean blackMaskImbued;
		private boolean salveEquipped;
		private boolean salveImbued;
		private boolean salveEnhanced;
		private boolean dharokHelm;
		private boolean dharokBody;
		private boolean dharokLegs;
		private boolean dharokAxe;
		private boolean voidTop;
		private boolean voidRobe;
		private boolean voidGloves;
		private boolean voidMeleeHelm;
		private boolean voidRangerHelm;
		private boolean voidMageHelm;
		private boolean eliteVoidTop;
		private boolean eliteVoidRobe;
		private boolean crystalHelm;
		private boolean crystalBody;
		private boolean crystalLegs;
		private boolean bloodMoonHelm;
		private boolean bloodMoonChestplate;
		private boolean bloodMoonTassets;
		private boolean chaosGauntlets;
		private boolean smokeStaff;
        private int shieldId=-1;
        private String capeName="";
        private boolean tome;
		private int virtusPieces;
		private boolean obsidianHelm;
		private boolean obsidianBody;
		private boolean obsidianLegs;
		private boolean berserkerNecklace;
		private boolean efaritaysAid;
		private boolean silverMeleeWeapon;
		private boolean silverRangedWeapon;
		private boolean tierTwoVampyrebane;
		private boolean tierThreeVampyrebane;
		private boolean leafBladedEquipment;
		private int inquisitorPoints;
        private int soulreaperStacks = -1;
        private boolean rondacheEquipped;
        private boolean tomeEmpowered;
        private int obligatorStyleIndex = -1;

        private boolean isObligatorThrust()
        {
            return obligatorStyleIndex == 0 || obligatorStyleIndex == 1;
        }

		private void inspectItemName(String name)
		{
            if (name.equals("rondache")) rondacheEquipped = true;
			// Cosmetic kits do not change set effects. Preserve functional suffixes
			// such as (i), (e), (inactive), and (broken).
			if (name.endsWith("(or)"))
			{
				name = name.substring(0, name.length() - 4).trim();
			}
			if ((name.startsWith("void ") || name.startsWith("elite void "))
				&& name.endsWith(" (l)"))
			{
				name = name.substring(0, name.length() - 4);
			}
			if (name.contains("dizana")
				&& (name.contains("quiver") || name.contains("max cape")))
			{
				dizanasQuiverEquipped = true;
				permanentDizanasSunfire |= name.contains("blessed")
					|| name.contains("max cape");
			}
			if (name.contains("slayer helmet"))
			{
				slayerHelmet = true;
				slayerHelmetImbued = name.contains("(i)");
			}
			if (name.contains("black mask"))
			{
				blackMask = true;
				blackMaskImbued = name.contains("(i)");
			}
			if (name.contains("salve amulet"))
			{
				salveEquipped = true;
				salveImbued |= name.contains("(i)") || name.contains("(ei)");
				salveEnhanced |= name.contains("(e)") || name.contains("(ei)") || name.contains(" (e)");
			}
			dharokHelm |= name.contains("dharok's helm");
			dharokBody |= name.contains("dharok's platebody");
			dharokLegs |= name.contains("dharok's platelegs");
			dharokAxe |= name.contains("dharok's greataxe");
			voidTop |= name.equals("void knight top");
			voidRobe |= name.equals("void knight robe");
			voidGloves |= name.equals("void knight gloves");
			voidMeleeHelm |= name.equals("void melee helm");
			voidRangerHelm |= name.equals("void ranger helm");
			voidMageHelm |= name.equals("void mage helm");
			eliteVoidTop |= name.equals("elite void top");
			eliteVoidRobe |= name.equals("elite void robe");
			crystalHelm |= name.equals("crystal helm");
			crystalBody |= name.equals("crystal body");
			crystalLegs |= name.equals("crystal legs");
			bloodMoonHelm |= name.startsWith("blood moon helm");
			bloodMoonChestplate |= name.startsWith("blood moon chestplate");
			bloodMoonTassets |= name.startsWith("blood moon tassets");
			chaosGauntlets |= name.equals("chaos gauntlets");
			smokeStaff |= name.contains("smoke battlestaff")
				|| name.contains("mystic smoke staff")
				|| name.contains("twinflame staff");
			tome |= name.contains("tome of fire") || name.contains("tome of water") || name.contains("tome of earth");
			obsidianHelm |= name.equals("obsidian helmet");
			obsidianBody |= name.equals("obsidian platebody");
			obsidianLegs |= name.equals("obsidian platelegs");
			berserkerNecklace |= name.equals("berserker necklace");
			efaritaysAid |= name.equals("efaritay's aid");
			silverRangedWeapon |= containsAny(name, "silver bolts", "blisterwood stake");
			silverMeleeWeapon |= containsAny(name,
				"blessed axe",
				"ivandis flail",
				"blisterwood flail",
				"hallowed flail",
				"silver sickle",
				"emerald sickle",
				"ruby sickle",
				"blisterwood sickle",
				"silverlight",
				"darklight",
				"arclight",
				"rod of ivandis",
				"wolfbane");
			tierTwoVampyrebane |= containsAny(name,
				"rod of ivandis",
				"ivandis flail",
				"blisterwood sickle",
				"blisterwood flail",
				"hallowed flail",
				"sunspear",
				"blisterwood stake");
			tierThreeVampyrebane |= containsAny(name,
				"ivandis flail",
				"blisterwood sickle",
				"blisterwood flail",
				"hallowed flail",
				"sunspear",
				"blisterwood stake");
			leafBladedEquipment |= containsAny(name, "leaf-bladed", "broad arrows", "broad bolts");
			if (name.startsWith("inquisitor's great helm"))
			{
				inquisitorPoints += 1;
			}
			if (name.startsWith("inquisitor's hauberk") || name.startsWith("inquisitor's plateskirt"))
			{
				inquisitorPoints += 2;
			}
			if (name.startsWith("virtus mask") || name.startsWith("virtus robe top") || name.startsWith("virtus robe bottom"))
			{
				virtusPieces++;
			}
		}

		private boolean hasSlayerDamageBonus(CombatStyle style, boolean scalesWithMeleeStrength)
		{
			// The Eclipse atlatl is Ranged for accuracy but intentionally uses the
			// Melee Slayer/Salve damage multiplier because its damage scales from Strength.
			if (style == CombatStyle.MELEE || scalesWithMeleeStrength)
			{
				return slayerHelmet || blackMask;
			}
			return slayerHelmetImbued || blackMaskImbued;
		}

		private boolean hasSlayerAccuracyBonus(CombatStyle style)
		{
			return style == CombatStyle.MELEE
				? slayerHelmet || blackMask
				: slayerHelmetImbued || blackMaskImbued;
		}

		private boolean isEnhancedSlayerStaff()
		{
			return weaponNameLower.contains("slayer's staff (e)");
		}

		private boolean hasFullDharok()
		{
			return dharokHelm && dharokBody && dharokLegs && dharokAxe;
		}

		private boolean hasMeleeVoid()
		{
			return voidMeleeHelm && voidGloves && (voidTop || eliteVoidTop) && (voidRobe || eliteVoidRobe);
		}

		private boolean hasRangedVoid()
		{
			return voidRangerHelm && voidGloves
				&& (voidTop || eliteVoidTop) && (voidRobe || eliteVoidRobe);
		}

		private boolean hasMageVoid()
		{
			return voidMageHelm && voidGloves
				&& (voidTop || eliteVoidTop) && (voidRobe || eliteVoidRobe);
		}

		private double getRangedVoidModifier()
		{
			if (!voidRangerHelm || !voidGloves || !(voidTop || eliteVoidTop) || !(voidRobe || eliteVoidRobe))
			{
				return 1.0;
			}
			return eliteVoidTop && eliteVoidRobe ? 1.125 : 1.10;
		}

		private boolean hasEliteMageVoid()
		{
			return voidMageHelm && voidGloves && eliteVoidTop && eliteVoidRobe;
		}

		private double getCrystalDamageModifier()
		{
			if (!(weaponNameLower.contains("crystal bow") || weaponNameLower.contains("bow of faerdhinen")))
			{
				return 1.0;
			}
			double bonus = (crystalHelm ? 0.025 : 0.0)
				+ (crystalBody ? 0.075 : 0.0)
				+ (crystalLegs ? 0.05 : 0.0);
			return 1.0 + bonus;
		}

		private double getCrystalAccuracyModifier()
		{
			if (!(weaponNameLower.contains("crystal bow") || weaponNameLower.contains("bow of faerdhinen")))
			{
				return 1.0;
			}
			int pieces = (crystalHelm ? 1 : 0) + (crystalLegs ? 2 : 0) + (crystalBody ? 3 : 0);
			return (20.0 + pieces) / 20.0;
		}

		private int attackBonus(AttackType attackType)
		{
			switch (attackType)
			{
				case STAB:
					return stabAttack;
				case SLASH:
					return slashAttack;
				case CRUSH:
					return crushAttack;
				case MAGIC:
					return magicAttack;
				case RANGED_LIGHT:
				case RANGED_STANDARD:
				case RANGED_HEAVY:
				case RANGED_MIXED:
					return rangedAttack;
				case UNKNOWN:
				default:
					return Math.max(stabAttack, Math.max(slashAttack, crushAttack));
			}
		}

		private AttackType bestMeleeType()
		{
			if (stabAttack >= slashAttack && stabAttack >= crushAttack)
			{
				return AttackType.STAB;
			}
			return slashAttack >= crushAttack ? AttackType.SLASH : AttackType.CRUSH;
		}

		private int bestMeleeAttackBonus()
		{
			return Math.max(stabAttack, Math.max(slashAttack, crushAttack));
		}

		private AttackType fallbackRangedType()
		{
			switch (weaponCategory)
			{
				case CROSSBOW:
				case CHINCHOMPA:
					return AttackType.RANGED_HEAVY;
				case THROWN:
					return AttackType.RANGED_LIGHT;
				case SALAMANDER:
					return AttackType.RANGED_MIXED;
				case BOW:
				default:
					return AttackType.RANGED_STANDARD;
			}
		}

		private boolean hasApplicableMeleeSalve(TargetProfile target)
		{
			return salveEquipped && target != null && target.hasAttribute("undead");
		}

		private boolean hasApplicableRangedSalve(TargetProfile target)
		{
			return target != null && target.hasAttribute("undead")
				&& salveEquipped && (salveImbued || isAtlatl());
		}

		private boolean hasApplicableMagicSalve(TargetProfile target)
		{
			return target != null && target.hasAttribute("undead") && salveEquipped && salveImbued;
		}

		private int getSalveNumerator()
		{
			return salveEnhanced ? 6 : 7;
		}

		private int getSalveDenominator()
		{
			return salveEnhanced ? 5 : 6;
		}

		private boolean isBlowpipe()
		{
			return weaponNameLower.contains("blowpipe");
		}

		private boolean hasTwoArrowsEquipped()
		{
			return ammoQuantity >= 2 && ammoNameLower.contains("arrow");
		}

		private boolean hasDragonArrows()
		{
			return ammoNameLower.contains("dragon arrow");
		}

		private boolean hasArrowEquipped()
		{
			return ammoQuantity >= 1 && ammoNameLower.contains("arrow");
		}

		private boolean hasBuiltInMagicAttack()
		{
			return MagicWeaponRules.hasBuiltInAttack(weaponNameLower);
		}

        private boolean usesSpellbookMagic()
        {
            return !hasBuiltInMagicAttack() && weaponCategory != WeaponCategory.SALAMANDER;
        }

		private boolean canCastManualSpell()
		{
			return MagicWeaponRules.canCastManualSpell(
				weaponNameLower,
				weaponStats == null ? 0 : weaponStats.getAmagic());
		}

		private boolean isTwistedBow()
		{
			return weaponNameLower.contains("twisted bow");
		}

		private boolean isShadow()
		{
			return weaponNameLower.contains("tumeken's shadow");
		}

		private boolean isSanguinesti()
		{
			return weaponNameLower.contains("sanguinesti staff");
		}

		private boolean isAtlatl()
		{
			return weaponNameLower.contains("eclipse atlatl");
		}

		private boolean isFang()
		{
			return weaponNameLower.contains("osmumten's fang");
		}

		private boolean hasFullBloodMoonSet()
		{
			return weaponNameLower.contains("dual macuahuitl")
				&& bloodMoonHelm && bloodMoonChestplate && bloodMoonTassets;
		}

		private double getObsidianDamageModifier()
		{
			if (!isObsidianWeapon())
			{
				return 1.0;
			}
			double bonus = berserkerNecklace ? 0.20 : 0.0;
			if (obsidianHelm && obsidianBody && obsidianLegs)
			{
				bonus += 0.10;
			}
			return 1.0 + bonus;
		}

		private boolean hasFullObsidian()
		{
			return obsidianHelm && obsidianBody && obsidianLegs;
		}

		private boolean isObsidianWeapon()
		{
			return containsAny(weaponNameLower, "toktz-xil-ak", "toktz-xil-ek", "toktz-mej-tal",
				"tzhaar-ket-em", "tzhaar-ket-om");
		}

		private boolean isRatBoneWeapon()
		{
			return containsAny(weaponNameLower, "bone mace", "bone shortbow", "bone staff");
		}

		private boolean isLeafBladedWeapon()
		{
			return leafBladedEquipment;
		}

		private boolean hasTierTwoVampyrebane()
		{
			return tierTwoVampyrebane;
		}

		private boolean hasTierThreeVampyrebane()
		{
			return tierThreeVampyrebane;
		}

		private boolean hasApplicableSilverWeapon(CombatStyle style)
		{
			return style == CombatStyle.RANGED ? silverRangedWeapon
				: style == CombatStyle.MELEE && silverMeleeWeapon;
		}

		private boolean hasUnsupportedMultiHitDps()
		{
			return containsAny(weaponNameLower,
				"venator bow",
                "chinchompa");
		}

		private boolean hasTome()
		{
			return tome;
		}
	}
}
