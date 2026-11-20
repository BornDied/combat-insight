package com.combatinsight.live;

import com.combatinsight.calculation.*;
import net.runelite.client.game.ItemEquipmentStats;
import org.junit.Test;
import static org.junit.Assert.*;

public class SunspearTest
{
    @Test public void vampyreBonusReachesLiveDamageAndAccuracy() throws Exception
    {
        LiveCombatSnapshot baseline=capture("Sunspear",16684);
        LiveCombatSnapshot sunspear=capture("Sunspear",9761);
        assertEquals(baseline.getMaxHit()*3/2,sunspear.getMaxHit());
        assertEquals(attackRoll(baseline)*5/4,attackRoll(sunspear));
        assertEquals(5,sunspear.getAttackSpeedTicks());
        assertTrue(sunspear.isDpsAvailable());
        double total=0;
        for(int roll=0;roll<=baseline.getMaxHit();roll++)total+=Math.max(1,roll)*3/2;
        double mean=total/(baseline.getMaxHit()+1);
        double chance=CombatPrediction.hitChance(attackRoll(sunspear),12096);
        assertEquals(String.format(java.util.Locale.ROOT,"%.2f",mean*chance/3),sunspear.getDpsText());
    }

    @Test public void ordinaryTargetsDoNotReceiveTheVampyreBonus() throws Exception
    {
        assertEquals(capture("Ivandis flail",16684).getMaxHit(),capture("Sunspear",16684).getMaxHit());
        assertEquals(attackRoll(capture("Ivandis flail",16684)),attackRoll(capture("Sunspear",16684)));
    }

    private static int attackRoll(LiveCombatSnapshot snapshot)
    {
        return Integer.parseInt(snapshot.getRollText().split(" / ")[0].replace(",",""));
    }

    private static LiveCombatSnapshot capture(String name,int target) throws Exception
    {
        return LiveEquipmentCaptureTest.equipped(33722,name,
            ItemEquipmentStats.builder().slot(3).astab(150).str(110).aspeed(5).build(),
            BlowpipeDart.NOT_SET,0,target,MagicSpell.NOT_SET,EncounterContext.UNKNOWN,CombatStyleOverride.MELEE);
    }
}
