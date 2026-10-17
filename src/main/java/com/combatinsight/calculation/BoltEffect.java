package com.combatinsight.calculation;

import java.util.Locale;

/** PvM enchanted-bolt direct-damage mixture. Delayed/status effects are not damage. */
public final class BoltEffect
{
    private BoltEffect() { }
    public static final class Result
    {
        public final int maximum;
        public final double expected, chance;
        private Result(int maximum, double expected, double chance)
        { this.maximum=maximum; this.expected=expected; this.chance=chance; }
        public SpecialAttackResult special()
        {
            return new SpecialAttackResult(SpecialAttackWeapon.ZARYTE_CROSSBOW, true,true,true,false,false,
                new int[]{maximum},chance,expected,"","","", "Proc guaranteed on accuracy success");
        }
    }
    public static boolean enchanted(String ammo) { return ammo.contains("bolt") && ammo.contains("(e)"); }

    public static Result calculate(String ammo, int maximum, int ranged, double accuracy, int currentHp,
        int armour, boolean zaryte, boolean special, boolean diary, boolean undead, boolean dragonOrFiery)
    {
        return calculate(ammo,maximum,ranged,accuracy,currentHp,armour,zaryte,special,diary,undead,dragonOrFiery,HitTransform.IDENTITY);
    }
    public static Result calculate(String ammo, int maximum, int ranged, double accuracy, int currentHp,
        int armour, boolean zaryte, boolean special, boolean diary, boolean undead, boolean dragonOrFiery, HitTransform transform)
    {
        String name=ammo.toLowerCase(Locale.ROOT);
        double p=Math.max(0,Math.min(1,accuracy));
        int baseMax=Math.max(0,maximum);
        double baseMean=baseMax<=0 ? 0 : transform.average(0,baseMax,armour);
        int normalMaximum=baseMax<=0 ? 0 : transform.maximum(baseMax,armour);
        double rate, effectMean; int effectMaximum; boolean ignoresAccuracy;
        if (name.contains("ruby") && enchanted(name))
        {
            if (currentHp<0) return null;
            rate=.06; ignoresAccuracy=true;
            int damage=Math.min(zaryte ? 110 : 100,currentHp*(zaryte ? 22 : 20)/100);
            effectMaximum=transform.maxForHit(Math.max(0,damage-armour)); effectMean=transform.meanForHit(Math.max(0,damage-armour));
        }
        else if(name.contains("diamond") && enchanted(name))
        {
            rate=.10; ignoresAccuracy=true;
            int effectMax=baseMax*(zaryte ? 126 : 115)/100;
            effectMaximum=transform.maximum(effectMax,armour);
            effectMean=transform.average(0,effectMax,armour);
        }
        else if(name.contains("onyx") && enchanted(name))
        {
            rate=undead ? 0 : .11; ignoresAccuracy=false;
            int effectMax=baseMax*(zaryte ? 132 : 120)/100;
            effectMaximum=transform.maximum(effectMax,armour);
            effectMean=transform.average(0,effectMax,armour);
        }
        else if(name.contains("dragonstone") && enchanted(name))
        {
            rate=dragonOrFiery ? 0 : .06; ignoresAccuracy=false;
            int bonus=ranged*2/(zaryte ? 9 : 10);
            effectMaximum=transform.maximum(baseMax+bonus,armour);
            effectMean=transform.average(bonus,baseMax+bonus,armour);
        }
        else return null;
        if (diary) rate*=1.1;
        // A blocked effect cannot be guaranteed by Zaryte's special.
        boolean guarantee=special && zaryte && rate>0;
        double effectProbability=ignoresAccuracy
            ? guarantee ? p+(1-p)*rate : rate
            : p*(guarantee ? 1 : rate);
        double normalProbability=guarantee ? 0 : p*(1-rate);
        double chance=ignoresAccuracy ? effectProbability+normalProbability : p;
        int resultMaximum=effectProbability>0 ? Math.max(normalProbability>0 ? normalMaximum : 0,effectMaximum) : normalMaximum;
        return new Result(resultMaximum,effectProbability*effectMean+normalProbability*baseMean,chance);
    }
}
