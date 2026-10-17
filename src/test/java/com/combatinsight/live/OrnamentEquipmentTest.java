package com.combatinsight.live;

import java.lang.reflect.*;
import org.junit.Test;
import static org.junit.Assert.*;
import com.combatinsight.calculation.*;

public class OrnamentEquipmentTest
{
    @Test public void eliteVoidRecognizesEveryOrnamentAndLockedCombination() throws Exception
    {
        String[] suffixes = {"", " (or)", " (l)", " (l)(or)"};
        for (String helm : new String[]{"ranger", "melee", "mage"})
            for (String top : suffixes) for (String legs : suffixes)
                for (String gloves : suffixes) for (String head : suffixes)
                {
                    Object e = equipment("elite void top"+top, "elite void robe"+legs,
                        "void knight gloves"+gloves, "void "+helm+" helm"+head);
                    assertEquals(true, call(e,"has"+(helm.equals("ranger")?"Ranged":helm.equals("melee")?"Melee":"Mage")+"Void"));
                    if (helm.equals("ranger")) assertEquals(1.125,(double)call(e,"getRangedVoidModifier"),0);
                    if (helm.equals("mage")) assertEquals(true,call(e,"hasEliteMageVoid"));
                }
    }
    @Test public void missingOrBrokenPieceNeverGrantsVoidBonus() throws Exception
    {
        for(String top : new String[]{"", "elite void top (broken)", "elite void top (or) (broken)"})
            assertEquals(1.0,(double)call(equipment(top,"elite void robe (or)","void knight gloves (or)","void ranger helm (or)"),"getRangedVoidModifier"),0);
    }
    @Test public void regularAndMixedVoidDoNotReceiveEliteRangedBonus() throws Exception
    {
        for(String top : new String[]{"void knight top (or)","void knight top (l)(or)"})
            assertEquals(1.10,(double)call(equipment(top,"elite void robe (or)","void knight gloves (or)","void ranger helm (or)"),"getRangedVoidModifier"),0);
    }
    @Test public void screenshotSetupRestores24InsteadOf21() throws Exception
    {
        Object e=equipment("elite void top (or)","elite void robe (or)","void knight gloves (or)","void ranger helm (or)");
        Method max=LiveCombatSnapshot.class.getDeclaredMethod("baseMaxHit",int.class,int.class); max.setAccessible(true);
        assertEquals(21,max.invoke(null,112,58));
        assertEquals(24,max.invoke(null,(int)Math.floor(112*(double)call(e,"getRangedVoidModifier")),58));
    }
    @Test public void berserkerOrnamentPreservesObsidianWeaponBonus() throws Exception
    {
        for(String necklace : new String[]{"berserker necklace","berserker necklace (or)"})
        {
            Object e=equipment(necklace); Field f=e.getClass().getDeclaredField("weaponNameLower");f.setAccessible(true);f.set(e,"tzhaar-ket-om");
            assertEquals(1.2,(double)call(e,"getObsidianDamageModifier"),0);
        }
    }
    @Test public void existingCosmeticWeaponsKeepSpecialAndMultiHitRecognition()
    {
        for(String base : new String[]{"dragon warhammer","elder maul","bandos godsword","armadyl godsword","saradomin godsword","zamorak godsword","dragon claws","dragon dagger","osmumten's fang"})
            assertEquals(SpecialAttackWeapon.forWeapon(base),SpecialAttackWeapon.forWeapon(base+" (or)"));
        assertEquals(MultiHitWeapon.SCYTHE_OF_VITUR,MultiHitWeapon.forWeapon("sanguine scythe of vitur"));
        assertEquals(MultiHitWeapon.SCYTHE_OF_VITUR,MultiHitWeapon.forWeapon("holy scythe of vitur"));
        assertEquals(SpecialAttackWeapon.TOXIC_BLOWPIPE,SpecialAttackWeapon.forWeapon("blazing blowpipe"));
    }
    @Test public void ornamentedWarhammerAndTridentHaveTheBaseCombatCategory()
    {
        assertEquals(WeaponCategoryDatabase.find(13576),WeaponCategoryDatabase.find(26710));
        assertEquals(WeaponCategory.POWERED_STAFF,WeaponCategoryDatabase.find(33434));
    }
    @Test public void auditedCosmeticWeaponCategoriesMatchTheirBaseItems()
    {
        assertEquals("Dragon scimitar (or)",WeaponCategoryDatabase.find(4587),WeaponCategoryDatabase.find(20000));
        assertEquals("Armadyl godsword (or)",WeaponCategoryDatabase.find(11802),WeaponCategoryDatabase.find(20368));
        assertEquals("Bandos godsword (or)",WeaponCategoryDatabase.find(11804),WeaponCategoryDatabase.find(20370));
        assertEquals("Saradomin godsword (or)",WeaponCategoryDatabase.find(11806),WeaponCategoryDatabase.find(20372));
        assertEquals("Zamorak godsword (or)",WeaponCategoryDatabase.find(11808),WeaponCategoryDatabase.find(20374));
        assertEquals("Dragon pickaxe (or)",WeaponCategoryDatabase.find(12797),WeaponCategoryDatabase.find(23677));
        assertEquals("Infernal harpoon (or)",WeaponCategoryDatabase.find(21031),WeaponCategoryDatabase.find(25059));
        assertEquals("Infernal pickaxe (or)",WeaponCategoryDatabase.find(13243),WeaponCategoryDatabase.find(25063));
        assertEquals("Infernal axe (or)",WeaponCategoryDatabase.find(13241),WeaponCategoryDatabase.find(25066));
        assertEquals("Dragon harpoon (or)",WeaponCategoryDatabase.find(21028),WeaponCategoryDatabase.find(25373));
        assertEquals("Dragon pickaxe (or)",WeaponCategoryDatabase.find(12797),WeaponCategoryDatabase.find(25376));
        assertEquals("Dragon axe (or)",WeaponCategoryDatabase.find(6739),WeaponCategoryDatabase.find(25378));
        assertEquals("Abyssal whip (or)",WeaponCategoryDatabase.find(4151),WeaponCategoryDatabase.find(26482));
        assertEquals("Abyssal tentacle (or)",WeaponCategoryDatabase.find(12006),WeaponCategoryDatabase.find(26484));
        assertEquals("Rune crossbow (or)",WeaponCategoryDatabase.find(9185),WeaponCategoryDatabase.find(26486));
        assertEquals("Dragon claws (or)",WeaponCategoryDatabase.find(13652),WeaponCategoryDatabase.find(26708));
        assertEquals("Dragon warhammer (or)",WeaponCategoryDatabase.find(13576),WeaponCategoryDatabase.find(26710));
        assertEquals("Heavy ballista (or)",WeaponCategoryDatabase.find(19481),WeaponCategoryDatabase.find(26712));
        assertEquals("Elder maul (or)",WeaponCategoryDatabase.find(21003),WeaponCategoryDatabase.find(27100));
        assertEquals("Osmumten's fang (or)",WeaponCategoryDatabase.find(26219),WeaponCategoryDatabase.find(27246));
        assertEquals("Infernal harpoon (or)",WeaponCategoryDatabase.find(21031),WeaponCategoryDatabase.find(30342));
        assertEquals("Infernal pickaxe (or)",WeaponCategoryDatabase.find(13243),WeaponCategoryDatabase.find(30345));
        assertEquals("Infernal axe (or)",WeaponCategoryDatabase.find(13241),WeaponCategoryDatabase.find(30347));
        assertEquals("Dragon harpoon (or)",WeaponCategoryDatabase.find(21028),WeaponCategoryDatabase.find(30349));
        assertEquals("Dragon pickaxe (or)",WeaponCategoryDatabase.find(12797),WeaponCategoryDatabase.find(30351));
        assertEquals("Dragon axe (or)",WeaponCategoryDatabase.find(6739),WeaponCategoryDatabase.find(30352));
        assertEquals("Trident of the Swamp (o)",WeaponCategoryDatabase.find(12899),WeaponCategoryDatabase.find(33314));
        assertEquals("Uncharged toxic trident (o)",WeaponCategoryDatabase.find(12900),WeaponCategoryDatabase.find(33316));
        assertEquals("Trident of the Swamp (e) (o)",WeaponCategoryDatabase.find(22292),WeaponCategoryDatabase.find(33318));
        assertEquals("Uncharged toxic trident (e) (o)",WeaponCategoryDatabase.find(22294),WeaponCategoryDatabase.find(33320));
        assertEquals("Trident of the Seas (o)",WeaponCategoryDatabase.find(11907),WeaponCategoryDatabase.find(33322));
        assertEquals("Trident of the Seas (full) (o)",WeaponCategoryDatabase.find(11905),WeaponCategoryDatabase.find(33323));
        assertEquals("Trident of the Seas (e) (o)",WeaponCategoryDatabase.find(22288),WeaponCategoryDatabase.find(33326));
        assertEquals("Uncharged trident (e) (o)",WeaponCategoryDatabase.find(22290),WeaponCategoryDatabase.find(33328));
        assertEquals("Iban's staff (o)",WeaponCategoryDatabase.find(1410),WeaponCategoryDatabase.find(33330));
        assertEquals("Iban's staff (u) (o)",WeaponCategoryDatabase.find(12658),WeaponCategoryDatabase.find(33332));
        assertEquals("Iban's staff (o)",WeaponCategoryDatabase.find(1410),WeaponCategoryDatabase.find(33333));
        assertEquals("Soulreaper axe (o)",WeaponCategoryDatabase.find(28338),WeaponCategoryDatabase.find(33335));
        assertEquals("Uncharged trident (o)",WeaponCategoryDatabase.find(11908),WeaponCategoryDatabase.find(33434));
    }
    private static Object equipment(String... names)throws Exception
    {
        Class<?> c=Class.forName(LiveCombatSnapshot.class.getName()+"$EquipmentState");Constructor<?> ctor=c.getDeclaredConstructor();ctor.setAccessible(true);Object e=ctor.newInstance();
        Method inspect=c.getDeclaredMethod("inspectItemName",String.class);inspect.setAccessible(true);
        for(String name:names)inspect.invoke(e,name);return e;
    }
    private static Object call(Object e,String method)throws Exception
    {Method m=e.getClass().getDeclaredMethod(method);m.setAccessible(true);return m.invoke(e);}
}
