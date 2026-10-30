package com.combatinsight.live;

import com.combatinsight.calculation.SpecialAttackWeapon;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArchiveBgsOverflowTest
{
    @Test public void bgsAtZeroDefenceConsumesStrengthAndAttackBeforeMagic()
    {
        for(int id:new int[]{16641,16643,16645,16647})
        {
            TargetEffectState state=new TargetEffectState(TargetDatabase.find(id));
            int defence=state.snapshot().getBaseDefence(),magic=state.snapshot().getBaseMagic();
            state.apply(SpecialAttackWeapon.BANDOS_GODSWORD,defence,true,"Archive",0);
            state.apply(SpecialAttackWeapon.BANDOS_GODSWORD,10,true,"Archive",1);
            assertEquals(0,state.snapshot().getCurrentDefence());
            assertEquals(magic-8,state.snapshot().getCurrentMagic());
        }
    }
    @Test public void priorArclightChangesTheLaterBgsSpill()
    {
        TargetEffectState state=new TargetEffectState(TargetDatabase.find(16641));
        state.apply(SpecialAttackWeapon.ARCLIGHT,1,true,"The Smorks",0);
        state.apply(SpecialAttackWeapon.BANDOS_GODSWORD,state.snapshot().getCurrentDefence(),true,"The Smorks",1);
        state.apply(SpecialAttackWeapon.BANDOS_GODSWORD,10,true,"The Smorks",2);
        assertEquals(175,state.snapshot().getCurrentMagic());
    }
    @Test public void aPositiveDefenceFloorStopsBgsBeforeAnyMagicSpill()
    {
        TargetEffectState state=new TargetEffectState(TargetDatabase.find(16674));
        state.apply(SpecialAttackWeapon.BANDOS_GODSWORD,1000,true,"Ocellus",0);
        assertEquals(129,state.snapshot().getCurrentDefence());
        assertEquals(350,state.snapshot().getCurrentMagic());
    }
}
