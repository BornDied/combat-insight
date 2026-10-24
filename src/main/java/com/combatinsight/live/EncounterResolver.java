package com.combatinsight.live;

import com.combatinsight.calculation.AttackType;
import com.combatinsight.calculation.TargetProfile;

/** Resolves shared DT2 IDs using observed combat level or an explicit override. */
public final class EncounterResolver
{
    private EncounterResolver() { }

    public static TargetProfile resolve(int id, EncounterContext context)
    {
        TargetProfile base = TargetDatabase.find(id);
        if (TargetMechanics.isWardenCore(id))
        {
            // Wiki's Core-ejected variant is indexed by the exposed body, not the live Core.
            TargetProfile core=TargetDatabase.find(id==11770 ? 11758 : 11755);
            return core.withEncounterIdentity(id, context==null ? 0 : context.npcSize);
        }
        if (base == null) return null;
        EncounterContext c = context == null ? EncounterContext.UNKNOWN : context;
        if(id==16640 && (c.borkStunned || c.borkQuartile>=0))
            return base.withEncounterStats(base.getDefenceLevel(),base.getMagicLevel(),base.getHitpoints(),
                base.defenceBonus(AttackType.SLASH),false).withFlatArmour(c.borkStunned?0:8+2*c.borkQuartile);
        if((id==16676 || id>=16678 && id<=16681) && c.npcSize>0)
            return base.withEncounterIdentity(id,c.npcSize);
        if (isNightmarePillar(id))
            return base.withEncounterStats(base.getDefenceLevel(), base.getMagicLevel(),
                c.nightmarePillarHp, base.defenceBonus(AttackType.SLASH), false);
        int normalLevel, awakenedLevel;
        int normalDef, awakenedDef, normalMagic, awakenedMagic, normalHp, awakenedHp;
        switch (id)
        {
            case 12191:
                normalLevel=758; awakenedLevel=1099; normalDef=275; awakenedDef=316;
                normalMagic=310; awakenedMagic=465; normalHp=485; awakenedHp=1697; break;
            case 12204: case 12205:
                normalLevel=791; awakenedLevel=1146; normalDef=250; awakenedDef=300;
                normalMagic=180; awakenedMagic=225; normalHp=900; awakenedHp=2700; break;
            case 12214:
                normalLevel=798; awakenedLevel=1157; normalDef=250; awakenedDef=287;
                normalMagic=160; awakenedMagic=280; normalHp=900; awakenedHp=2700; break;
            case 12223:
                normalLevel=784; awakenedLevel=1136; normalDef=215; awakenedDef=268;
                normalMagic=215; awakenedMagic=215; normalHp=700; awakenedHp=1400; break;
            case 12224: case 12228:
                return vardorvis(base, c, 180, 130, base.getMagicLevel(), 500);
            default: return base;
        }
        boolean awakened = c.mode == EncounterMode.AWAKENED
            || (c.mode == EncounterMode.AUTO && c.combatLevel == awakenedLevel);
        boolean normal = c.mode == EncounterMode.NORMAL
            || (c.mode == EncounterMode.AUTO && c.combatLevel == normalLevel);
        if (!awakened && !normal) return base;
        int defence=awakened ? awakenedDef : normalDef;
        int magic=awakened ? awakenedMagic : normalMagic;
        int hp=awakened ? awakenedHp : normalHp;
        if (id == 12223) return vardorvis(base, c, defence, awakened ? 181 : 145, magic, hp);
        int slash = id == 12191 ? (awakened ? 65 : 45) : base.defenceBonus(AttackType.SLASH);
        return base.withEncounterStats(defence, magic, hp, slash, false);
    }

    /** Damage uncertainty does not erase separately verified Defence and Magic drain inputs. */
    static TargetProfile resolveForTracking(int id, EncounterContext context)
    {
        TargetProfile target=resolve(id,context);
        if(target!=null && (id==16640 || id==16676))
            return target.withEncounterStats(target.getDefenceLevel(),target.getMagicLevel(),0,
                target.defenceBonus(AttackType.SLASH),false);
        return target;
    }

    public static boolean isNightmarePillar(int id)
    { return id == 9435 || id == 9438 || id == 9441 || id == 9444; }

    public static String notice(int id, EncounterContext context)
    {
        if (isNightmarePillar(id)) return context == null || context.nightmarePillarHp <= 0
            ? "Nightmare pillar: shared combat stats; HP variant unavailable" : "Nightmare pillar HP: observed encounter";
        if (!(id==12191 || id==12195 || id>=12204 && id<=12207 || id==12214 || id==12215 || id==12219 || id==12223 || id==12224 || id==12228)) return "";
        EncounterContext c=context==null ? EncounterContext.UNKNOWN : context;
        TargetProfile resolved=resolve(id,c);
        String mode = c.mode==EncounterMode.AUTO ? "Auto (observed level " + c.combatLevel + ")" : "Manual " + c.mode;
        return "DT2: " + mode + (resolved==null || resolved.isAmbiguous() ? "; unresolved variant/health" : "")
            + (id==12223 || id==12224 || id==12228 ? "; Defence uses estimated current HP" : "");
    }

    private static TargetProfile vardorvis(TargetProfile base, EncounterContext c,
        int fullDefence, int emptyDefence, int magic, int hp)
    {
        boolean unknown = c.healthRatio < 0 || c.healthScale <= 0 || c.healthRatio > c.healthScale;
        int estimatedHp = LiveCombatSnapshot.estimateTargetCurrentHitpoints(c.healthRatio, c.healthScale, hp);
        int defence = unknown ? fullDefence
            : emptyDefence + (int) ((long) (fullDefence-emptyDefence)*estimatedHp/hp);
        return base.withEncounterStats(defence, magic, hp, base.defenceBonus(AttackType.SLASH), unknown);
    }
}
