package com.combatinsight.calculation;

/** Per-hitsplat encounter transform, including random caps. Applied after flat armour. */
public final class HitTransform
{
    public static final HitTransform IDENTITY = new HitTransform(1,1,0,-1);
    private final int numerator, denominator, minimum, cap, accurateFloor;
    private HitTransform(int numerator, int denominator, int minimum, int cap)
    { this(numerator, denominator, minimum, cap, 1); }
    private HitTransform(int numerator, int denominator, int minimum, int cap, int accurateFloor)
    { this.numerator=numerator; this.denominator=denominator; this.minimum=minimum; this.cap=cap; this.accurateFloor=accurateFloor; }
    /** Clamp accurate rolls before armour and encounter transforms; never transforms misses. */
    public HitTransform withAccurateFloor(int floor)
    { return new HitTransform(numerator, denominator, minimum, cap, Math.max(1,floor)); }
    public static HitTransform division(int divisor, int minimum) { return new HitTransform(1,Math.max(1,divisor),minimum,-1); }
    public static HitTransform multiplier(int numerator, int denominator) { return new HitTransform(numerator,Math.max(1,denominator),0,-1); }
    public static HitTransform randomCap(int cap) { return new HitTransform(1,1,0,Math.max(0,cap)); }
    public int maxForHit(int hit)
    { return cap >= 0 ? Math.min(hit,cap) : Math.max(minimum,(int)((long)hit*numerator/denominator)); }
    public double meanForHit(int hit)
    {
        if (cap < 0) return maxForHit(hit);
        long total=0;
        for(int limit=0;limit<=cap;limit++) total+=Math.min(hit,limit);
        return (double)total/(cap+1);
    }
    public int maximum(int maximum, int armour) { return maxForHit(DamageRoll.maximumSuccessfulHit(Math.max(accurateFloor,maximum),armour)); }
    public double average(int minimum, int maximum, int armour)
    {
        int upper=Math.max(0,maximum), lower=Math.max(0,Math.min(minimum,upper));
        double total=0;
        for(int roll=lower;roll<=upper;roll++) total+=meanForHit(DamageRoll.transformSuccessfulHit(Math.max(accurateFloor,roll),armour));
        return total/(upper-lower+1);
    }
    public double averageWithProc(int minimum, int maximum, int armour, double probability, int bonus)
    {
        int upper=Math.max(0,maximum), lower=Math.max(0,Math.min(minimum,upper));
        double total=0;
        for(int roll=lower;roll<=upper;roll++)
        {
            int hit=DamageRoll.transformSuccessfulHit(Math.max(accurateFloor,roll),armour);
            total+=(1-probability)*meanForHit(hit)+probability*meanForHit(hit+bonus);
        }
        return total/(upper-lower+1);
    }
}
