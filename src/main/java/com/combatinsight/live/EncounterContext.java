package com.combatinsight.live;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;

/** Evidence read afresh for the active actor; never carry another target's phase or health. */
public final class EncounterContext
{
    public static final EncounterContext UNKNOWN=new EncounterContext(EncounterMode.AUTO,-1,-1,-1);
    final EncounterMode mode;
    final ArchiveEncounterState archive;
    final int combatLevel,healthRatio,healthScale,nightmarePillarHp,npcSize,protectionMask;
    final boolean borkStunned,ocellusAirborne,ocellusFinalPhase;
    final int borkQuartile,observedCurrentHp,observedMaximumHp;

    public EncounterContext(EncounterMode mode,int combatLevel,int ratio,int scale)
    { this(mode,combatLevel,ratio,scale,0); }

    EncounterContext(EncounterMode mode,int combatLevel,int ratio,int scale,int pillarHp)
    { this(mode,combatLevel,ratio,scale,pillarHp,0,-1,false,-1,-1,0,false,false); }

    private EncounterContext(EncounterMode mode,int combatLevel,int ratio,int scale,int pillarHp,
        int size,int protection,boolean stunned,int quartile,int currentHp,int maximumHp,boolean airborne,boolean finalPhase)
    {
        this(mode,combatLevel,ratio,scale,pillarHp,size,protection,stunned,quartile,currentHp,maximumHp,airborne,finalPhase,ArchiveEncounterState.UNKNOWN);
    }
    private EncounterContext(EncounterMode mode,int combatLevel,int ratio,int scale,int pillarHp,
        int size,int protection,boolean stunned,int quartile,int currentHp,int maximumHp,boolean airborne,boolean finalPhase,
        ArchiveEncounterState archive)
    {
        this.archive=archive;
        this.mode=mode==null?EncounterMode.AUTO:mode;
        this.combatLevel=combatLevel;this.healthRatio=ratio;this.healthScale=scale;
        this.nightmarePillarHp=pillarHp;this.npcSize=size;this.protectionMask=protection;
        this.borkStunned=stunned;this.borkQuartile=quartile;
        this.ocellusAirborne=airborne;this.ocellusFinalPhase=finalPhase;
        this.observedCurrentHp=currentHp;this.observedMaximumHp=maximumHp;
    }

    public static EncounterContext from(NPC npc,EncounterMode mode)
    {
        if(npc==null)return new EncounterContext(mode,-1,-1,-1);
        NPCComposition definition=npc.getTransformedComposition();
        if(definition==null)definition=npc.getComposition();
        int animation=npc.getAnimation(),pose=npc.getPoseAnimation();
        boolean bork=npc.getId()==16640;
        boolean stunned=bork && (animation==14538 || pose==14538 || animation>=14543 && animation<=14545);
        // Named RuneLite Bork enrage sequences; a dead Smorks is the lowest HP quartile.
        boolean enraged=bork && (animation>=14546 && animation<=14552 || animation==14556 || animation==14557);
        return new EncounterContext(mode,npc.getCombatLevel(),npc.getHealthRatio(),npc.getHealthScale(),0,
            definition==null?0:definition.getSize(),protection(npc),stunned,enraged?3:-1,-1,0,
            npc.getId()==16677 && (animation==14841 || pose==14841),
            npc.getId()==16677 && (hasCurse(npc) || animation>=14863 && animation<=14871))
            .withArchiveState(ArchiveEncounterState.observe(null,npc));
    }

    EncounterContext withBorkQuartile(int quartile)
    { return new EncounterContext(mode,combatLevel,healthRatio,healthScale,nightmarePillarHp,npcSize,
        protectionMask,borkStunned,quartile,observedCurrentHp,observedMaximumHp,ocellusAirborne,ocellusFinalPhase,archive); }

    EncounterContext withOcellusFinalPhase()
    { return new EncounterContext(mode,combatLevel,healthRatio,healthScale,nightmarePillarHp,npcSize,
        protectionMask,borkStunned,borkQuartile,observedCurrentHp,observedMaximumHp,ocellusAirborne,true,archive); }

    EncounterContext withArchiveState(ArchiveEncounterState state)
    { return new EncounterContext(mode,combatLevel,healthRatio,healthScale,nightmarePillarHp,npcSize,
        protectionMask,borkStunned,borkQuartile,observedCurrentHp,observedMaximumHp,ocellusAirborne,ocellusFinalPhase,state); }

    /** Explicit starting roster; zero leaves HP unknown unless observed from the boss HUD. */
    public EncounterContext withArchivePartySize(int size)
    { return withArchiveState(archive.withManualPartySize(size)); }

    private static boolean hasCurse(NPC npc)
    {
        int[] archives=npc.getOverheadArchiveIds();short[] sprites=npc.getOverheadSpriteIds();
        if(archives==null || sprites==null)return false;
        for(int i=0;i<Math.min(archives.length,sprites.length);i++)
            if(archives[i]==net.runelite.api.SpriteID.OVERHEAD_PROTECT_FROM_MELEE && sprites[i]>=10 && sprites[i]<=14)return true;
        return false;
    }

    static int healthQuartile(int ratio,int scale)
    {
        if(ratio<0 || scale<=1 || ratio>scale)return -1;
        if(ratio==0)return 3;
        long denominator=scale-1;
        // Server ratio is 1+floor((scale-1)*HP/maxHP). Do not select a quartile
        // if that possible HP interval crosses a 25% threshold.
        int low=(int)Math.max(0,Math.min(3,4*(denominator-ratio+1)/denominator));
        int high=(int)Math.max(0,Math.min(3,4*(denominator-ratio)/denominator));
        return low==high?low:-1;
    }

    private static int protection(NPC npc)
    {
        int[] archives=npc.getOverheadArchiveIds();short[] sprites=npc.getOverheadSpriteIds();
        if(archives==null || sprites==null)return -1;
        int mask=0;boolean known=false;
        for(int i=0;i<Math.min(archives.length,sprites.length);i++)
        {
            if(archives[i]!=net.runelite.api.SpriteID.OVERHEAD_PROTECT_FROM_MELEE)continue;
            switch(sprites[i])
            {
                case 0:case 12:mask|=1;known=true;break;
                case 1:case 13:mask|=2;known=true;break;
                case 2:case 14:mask|=4;known=true;break;
                case 6:mask|=6;known=true;break;
                case 7:mask|=3;known=true;break;
                case 8:mask|=5;known=true;break;
                case 9:mask|=7;known=true;break;
                default:break;
            }
        }
        return known?mask:-1;
    }

    boolean protects(com.combatinsight.calculation.AttackType type)
    {
        int bit=type.isMelee()?1:type.isRanged()?2:type==com.combatinsight.calculation.AttackType.MAGIC?4:0;
        return protectionMask>=0 && (protectionMask & bit)!=0;
    }

    public static EncounterContext from(Client client,NPC npc,EncounterMode mode)
    {
        EncounterContext c=from(npc,mode);
        if(npc==null)return c.withArchiveState(ArchiveEncounterState.observe(client,null));
        int pillar=0,quartile=c.borkQuartile,current=-1,maximum=0;
        if(client.getNpcs()!=null && (EncounterResolver.isNightmarePillar(npc.getId()) || npc.getId()==16640))
        {
            for(NPC actor:client.getNpcs())
            {
                if(actor==null)continue;
                if(npc.getId()==16640 && actor.getId()==16641)
                    quartile=healthQuartile(actor.getHealthRatio(),actor.getHealthScale());
                if(EncounterResolver.isNightmarePillar(npc.getId()))
                {
                    if("Phosani's Nightmare".equals(actor.getName()))pillar=200;
                    else if("The Nightmare".equals(actor.getName()) && pillar!=200)pillar=300;
                }
            }
        }
        NPCComposition composition=npc.getComposition();
        // Same NPC identity check used by RuneLite OpponentInfoOverlay. Shared/shield
        // extra bars are deliberately excluded: they do not establish this actor's HP.
        if(composition!=null && client.getVarpValue(1683)==composition.getId()
            && client.getVarbitValue(12389)==0 && client.getVarpValue(5991)==0)
        {
            int hp=client.getVarbitValue(6099),base=client.getVarbitValue(6100);
            if(base>0 && hp>=0 && hp<=base){current=hp;maximum=base;}
        }
        return new EncounterContext(c.mode,c.combatLevel,c.healthRatio,c.healthScale,pillar,c.npcSize,
            c.protectionMask,c.borkStunned,quartile,current,maximum,c.ocellusAirborne,c.ocellusFinalPhase,ArchiveEncounterState.observe(client,npc));
    }
}
