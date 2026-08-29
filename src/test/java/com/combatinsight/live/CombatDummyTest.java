package com.combatinsight.live;

import com.combatinsight.calculation.CombatStyle;
import com.combatinsight.calculation.TargetProfile;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class CombatDummyTest
{
	@Test
	public void mapsEveryKnownPohNpcAndObjectVariant()
	{
		for (CombatDummy dummy : CombatDummy.values())
		{
			assertSame(dummy, CombatDummy.forNpcId(dummy.getNpcId()));
			assertSame(dummy, CombatDummy.forObjectId(dummy.getObjectId()));
		}
		assertEquals(9, CombatDummy.values().length);
		assertNull(CombatDummy.forNpcId(-1));
		assertNull(CombatDummy.forObjectId(-1));
	}

	@Test
	public void ordinaryDummiesDoNotInventTargetTraits()
	{
		assertPlain(CombatDummy.COMBAT.getTargetProfile());
		assertPlain(CombatDummy.ORNATE.getTargetProfile());
	}

	@Test
	public void undeadDummiesRepresentUndeadDemonsAndSlayerTargets()
	{
		assertUndeadSlayer(CombatDummy.UNDEAD.getTargetProfile());
		assertUndeadSlayer(CombatDummy.ORNATE_UNDEAD.getTargetProfile());
	}

	@Test
	public void ornateVariantsExposeOnlyTheirOwnTargetTrait()
	{
		assertExclusive(CombatDummy.ORNATE_WILDERNESS, "wilderness");
		assertExclusive(CombatDummy.ORNATE_KALPHITE, "kalphite");
		assertExclusive(CombatDummy.ORNATE_KURASK, "leafy");
		assertExclusive(CombatDummy.ORNATE_VAMPYRE, "vampyre2");
		assertExclusive(CombatDummy.ORNATE_DRAGON, "dragon");
		assertFalse(CombatDummy.ORNATE_DRAGON.getTargetProfile().hasAttribute("undead"));
	}

	@Test
	public void slayerInputIsLimitedToSlayerDummyForms()
	{
		assertTrue(LiveCombatSnapshot.slayerInputApplies(true, null));
		assertTrue(LiveCombatSnapshot.slayerInputApplies(true, CombatDummy.UNDEAD));
		assertTrue(LiveCombatSnapshot.slayerInputApplies(true, CombatDummy.ORNATE_UNDEAD));
		assertFalse(LiveCombatSnapshot.slayerInputApplies(true, CombatDummy.COMBAT));
		assertFalse(LiveCombatSnapshot.slayerInputApplies(true, CombatDummy.ORNATE_DRAGON));
		assertFalse(LiveCombatSnapshot.slayerInputApplies(false, CombatDummy.UNDEAD));
	}

	@Test
	public void wildernessWeaponDamageRequiresTheWildernessDummyAndMatchingStyle()
	{
		TargetProfile wilderness = CombatDummy.ORNATE_WILDERNESS.getTargetProfile();
		TargetProfile ordinary = CombatDummy.ORNATE.getTargetProfile();

		assertEquals(60, LiveCombatSnapshot.applyWildernessDamage(
			40, "Viggora's chainmace", CombatStyle.MELEE, wilderness));
		assertEquals(60, LiveCombatSnapshot.applyWildernessDamage(
			40, "Webweaver bow", CombatStyle.RANGED, wilderness));
		assertEquals(60, LiveCombatSnapshot.applyWildernessDamage(
			40, "Accursed sceptre (a)", CombatStyle.MAGIC, wilderness));
		assertEquals(40, LiveCombatSnapshot.applyWildernessDamage(
			40, "Webweaver bow", CombatStyle.MELEE, wilderness));
		assertEquals(40, LiveCombatSnapshot.applyWildernessDamage(
			40, "Webweaver bow", CombatStyle.RANGED, ordinary));
	}

	private static void assertPlain(TargetProfile target)
	{
		assertFalse(target.isSlayerMonster());
		for (String attribute : new String[]{
			"undead", "demon", "wilderness", "kalphite", "leafy", "vampyre2", "dragon"})
		{
			assertFalse(target.hasAttribute(attribute));
		}
	}

	private static void assertUndeadSlayer(TargetProfile target)
	{
		assertTrue(target.isSlayerMonster());
		assertTrue(target.hasAttribute("undead"));
		assertTrue(target.hasAttribute("demon"));
		assertFalse(target.hasAttribute("dragon"));
	}

	private static void assertExclusive(CombatDummy dummy, String expectedAttribute)
	{
		TargetProfile target = dummy.getTargetProfile();
		assertFalse(target.isSlayerMonster());
		for (String attribute : new String[]{
			"undead", "demon", "wilderness", "kalphite", "leafy", "vampyre2", "dragon"})
		{
			assertEquals(attribute.equals(expectedAttribute), target.hasAttribute(attribute));
		}
	}
}
