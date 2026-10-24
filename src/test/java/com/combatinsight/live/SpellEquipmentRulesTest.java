package com.combatinsight.live;

import com.combatinsight.calculation.MagicSpell;
import org.junit.Test;
import static org.junit.Assert.*;

public class SpellEquipmentRulesTest
{
    @Test public void chargedTomesApplyAfterDamageBonusesOnlyToMatchingStandardSpells()
    {
        assertEquals(26,SpellEquipmentRules.damage(24,MagicSpell.FIRE_SURGE,20714,true));
        assertEquals(26,SpellEquipmentRules.damage(24,MagicSpell.WATER_SURGE,25574,true));
        assertEquals(26,SpellEquipmentRules.damage(24,MagicSpell.EARTH_SURGE,30064,true));
        assertEquals(24,SpellEquipmentRules.damage(24,MagicSpell.FIRE_SURGE,20716,true));
        assertEquals(24,SpellEquipmentRules.damage(24,MagicSpell.WATER_SURGE,20714,true));
        assertEquals(30,SpellEquipmentRules.damage(30,MagicSpell.ICE_BARRAGE,25574,true));
        assertEquals(20,SpellEquipmentRules.damage(20,MagicSpell.FLAMES_OF_ZAMORAK,20714,true));
        assertEquals(24,SpellEquipmentRules.damage(24,MagicSpell.FIRE_SURGE,20714,false));
    }
    @Test public void waterAccuracyUsesCurrentPvmTenPercentAndRequiresChargedTome()
    {
        assertEquals(1100,SpellEquipmentRules.accuracy(1000,MagicSpell.WATER_SURGE,25574,true));
        assertEquals(1000,SpellEquipmentRules.accuracy(1000,MagicSpell.WATER_SURGE,25576,true));
        assertEquals(1000,SpellEquipmentRules.accuracy(1000,MagicSpell.FIRE_SURGE,25574,true));
        assertEquals(1000,SpellEquipmentRules.accuracy(1000,MagicSpell.WATER_SURGE,25574,false));
    }
    @Test public void chargeRequiresActiveBuffMatchingGodCapeAndActualSpellbookAttack()
    {
        assertEquals(10,SpellEquipmentRules.chargeBonus(MagicSpell.CLAWS_OF_GUTHIX,"imbued guthix max cape",true,true));
        assertEquals(10,SpellEquipmentRules.chargeBonus(MagicSpell.SARADOMIN_STRIKE,"saradomin cape",true,true));
        assertEquals(10,SpellEquipmentRules.chargeBonus(MagicSpell.FLAMES_OF_ZAMORAK,"imbued zamorak cape",true,true));
        assertEquals(0,SpellEquipmentRules.chargeBonus(MagicSpell.FLAMES_OF_ZAMORAK,"guthix cape",true,true));
        assertEquals(0,SpellEquipmentRules.chargeBonus(MagicSpell.FLAMES_OF_ZAMORAK,"zamorak cape",false,true));
        assertEquals(0,SpellEquipmentRules.chargeBonus(MagicSpell.FLAMES_OF_ZAMORAK,"zamorak cape",true,false));
        assertEquals(0,SpellEquipmentRules.chargeBonus(MagicSpell.FIRE_SURGE,"zamorak cape",true,true));
    }
}
