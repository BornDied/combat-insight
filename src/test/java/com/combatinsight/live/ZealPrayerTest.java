package com.combatinsight.live;

import java.lang.reflect.*;
import java.util.*;
import net.runelite.api.Client;
import net.runelite.api.Prayer;
import org.junit.Test;
import static org.junit.Assert.*;

public class ZealPrayerTest
{
    @Test public void activeZealUsesItsOwnBonusesAndName() throws Exception
    {
        check(true, EnumSet.noneOf(Prayer.class), "Zeal", 1.25, 1.28);
    }
    @Test public void pietyIsNotUpgradedJustBecauseZealIsUnlocked() throws Exception
    {
        check(false, EnumSet.of(Prayer.PIETY), "Piety", 1.20, 1.23);
    }
    @Test public void switchingPrayersDoesNotRetainPreviousBonuses() throws Exception
    {
        Set<Prayer> active = EnumSet.noneOf(Prayer.class);
        check(false, active, "None", 1, 1);
        active.add(Prayer.PIETY); check(false, active, "Piety", 1.20, 1.23);
        active.clear(); check(true, active, "Zeal", 1.25, 1.28);
        active.clear(); check(false, active, "None", 1, 1);
    }
    @Test public void transientOverlapDoesNotStackPietyAndZeal() throws Exception
    {
        check(true, EnumSet.of(Prayer.PIETY), "Zeal", 1.25, 1.28);
    }
    private void check(boolean zeal, Set<Prayer> active, String name, double accuracy, double strength) throws Exception
    {
        Client client = (Client) Proxy.newProxyInstance(Client.class.getClassLoader(), new Class<?>[]{Client.class},
            (proxy, method, args) -> {
                if (method.getName().equals("isPrayerActive")) return active.contains(args[0]);
                if (method.getName().equals("getVarbitValue")) return (int) args[0] == 16577 ? (zeal ? 1 : 0) : 1; // unlocked is not active
                throw new AssertionError("Unexpected client read: " + method.getName());
            });
        Method method = LiveCombatSnapshot.class.getDeclaredMethod("getPrayerState", Client.class);
        method.setAccessible(true);
        Object state = method.invoke(null, client);
        assertEquals(name, field(state, "name"));
        assertEquals(accuracy, (double) field(state, "meleeAccuracyMultiplier"), 0);
        assertEquals(strength, (double) field(state, "meleeStrengthMultiplier"), 0);
        assertEquals(1, (double) field(state, "rangedStrengthMultiplier"), 0);
        assertEquals(0, (double) field(state, "magicDamagePercent"), 0);
    }
    private Object field(Object object, String name) throws Exception
    {
        Field field = object.getClass().getDeclaredField(name); field.setAccessible(true); return field.get(object);
    }
}
