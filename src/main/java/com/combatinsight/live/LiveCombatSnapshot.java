package com.combatinsight.live;

import com.combatinsight.calculation.BlowpipeDart;
import com.combatinsight.calculation.AttackType;
import com.combatinsight.calculation.CombatPrediction;
import com.combatinsight.calculation.CombatStyle;
import com.combatinsight.calculation.CombatStyleOverride;
import com.combatinsight.calculation.DamageRoll;
import com.combatinsight.calculation.MagicSpell;
import com.combatinsight.calculation.MultiHitResult;
import com.combatinsight.calculation.MultiHitWeapon;
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
	private final String targetName;
	private final int targetId;
	private final int targetCurrentHitpoints;
	private final int targetMaximumHitpoints;
	private final boolean maxHitAvailable;
	private final int maxHit;
	private final String maxHitStatusText;
	private final TargetResult targetResult;
	private final boolean targetImmune;
	private final List<String> warnings;

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
		String targetName,
		int targetId,
		int targetCurrentHitpoints,
		int targetMaximumHitpoints,
		boolean maxHitAvailable,
		int maxHit,
		String maxHitStatusText,
		TargetResult targetResult,
		boolean targetImmune,
		List<String> warnings)
	{
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
		this.targetName = targetName;
		this.targetId = targetId;
		this.targetCurrentHitpoints = targetCurrentHitpoints;
		this.targetMaximumHitpoints = targetMaximumHitpoints;
		this.maxHitAvailable = maxHitAvailable;
		this.maxHit = maxHit;
		this.maxHitStatusText = maxHitStatusText;
		this.targetResult = targetResult;
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
			"",
			-1,
			-1,
			0,
			false,
			0,
			"Unavailable",
			TargetResult.noTarget(),
			false,
			Collections.emptyList());
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
		int targetHealthScale)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return empty();
		}

		EquipmentState equipment = readEquipment(client, itemManager);
		StyleState style = getStyleState(client, equipment, manualSpell);
		if (combatStyleOverride != CombatStyleOverride.AUTO)
		{
			style = getForcedStyle(client, combatStyleOverride, equipment);
		}
		PrayerState prayer = getPrayerState(client);
		List<String> warnings = new ArrayList<>();

		int targetId = retainedTargetId;
		String targetName = retainedTargetName == null ? "" : retainedTargetName;
		if (targetId >= 0 && targetName.trim().isEmpty())
		{
			targetName = "NPC #" + targetId;
		}
		TargetProfile target = targetId < 0 ? null : TargetDatabase.find(targetId);
		boolean targetDataAvailable = target != null && !target.isAmbiguous();
		int targetMaximumHitpoints = targetDataAvailable ? Math.max(0, target.getHitpoints()) : 0;
		int targetCurrentHitpoints = estimateTargetCurrentHitpoints(
			targetHealthRatio,
			targetHealthScale,
			targetMaximumHitpoints);
		if (TargetMechanics.isInTombsOfAmascut(targetId))
		{
			warnings.add("ToA invocation input: " + Math.max(0, toaInvocationLevel));
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
		int minimumHit = 0;
		int maxHit = 0;
		boolean maxHitAvailable = true;
		String maxHitStatusText = "";
		String weaponOrSpellName = equipment.weaponName;

		boolean eligibleSlayerGear = equipment.hasSlayerDamageBonus(style.combatStyle, equipment.isAtlatl());
		boolean useSlayerBonus = applySlayerBonus && eligibleSlayerGear;
		boolean slayerInputUsedByWeapon = equipment.isEnhancedSlayerStaff()
			&& manualSpell == MagicSpell.MAGIC_DART;
		boolean eligibleSlayerAccuracyGear = equipment.hasSlayerAccuracyBonus(style.combatStyle);
		boolean useAccuracySlayerBonus = applySlayerBonus
			&& eligibleSlayerAccuracyGear;
		if (applySlayerBonus && !eligibleSlayerGear && !slayerInputUsedByWeapon)
		{
			warnings.add(style.combatStyle == CombatStyle.MELEE
				? "Slayer bonus needs an eligible helm or mask"
				: "Ranged/Magic Slayer bonus needs an imbued helm or mask");
		}
		else if (!applySlayerBonus && eligibleSlayerGear)
		{
			warnings.add("Slayer bonus is off in Calculation inputs");
		}
		else if (applySlayerBonus)
		{
			warnings.add("Slayer target is being manually assumed");
		}
		if (applySlayerBonus && equipment.isAtlatl()
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
					+ style.damageStyleBonus + 8;
				if (equipment.hasMeleeVoid())
				{
					effectiveDamageLevel = (int) Math.floor(effectiveDamageLevel * 1.10);
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
				if (equipment.isFang() && style.attackType == AttackType.STAB)
				{
					int shrink = maxHit * 3 / 20;
					minimumHit = shrink;
					maxHit -= shrink;
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
					applySlayerBonus);
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

				float targetAdjustedMagicDamage = equipment.magicDamage;
				if (magicBase.shadow)
				{
					int shadowFactor = TargetMechanics.isInTombsOfAmascut(targetId) ? 4 : 3;
					targetAdjustedMagicDamage = Math.min(100.0f, targetAdjustedMagicDamage * shadowFactor);
				}
				double additiveMagicDamage = targetAdjustedMagicDamage / 100.0
					+ prayer.magicDamagePercent / 100.0;
				if (equipment.hasEliteMageVoid())
				{
					additiveMagicDamage += 0.05;
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

				maxHit = (int) Math.floor(magicBaseMaxHit * (1.0 + additiveMagicDamage));
				if (!(targetDataAvailable && equipment.hasApplicableMagicSalve(target)) && useSlayerBonus)
				{
					maxHit = scale(maxHit, 23, 20);
				}
				if (targetDataAvailable)
				{
					maxHit = applyMagicTargetDamage(maxHit, equipment, target);
					if (manualSpell.isSet()
						&& manualSpell.getElementName().equals(target.getWeaknessElement()))
					{
						maxHit += elementalBaseMax * target.getWeaknessSeverity() / 100;
					}
				}
				if (equipment.weaponNameLower.contains("dawnbringer"))
				{
					maxHit = Math.max(2, maxHit / 2);
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
				if (!magicBase.poweredWeapon && manualSpell.getSpellType() == MagicSpell.SpellType.GOD)
				{
					warnings.add("Charge and god-cape state are not detected yet");
				}
				if (!magicBase.poweredWeapon && manualSpell.getSpellType() == MagicSpell.SpellType.TARGET_DEPENDENT)
				{
					warnings.add("This spell has a target-dependent modifier");
				}
				if (!magicBase.poweredWeapon && equipment.hasTome())
				{
					warnings.add("Charged tome and matching element need verification");
				}
				break;
		}

		addConditionalWeaponWarnings(equipment, warnings);
		if (targetDataAvailable && isTargetImmune(targetId, style, equipment, target))
		{
			warnings.add("Target is immune to " + style.attackType.toString().toLowerCase(Locale.ROOT));
		}
		if (equipment.salveEquipped && !targetDataAvailable)
		{
			warnings.add("Salve bonuses need supported target attributes");
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
			attackSpeedTicks);
		boolean targetImmune = targetDataAvailable && isTargetImmune(targetId, style, equipment, target);

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
			targetName,
			targetId,
			targetCurrentHitpoints,
			targetMaximumHitpoints,
			maxHitAvailable,
			maxHit,
			maxHitStatusText,
			targetResult,
			targetImmune,
			warnings);
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
		int attackSpeedTicks)
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
		int defenceRoll = CombatPrediction.defenceRoll(
			target,
			style.attackType,
			TargetMechanics.magicUsesDefenceLevel(targetId));
		if (TargetMechanics.scalesWithToaInvocation(targetId) && toaInvocationLevel > 0)
		{
			defenceRoll = scale(defenceRoll, 250 + Math.max(0, toaInvocationLevel), 250);
		}

		if (isTargetImmune(targetId, style, equipment, target))
		{
			return TargetResult.immune(attackRoll, defenceRoll, effectiveAccuracy);
		}

		double chance;
		boolean guaranteedMaxHit = TargetMechanics.isGuaranteedMaxHitTarget(targetId);
		boolean fangAccuracy = equipment.isFang() && style.attackType == AttackType.STAB;
		if (guaranteedMaxHit)
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

		MultiHitWeapon multiHitWeapon = MultiHitWeapon.forWeapon(equipment.weaponNameLower);
		int targetMaximumHit;
		String maximumHitSplit = "";
		double averageSuccessfulHit;
		double damagePerAttack;
		double expectedAttackSpeedTicks = Math.max(1, attackSpeedTicks);
		if (multiHitWeapon != null)
		{
			MultiHitResult multiHit = multiHitWeapon.calculate(
				minimumHit,
				maximumHit,
				target.getFlatArmour(),
				chance,
				target.getSize(),
				attackSpeedTicks,
				guaranteedMaxHit,
				equipment.hasFullBloodMoonSet());
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
				: DamageRoll.maximumSuccessfulHit(maximumHit, flatArmour);
			CombatPrediction prediction = CombatPrediction.calculateWithHitChance(
				attackRoll,
				defenceRoll,
				chance,
				guaranteedMaxHit ? maximumHit : minimumHit,
				maximumHit,
				attackSpeedTicks,
				target.getFlatArmour(),
				applyFlatArmour);
			averageSuccessfulHit = prediction.getAverageSuccessfulHit();
			if (equipment.isSanguinesti())
			{
				averageSuccessfulHit += 1.6;
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
					attackRoll += scale(attackRoll, 70, 100);
				}
				else if (containsAny(weapon, "silverlight", "darklight") && target.hasAttribute("demon"))
				{
					attackRoll += scale(attackRoll, 60, 100);
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
					attackRoll += scale(attackRoll, 30, 100);
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
				if (manualSpell.getSpellType() == MagicSpell.SpellType.TARGET_DEPENDENT
					&& target.hasAttribute("demon"))
				{
					attackRoll += scale(attackRoll, 20, 100);
				}
				if (manualSpell.isSet()
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
		if (containsAny(weapon, "arclight", "emberlight") && target.hasAttribute("demon"))
		{
			maxHit += scale(maxHit, 70, 100);
		}
		else if (containsAny(weapon, "silverlight", "darklight") && target.hasAttribute("demon"))
		{
			maxHit += scale(maxHit, 60, 100);
		}
		if (containsAny(weapon, "bone claws", "burning claws") && target.hasAttribute("demon"))
		{
			maxHit += scale(maxHit, 5, 100);
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
		if (weapon.contains("dragon hunter crossbow") && target.hasAttribute("dragon"))
		{
			maxHit = scale(maxHit, 5, 4);
		}
		if (weapon.contains("scorching bow") && target.hasAttribute("demon"))
		{
			maxHit += scale(maxHit, 30, 100);
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
		if (style.combatStyle == CombatStyle.MAGIC && !equipment.hasBuiltInMagicAttack())
		{
			ticks = equipment.weaponNameLower.contains("harmonised nightmare staff")
				&& manualSpell.getSpellbook() == MagicSpell.Spellbook.STANDARD
				? 4 : equipment.weaponNameLower.contains("twinflame staff") ? 6 : 5;
		}
		else if (style.combatStyle == CombatStyle.RANGED
			&& ("Rapid".equals(style.name) || "Flare".equals(style.name)))
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
		for (int slot = 0; slot < items.length; slot++)
		{
			Item item = items[slot];
			if (item == null || item.getId() == -1)
			{
				continue;
			}

			ItemComposition composition = client.getItemDefinition(item.getId());
			String itemName = composition == null || composition.getName() == null
				? ""
				: composition.getName();
			String lowerName = itemName.toLowerCase(Locale.ROOT);
			result.inspectItemName(lowerName);

			ItemStats stats = itemManager.getItemStats(item.getId());
			ItemEquipmentStats equipment = stats == null ? null : stats.getEquipment();
			boolean wornAmmoContributes = slot != EquipmentInventorySlot.AMMO.getSlotIdx()
				|| AmmunitionRules.wornAmmoContributes(result.weaponNameLower);
			if (equipment != null && wornAmmoContributes)
			{
				result.stabAttack += equipment.getAstab();
				result.slashAttack += equipment.getAslash();
				result.crushAttack += equipment.getAcrush();
				result.meleeStrength += equipment.getStr();
				result.rangedAttack += equipment.getArange();
				result.rangedStrength += equipment.getRstr();
				result.magicAttack += equipment.getAmagic();
				result.magicDamage += equipment.getMdmg();
			}

			if (slot == EquipmentInventorySlot.WEAPON.getSlotIdx())
			{
				result.weaponId = item.getId();
				result.weaponName = itemName.isEmpty() ? "Unknown weapon" : itemName;
				result.weaponNameLower = lowerName;
				result.weaponCategory = WeaponCategoryDatabase.find(item.getId());
				result.weaponStats = equipment;
				if (equipment != null && equipment.getAspeed() > 0)
				{
					result.attackSpeedTicks = equipment.getAspeed();
				}
			}
		}

		return result;
	}

	private static StyleState getStyleState(
		Client client,
		EquipmentState equipment,
		MagicSpell manualSpell)
	{
		int styleIndex = client.getVarpValue(VarPlayerID.COM_MODE);
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
		if (manualSpell.isSet() && equipment.canCastManualSpell())
		{
			return new StyleState(CombatStyle.MAGIC, AttackType.MAGIC, "Manual casting", 0, 0);
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

		if (client.isPrayerActive(Prayer.PIETY))
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

	private static void addConditionalWeaponWarnings(EquipmentState equipment, List<String> warnings)
	{
		String weapon = equipment.weaponNameLower;
		if (containsAny(weapon, "venator bow", "chinchompa", "tonalztics of ralos"))
		{
			warnings.add("Multi-hit total needs target size and attack behaviour");
		}

		if (containsAny(weapon, "dragon hunter lance", "dragon hunter crossbow", "arclight",
			"emberlight", "barronite mace", "keris", "leaf-bladed", "rat bone", "scorching bow"))
		{
			warnings.add("Weapon damage modifier needs the target type");
		}
		if (containsAny(weapon, "craw's bow", "webweaver bow", "viggora's chainmace",
			"ursine chainmace", "thammaron's sceptre", "accursed sceptre"))
		{
			warnings.add("Wildeness weapon modifier needs location data");
		}
		if (weapon.contains("salamander"))
		{
			warnings.add("Salamander fuel and style formulas need a dedicated input");
		}
		if (equipment.inquisitorPoints > 0)
		{
			warnings.add("Inquisitor damage only applies on a crush attack type");
		}
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
		return targetResult.dpsAvailable && !targetResult.maximumHitSplit.isEmpty();
	}

	public String getMultiHitSplitText()
	{
		return targetResult.maximumHitSplit;
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

	private static final class EquipmentState
	{
		private int stabAttack;
		private int slashAttack;
		private int crushAttack;
		private int meleeStrength;
		private int rangedAttack;
		private int rangedStrength;
		private int magicAttack;
		private float magicDamage;
		private int attackSpeedTicks = DEFAULT_ATTACK_SPEED_TICKS;
		private int weaponId = -1;
		private String weaponName = "Unarmed";
		private String weaponNameLower = "unarmed";
		private WeaponCategory weaponCategory = WeaponCategory.UNARMED;
		private ItemEquipmentStats weaponStats;

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

		private void inspectItemName(String name)
		{
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

		private boolean hasBuiltInMagicAttack()
		{
			return MagicWeaponRules.hasBuiltInAttack(weaponNameLower);
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
				"chinchompa",
				"tonalztics of ralos");
		}

		private boolean hasTome()
		{
			return tome;
		}
	}
}
