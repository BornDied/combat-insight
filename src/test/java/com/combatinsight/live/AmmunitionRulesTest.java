package com.combatinsight.live;

import java.lang.reflect.Proxy;
import net.runelite.api.Client;
import net.runelite.api.gameval.VarPlayerID;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class AmmunitionRulesTest
{
	@Test
	public void ignoresWornAmmoForBlowpipes()
	{
		assertFalse(AmmunitionRules.wornAmmoContributes("Toxic blowpipe"));
		assertFalse(AmmunitionRules.wornAmmoContributes("Blazing blowpipe"));
	}

	@Test
	public void keepsWornAmmoForOrdinaryRangedWeapons()
	{
		assertTrue(AmmunitionRules.wornAmmoContributes("Twisted bow"));
		assertTrue(AmmunitionRules.wornAmmoContributes("Armadyl crossbow"));
	}

	@Test
	public void darkBowUsesCompatibleQuiverArrowsInsteadOfWornBolts()
	{
		assertSame(
			AmmunitionRules.Source.QUIVER,
			AmmunitionRules.selectSource(
				"Dark bow",
				"Dragon bolts (e)",
				"Dragon arrows"));
	}

	@Test
	public void ordinaryAmmoSlotWinsWhenBothStacksAreCompatible()
	{
		assertSame(
			AmmunitionRules.Source.WORN,
			AmmunitionRules.selectSource(
				"Dark bow",
				"Dragon arrows",
				"Amethyst arrows"));
		assertSame(
			AmmunitionRules.Source.QUIVER,
			AmmunitionRules.selectSource(
				"Dragon crossbow",
				"Dragon arrows",
				"Ruby dragon bolts (e)"));
	}

	@Test
	public void chargedQuiverBonusOnlyAppliesToEligibleArrowsAndBolts()
	{
		assertTrue(AmmunitionRules.sunfireBonusApplies("Dark bow", "Dragon arrows"));
		assertTrue(AmmunitionRules.sunfireBonusApplies("Dragon crossbow", "Dragon bolts"));
		assertFalse(AmmunitionRules.sunfireBonusApplies("Toxic blowpipe", "Dragon arrows"));
		assertFalse(AmmunitionRules.sunfireBonusApplies("Venator bow", "Dragon arrows"));
		assertFalse(AmmunitionRules.sunfireBonusApplies("Heavy ballista", "Dragon javelin"));
	}

	@Test
	public void readsQuiverItemAndQuantityFromLiveVarplayers()
	{
		Client client = (Client) Proxy.newProxyInstance(
			Client.class.getClassLoader(),
			new Class<?>[] {Client.class},
			(proxy, method, args) ->
			{
				if ("getVarpValue".equals(method.getName()) && args != null && args.length == 1)
				{
					int varpId = (Integer) args[0];
					if (varpId == VarPlayerID.DIZANAS_QUIVER_TEMP_AMMO)
					{
						return 1122;
					}
					if (varpId == VarPlayerID.DIZANAS_QUIVER_TEMP_AMMO_AMOUNT)
					{
						return 3456;
					}
				}
				Class<?> returnType = method.getReturnType();
				if (returnType == boolean.class)
				{
					return false;
				}
				if (returnType == int.class)
				{
					return 0;
				}
				return null;
			});

		assertArrayEquals(
			new int[] {1122, 3456},
			LiveCombatSnapshot.readQuiverAmmunitionValues(client));
	}
}
