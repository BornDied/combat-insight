package com.combatinsight.live;

import com.combatinsight.calculation.SpecialAttackWeapon;
import com.combatinsight.calculation.TargetProfile;
import org.junit.Test;
import static org.junit.Assert.*;

public class DefenceCapDisplayTest
{
    @Test public void zeroRemainingNeverBecomesZeroDefenceInTheCalculator()
    {
        TargetProfile target=TargetDatabase.find(16674);
        TargetEffectState state=new TargetEffectState(target);
        state.apply(SpecialAttackWeapon.BANDOS_GODSWORD,1000,true,"Ocellus",0);
        TargetEffectSnapshot snapshot=state.snapshot();
        assertEquals(129,snapshot.getDefenceFloor());
        assertEquals(0,snapshot.getRemainingDefence());
        assertEquals(86,snapshot.getBaseReducibleDefence());
        assertTrue(snapshot.isAtDefenceFloor());
        assertEquals(129,snapshot.applyTo(target).getDefenceLevel());
        assertTrue(snapshot.getDefenceText().startsWith("129 / 215"));
        assertTrue(snapshot.getRemainingDefenceText().startsWith("0 / 86"));
    }

    @Test public void unknownTargetsCannotClaimToBeAtTheirFloor()
    {
        assertFalse(TargetEffectSnapshot.empty().isAtDefenceFloor());
        assertEquals("Unavailable",TargetEffectSnapshot.empty().getRemainingDefenceText());
    }

    @Test public void estimatedRecoveryShowsAnEstimateAboveTheFloor()
    {
        TargetEffectState state=new TargetEffectState(TargetDatabase.find(16684));
        state.apply(SpecialAttackWeapon.BANDOS_GODSWORD,1000,true,"Xarth",0);
        state.advance(100);
        assertEquals(1,state.snapshot().getRemainingDefence());
        assertFalse(state.snapshot().isAtDefenceFloor());
        assertTrue(state.snapshot().getRemainingDefenceText().endsWith("est."));
    }
}
