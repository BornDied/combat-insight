package com.combatinsight.live;
import com.combatinsight.calculation.*;
import java.lang.reflect.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class SpecialInputRegressionTest
{
    @Test public void unresolvedRaidPhasesCannotShowAnOrdinaryVoidwakerSpecial() throws Exception
    {
        for (int id : new int[]{11753,11754,11755,11756,11757,11758,16678,16679,16680,16681})
        {
            SpecialAttackResult result=calculate("voidwaker",CombatStyle.MELEE,id,80);
            assertFalse("Unresolved phase "+id,result.isMaximumAvailable());
            assertFalse(result.isAccuracyAvailable());
        }
    }
    @Test public void chargingWardenBlocksEvenGuaranteedAccuracySpecials() throws Exception
    {
        for (int id : new int[]{11763,11764})
        {
            SpecialAttackResult result=calculate("voidwaker",CombatStyle.MELEE,id,80);
            assertEquals(0,result.getTotalMaximumHit());
            assertEquals(0.0,result.getExpectedDamage(),0.0);
        }
    }
    @Test public void magicPillarSpecialUsesMeleeDerivedVoidwakerRollThenDoublesIt() throws Exception
    {
        SpecialAttackResult result=calculate("voidwaker",CombatStyle.MELEE,9435,43);
        assertEquals(128,result.getTotalMaximumHit());
        assertEquals(85.0,result.getExpectedDamage(),1e-12);
        assertEquals(1.0,result.getHitChance(),1e-12);
        assertEquals(64,calculate("voidwaker",CombatStyle.MELEE,9425,43).getTotalMaximumHit());
    }
    @Test public void zulrahSpecialUsesPerRollReroll() throws Exception
    {
        SpecialAttackResult result=calculate("voidwaker",CombatStyle.MELEE,2042,80);
        assertEquals(50,result.getTotalMaximumHit());
        assertEquals((495+70*47.5)/81,result.getExpectedDamage(),1e-12);
    }
    @Test public void soulreaperFiveStacksUseUnstackedMaximum() throws Exception
    {
        SpecialAttackResult result=calculate("soulreaper axe",CombatStyle.MELEE,9425,80);
        assertEquals(104,result.getTotalMaximumHit());
        assertTrue(result.isExpectedDamageAvailable());
    }
    @Test public void toaRubySpecialUsesResolvedCurrentHitpoints() throws Exception
    {
        SpecialAttackResult result=calculate("zaryte crossbow",CombatStyle.RANGED,11789,80);
        assertTrue(result.isMaximumAvailable());
        assertEquals(44,result.getTotalMaximumHit());
    }
    @Test public void soulreaperCrushUsesSelectedDefence()
    {
        assertEquals(AttackType.CRUSH,LiveCombatSnapshot.specialDefenceAttackType(SpecialAttackWeapon.SOULREAPER_AXE,AttackType.CRUSH));
    }
    @Test public void forcedWrongStyleDoesNotFeedSpellDamageToMeleeSpecial() throws Exception
    {
        SpecialAttackResult result=calculate("dragon dagger",CombatStyle.MAGIC,9425,25);
        assertFalse(result.isMaximumAvailable());
    }
    @Test public void staffMeleeDoesNotFeedMeleeDamageToMagicSpecial() throws Exception
    {
        assertFalse(calculate("accursed sceptre",CombatStyle.MELEE,9425,25).isMaximumAvailable());
    }

    @Test public void unknownTektonHistoryCannotPromiseGuaranteedSpecial() throws Exception
    {
        SpecialAttackResult unknown = calculate("dragon warhammer",CombatStyle.MELEE,7540,43);
        assertFalse(unknown.isAccuracyAvailable());
        assertTrue(unknown.getHitChanceText().contains("history unknown"));
    }
    @Test public void knownTektonAttemptUsesOrdinaryAccuracyAndFreshHistoryKeepsFirstGuarantee() throws Exception
    {
        TargetEffectSnapshot base = TargetEffectSnapshot.unmodified(TargetDatabase.find(7540));
        assertEquals(1.0,calculate("dragon warhammer",CombatStyle.MELEE,7540,43,base).getHitChance(),0.0);
        SpecialAttackResult later = calculate("dragon warhammer",CombatStyle.MELEE,7540,43,base.withTektonHistory(true,true));
        assertTrue(later.isAccuracyAvailable());
        assertTrue(later.getHitChance()<1.0);
    }

    @Test public void borkMagicSpecialUsesMagicResistanceWithoutPhysicalArmour() throws Exception
    {
        TargetProfile base=TargetDatabase.find(16640);
        TargetProfile known=base.withEncounterStats(300,90,1500,130,false).withFlatArmour(14);
        SpecialAttackResult s=calculate("accursed sceptre",CombatStyle.MAGIC,16640,40,TargetEffectSnapshot.empty(),known);
        assertEquals(34,s.getTotalMaximumHit()); // supplied final special max 40, minus 6 magic resistance
    }

    @Test public void ralosSecondHitPredictionUsesTheOriginalDefenceFloor() throws Exception
    {
        TargetProfile known=TargetDatabase.find(16640).withEncounterStats(185,90,1500,130,false).withFlatArmour(14);
        TargetEffectSnapshot effects=new TargetEffectSnapshot(16640,300,185,90,90,300,300,true,false,false,false);
        SpecialAttackResult result=calculate("tonalztics of ralos",CombatStyle.RANGED,16640,40,effects,known);
        double first=10272.0/(2*22117),afterFirst=10272.0/(2*21547);
        double second=first*afterFirst+(1-first)*first;
        assertEquals((first+second)*136/31,result.getExpectedDamage(),1e-10);
    }

    private SpecialAttackResult calculate(String weapon,CombatStyle combat,int id,int maximum)throws Exception
    {
        return calculate(weapon,combat,id,maximum,TargetEffectSnapshot.empty());
    }
    private SpecialAttackResult calculate(String weapon,CombatStyle combat,int id,int maximum,TargetEffectSnapshot effects)throws Exception
    { return calculate(weapon,combat,id,maximum,effects,EncounterResolver.resolve(id,EncounterContext.UNKNOWN)); }
    private SpecialAttackResult calculate(String weapon,CombatStyle combat,int id,int maximum,TargetEffectSnapshot effects,TargetProfile target)throws Exception
    {
        Object eq=construct("EquipmentState"),pr=construct("PrayerState");set(eq,"weaponNameLower",weapon);
        if(weapon.equals("zaryte crossbow")) set(eq,"ammoNameLower","ruby dragon bolts (e)");
        if(weapon.equals("soulreaper axe")) {
            try { set(eq,"soulreaperStacks",5); }
            catch(NoSuchFieldException e) { throw new AssertionError("Live Soulreaper stacks input missing",e); }
        }
        Class<?> st=Class.forName(LiveCombatSnapshot.class.getName()+"$StyleState");
        Constructor<?> c=st.getDeclaredConstructors()[0];c.setAccessible(true);
        Object style=c.newInstance(combat,combat==CombatStyle.MAGIC?AttackType.MAGIC:AttackType.SLASH,"Aggressive",0,3);
        for(Method m:LiveCombatSnapshot.class.getDeclaredMethods())if(m.getName().equals("calculateSpecialAttackResult")) {
            m.setAccessible(true);
            return (SpecialAttackResult)m.invoke(null,style,eq,pr,id,"Target",target,
                true,effects,99,99,99,MagicSpell.BLOOD_BLITZ,false,0,maximum,true,maximum,false,false,200,false,99);
        }
        throw new AssertionError();
    }
    private static Object construct(String name)throws Exception
    {Constructor<?> c=Class.forName(LiveCombatSnapshot.class.getName()+"$"+name).getDeclaredConstructor();c.setAccessible(true);return c.newInstance();}
    private static void set(Object o,String name,Object v)throws Exception
    {Field f=o.getClass().getDeclaredField(name);f.setAccessible(true);f.set(o,v);}
}
