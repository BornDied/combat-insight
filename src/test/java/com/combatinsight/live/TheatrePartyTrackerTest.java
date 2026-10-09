package com.combatinsight.live;

import org.junit.Test;
import static org.junit.Assert.*;

public class TheatrePartyTrackerTest
{
    @Test public void livingAndDeadMembersCountAndHiddenMembersCannotShrinkTheRaid()
    {
        TheatrePartyTracker tracker = new TheatrePartyTracker();
        assertEquals(3, tracker.observe(true, 2, new int[]{27, 1, 30, 0, 0}));
        assertEquals(3, tracker.observe(true, 3, new int[]{30, 30, 0, 0, 0}));
        assertEquals(3, tracker.observe(true, 2, new int[5]));
        assertEquals(4, tracker.observe(true, 2, new int[]{27, 27, 27, 27, 0}));
    }
    @Test public void invalidOrMissingInitialRosterFallsBackAndLobbyClearsOldRoster()
    {
        TheatrePartyTracker tracker = new TheatrePartyTracker();
        assertEquals(0, tracker.observe(true, 2, new int[]{27, 29, 0, 0, 0}));
        assertEquals(0, tracker.observe(true, 2, new int[5]));
        assertEquals(0, tracker.observe(true, 2, null));
        assertEquals(0, tracker.observe(true, 2, new int[]{27}));
        assertEquals(5, tracker.observe(true, 2, new int[]{27,27,27,27,30}));
        assertEquals(0, tracker.observe(true, 1, new int[]{27,27,27,0,0}));
        assertEquals(3, tracker.observe(true, 2, new int[]{27,27,27,0,0}));
        assertEquals(0, tracker.observe(false, 2, new int[]{27,27,27,0,0}));
        assertEquals(0, tracker.observe(true, 2, new int[5]));
    }
    @Test public void clearingOnLogoutPreventsCrossRaidReuse()
    {
        TheatrePartyTracker tracker = new TheatrePartyTracker();
        tracker.observe(true, 2, new int[]{27,27,27,27,27});
        tracker.clear();
        assertEquals(0, tracker.observe(true, 2, new int[5]));
    }
    @Test public void observedTheatreKeepsChambersAndManualTeamSkills()
    {
        RaidScaling manual = new RaidScaling(2,false,100,90,80,5);
        RaidScaling observed = RaidInputResolver.resolve(manual,true,true,4,1,126).withObservedTheatre(3);
        assertEquals(new RaidScaling(4,true,126,90,80,3),observed);
        assertTrue(observed.inputSummary().contains("ToB observed"));
        assertTrue(observed.inputSummary().contains("HP=90"));
        assertEquals(manual,manual.withObservedTheatre(0));
    }
}
