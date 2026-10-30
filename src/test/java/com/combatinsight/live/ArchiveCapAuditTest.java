package com.combatinsight.live;

import com.combatinsight.calculation.SpecialAttackWeapon;
import org.junit.Test;
import static org.junit.Assert.*;

/** Boundary checks for Defence floors, distinct from offensive-stat limits. */
public class ArchiveCapAuditTest
{
    @Test public void repeatedReductionsStopAtEachArchiveDefenceFloor()
    {
        for (int id : new int[]{16652,16659,16660,16667,16674,16677,16684})
            for (SpecialAttackWeapon weapon : new SpecialAttackWeapon[]{SpecialAttackWeapon.DRAGON_WARHAMMER,
                SpecialAttackWeapon.ELDER_MAUL,SpecialAttackWeapon.BANDOS_GODSWORD,
                SpecialAttackWeapon.TONALZTICS_OF_RALOS,SpecialAttackWeapon.EMBERLIGHT})
            {
                TargetEffectState state = new TargetEffectState(TargetDatabase.find(id));
                int floor=TargetDatabase.find(id).getDefenceLevel()*3/5;
                for(int hit=0;hit<50;hit++) state.apply(weapon,100,true,"Audit",0);
                assertEquals(id+" "+weapon,floor,state.snapshot().getCurrentDefence());
            }
    }
    @Test public void dreadnothHasNoDefenceFloorDespiteNewOffensiveCap()
    {
        TargetEffectState state=new TargetEffectState(TargetDatabase.find(16665));
        state.apply(SpecialAttackWeapon.BANDOS_GODSWORD,500,true,"Dreadnoth",0);
        assertEquals(0,state.snapshot().getCurrentDefence());
        assertEquals(0,TargetEffectRules.defenceFloor(TargetDatabase.find(16665)));
    }
}
