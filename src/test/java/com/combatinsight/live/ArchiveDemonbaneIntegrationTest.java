package com.combatinsight.live;

import com.combatinsight.calculation.*;
import java.lang.reflect.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArchiveDemonbaneIntegrationTest
{
    @Test public void meleeAndRangedUseTheReducedBonusForAccuracyAndMaximum() throws Exception
    {
        assertModifiers(16665, "emberlight", CombatStyle.MELEE, 11700, 46);
        assertModifiers(16666, "emberlight", CombatStyle.MELEE, 11700, 46);
        assertModifiers(16684, "emberlight", CombatStyle.MELEE, 15600, 62);
        assertModifiers(16677, "scorching bow", CombatStyle.RANGED, 12400, 49);
        assertModifiers(16666, "scorching bow", CombatStyle.RANGED, 10700, 42);
        assertModifiers(16687, "emberlight", CombatStyle.MELEE, 10000, 40);
    }

    @Test public void crumbleUndeadIsNotADemonbaneSpellAndSavedDemonbaneDoesNotAffectTome() throws Exception
    {
        assertEquals(10000, accuracy(16666, "kodai wand", CombatStyle.MAGIC, MagicSpell.CRUMBLE_UNDEAD));
        assertEquals(10500, accuracy(16666, "kodai wand", CombatStyle.MAGIC, MagicSpell.DARK_DEMONBANE));
        assertEquals(11600, accuracy(16684, "kodai wand", CombatStyle.MAGIC, MagicSpell.DARK_DEMONBANE));
        assertEquals(10000, accuracy(16666, "zorya's tome", CombatStyle.MAGIC, MagicSpell.DARK_DEMONBANE));
        LiveCombatSnapshot demonbane = ArchiveMagicTest.capture(MagicSpell.DARK_DEMONBANE, 0, CombatStyleOverride.MAGIC, 7584);
        assertEquals("30", demonbane.getMaxHitText());
        assertFalse(RaidDamageRules.bypassesIceDemon(CombatStyle.MAGIC, "kodai wand", false,
            MagicSpell.CRUMBLE_UNDEAD.isDemonbane()));
    }

    private static void assertModifiers(int id, String weapon, CombatStyle style, int roll, int max) throws Exception
    {
        assertEquals(roll, accuracy(id, weapon, style, MagicSpell.FIRE_SURGE));
        Object equipment = equipment(weapon);
        String method = style == CombatStyle.MELEE ? "applyMeleeTargetDamage" : "applyRangedTargetDamage";
        assertEquals(max, invoke(method, 40, equipment, TargetDatabase.find(id)));
    }

    private static int accuracy(int id, String weapon, CombatStyle combat, MagicSpell spell) throws Exception
    {
        Class<?> type = Class.forName(LiveCombatSnapshot.class.getName() + "$StyleState");
        Constructor<?> ctor = type.getDeclaredConstructors()[0];
        ctor.setAccessible(true);
        AttackType attack = combat == CombatStyle.MAGIC ? AttackType.MAGIC
            : combat == CombatStyle.RANGED ? AttackType.RANGED_HEAVY : AttackType.SLASH;
        Object style = ctor.newInstance(combat, attack, "Test", 0, 0);
        return (int) invoke("applyTargetAccuracyModifiers", 10000, style, equipment(weapon),
            TargetDatabase.find(id), spell, false);
    }

    private static Object equipment(String weapon) throws Exception
    {
        Class<?> type = Class.forName(LiveCombatSnapshot.class.getName() + "$EquipmentState");
        Constructor<?> ctor = type.getDeclaredConstructor();
        ctor.setAccessible(true);
        Object equipment = ctor.newInstance();
        Field field = type.getDeclaredField("weaponNameLower");
        field.setAccessible(true);
        field.set(equipment, weapon);
        return equipment;
    }

    private static Object invoke(String name, Object... args) throws Exception
    {
        for (Method method : LiveCombatSnapshot.class.getDeclaredMethods())
            if (method.getName().equals(name))
            {
                method.setAccessible(true);
                return method.invoke(null, args);
            }
        throw new AssertionError(name);
    }
}
