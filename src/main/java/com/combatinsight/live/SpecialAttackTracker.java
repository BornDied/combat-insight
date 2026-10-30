package com.combatinsight.live;

import com.combatinsight.calculation.SpecialAttackWeapon;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.NPC;

/** Local special attribution; exact target/tick batches follow RuneLite Special Attack Counter. */
public final class SpecialAttackTracker
{
    private final TargetEffectTracker targetEffects=new TargetEffectTracker();
    private final Map<NPC,Integer> uncertain=new IdentityHashMap<>();
    private int previousEnergy=-1;
    private DefenceTrackingMode trackingMode=DefenceTrackingMode.LOCAL;
    private EncounterMode encounterMode=EncounterMode.AUTO;
    private RaidScaling raidScaling=RaidScaling.DEFAULT;
    private PendingSpecial pendingSpecial;

    public void setTrackingMode(DefenceTrackingMode next)
    {
        if(next==null)next=DefenceTrackingMode.LOCAL;
        if(trackingMode!=next){clearTrackedEffects();trackingMode=next;}
    }
    public void clearTrackedEffects()
    { pendingSpecial=null;uncertain.clear();targetEffects.clearReductions(); }
    public void setEncounterMode(EncounterMode next)
    {
        if(encounterMode!=next){pendingSpecial=null;uncertain.clear();encounterMode=next;targetEffects.setEncounterMode(next);}
    }
    public void setRaidScaling(RaidScaling next)
    {
        if(!raidScaling.equals(next)){pendingSpecial=null;uncertain.clear();raidScaling=next;targetEffects.setRaidScaling(next);}
    }
    public void resetEnergy(int energy) { previousEnergy=energy;pendingSpecial=null; }

    public boolean onEnergyChanged(int energy,SpecialAttackWeapon weapon,NPC target,int tick)
    { return onEnergyChanged(energy,weapon,target,tick,1,null); }

    /** Distance is read from the live actor. Elder Maul uses same-cycle HP-XP evidence. */
    public boolean onEnergyChanged(int energy,SpecialAttackWeapon weapon,NPC target,int tick,int distance,Boolean elderAccurate)
    {
        if(previousEnergy<0 || energy>=previousEnergy){previousEnergy=energy;return false;}
        previousEnergy=energy;
        targetEffects.recordSpecialAttempt(weapon,target);
        if(pendingSpecial!=null)markUncertain(pendingSpecial);
        pendingSpecial=null;
        if(trackingMode!=DefenceTrackingMode.LOCAL || weapon==null || !weapon.hasTrackedTargetEffect() || target==null)return false;
        if(weapon==SpecialAttackWeapon.ELDER_MAUL)
        {
            if(elderAccurate==null){uncertain.put(target,target.getId());return false;}
            targetEffects.record(weapon,target,elderAccurate?1:0,elderAccurate,tick);
            return true;
        }
        int delay=SpecialHitTiming.delay(weapon,distance);
        if(delay<0){uncertain.put(target,target.getId());return false;}
        pendingSpecial=new PendingSpecial(weapon,target,tick+delay,delay>1);
        return true;
    }

    public boolean onHitsplat(NPC target,int amount,int tick)
    { return onHitsplat(target,amount,amount>0,tick); }
    public boolean onHitsplat(NPC target,int amount,boolean accurate,int tick)
    {
        PendingSpecial p=pendingSpecial;
        if(p==null || target==null || p.target!=target || p.hitTick!=tick)return false;
        if(target.getId()!=p.targetId){markUncertain(p);pendingSpecial=null;return false;}
        if(p.hits.size()>=8){uncertain.put(target,target.getId());pendingSpecial=null;return false;}
        p.hits.add(new Hit(Math.max(0,amount),accurate));
        return true;
    }

    public void onGameTick(int tick)
    {
        PendingSpecial p=pendingSpecial;
        if(p==null || tick<p.hitTick)return;
        // GameTick runs after this tick's NPC hitsplat updates, as in RuneLite's counter.
        pendingSpecial=null;
        if(p.target.getId()!=p.targetId){markUncertain(p);return;}
        int needed=p.weapon==SpecialAttackWeapon.TONALZTICS_OF_RALOS?2:1;
        if(p.hits.size()<needed){markUncertain(p);return;}
        if(needed==2)
        {
            apply(p,p.hits.get(0));apply(p,p.hits.get(1));
        }
        else apply(p,p.hits.get(p.delayed?0:p.hits.size()-1));
    }
    private void apply(PendingSpecial p,Hit hit)
    { targetEffects.record(p.weapon,p.target,hit.amount,hit.accurate,p.hitTick); }

    /** Keep identity-bound estimates through loading; never rebind an arbitrary new NPC. */
    public void onLoading()
    {
        if(pendingSpecial!=null)markUncertain(pendingSpecial);
        pendingSpecial=null;
    }
    public TargetEffectSnapshot getTargetEffects(NPC target,int tick)
    {
        TargetEffectSnapshot result=targetEffects.snapshot(target,tick);
        if(trackingMode==DefenceTrackingMode.OFF)
            return result.withTektonHistory(result.isTektonDefenceSpecialAttempted(),result.isTektonDefenceSpecialAttempted());
        Integer oldPhase=uncertain.get(target);
        if(oldPhase!=null && target!=null && result.hasTargetStats() && oldPhase!=target.getId()
            && ocellusPhase(oldPhase) && ocellusPhase(target.getId()))
            uncertain.remove(target);
        return uncertain.containsKey(target)?result.withUncertainReductions():result;
    }
    private void markUncertain(PendingSpecial p) { uncertain.put(p.target,p.targetId); }
    private static boolean ocellusPhase(int id) { return id==16674 || id==16676 || id==16677; }
    public void remove(NPC target)
    {
        if(pendingSpecial!=null && pendingSpecial.target==target)pendingSpecial=null;
        uncertain.remove(target);targetEffects.remove(target);
    }
    public void clear()
    {previousEnergy=-1;pendingSpecial=null;uncertain.clear();targetEffects.clear();}

    private static final class Hit
    {
        final int amount;final boolean accurate;
        Hit(int amount,boolean accurate){this.amount=amount;this.accurate=accurate;}
    }
    private static final class PendingSpecial
    {
        final SpecialAttackWeapon weapon;final NPC target;final int targetId;final int hitTick;final boolean delayed;
        final List<Hit> hits=new ArrayList<>();
        PendingSpecial(SpecialAttackWeapon weapon,NPC target,int tick,boolean delayed)
        {this.weapon=weapon;this.target=target;this.targetId=target.getId();this.hitTick=tick;this.delayed=delayed;}
    }
}
