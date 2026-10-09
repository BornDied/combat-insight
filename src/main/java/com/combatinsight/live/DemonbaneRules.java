package com.combatinsight.live;

/** Duke's demonbane vulnerability applies to the bonus, not total damage/accuracy. */
final class DemonbaneRules
{
    private DemonbaneRules() { }
    static int bonusPercent(int id, int ordinaryBonus)
    { return id==12191 || id==12195 ? ordinaryBonus*70/100 : id==7584 || id==7585 ? ordinaryBonus*115/100 : ordinaryBonus; }
}
