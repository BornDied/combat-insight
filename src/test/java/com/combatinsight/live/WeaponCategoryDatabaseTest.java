package com.combatinsight.live;

import static org.junit.Assert.assertEquals;

import com.combatinsight.calculation.AttackType;
import com.combatinsight.calculation.WeaponCategory;
import org.junit.Test;

public class WeaponCategoryDatabaseTest
{
	@Test
	public void resolvesCurrentWeaponCategoriesAndDefenceTypes()
	{
		assertEquals(WeaponCategory.THROWN, WeaponCategoryDatabase.find(12926));
		assertEquals(AttackType.RANGED_LIGHT, WeaponCategoryDatabase.find(12926).attackType(0));
		assertEquals(WeaponCategory.BOW, WeaponCategoryDatabase.find(20997));
		assertEquals(AttackType.RANGED_STANDARD, WeaponCategoryDatabase.find(20997).attackType(1));
		assertEquals(WeaponCategory.POWERED_STAFF, WeaponCategoryDatabase.find(22323));
		assertEquals(AttackType.MAGIC, WeaponCategoryDatabase.find(22323).attackType(0));
		assertEquals(WeaponCategory.BLUNT, WeaponCategory.fromDataName("blunt"));
	}
}
