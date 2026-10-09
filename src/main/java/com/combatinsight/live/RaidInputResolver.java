package com.combatinsight.live;

/** Uses only observed, bounded CoX inputs. Other team skills remain user inputs. */
public final class RaidInputResolver
{
    private RaidInputResolver() { }
    public static RaidScaling resolve(RaidScaling manual, boolean automatic, boolean inRaid,
        int scaledParty, int challengeMode, int highestCombat)
    {
        if (!automatic || !inRaid || scaledParty<1 || scaledParty>100 || challengeMode<0 || challengeMode>1) return manual;
        return manual.withObservedChambers(scaledParty,challengeMode==1,highestCombat);
    }
}
