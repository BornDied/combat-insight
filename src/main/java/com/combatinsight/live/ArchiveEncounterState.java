package com.combatinsight.live;

import net.runelite.api.Client;
import net.runelite.api.NPC;

/** Immutable Archive-specific observations; no server damage formula is inferred here. */
final class ArchiveEncounterState
{
    static final ArchiveEncounterState UNKNOWN=new ArchiveEncounterState(-1,0,false);
    final int partyStatus,partySize,manualPartySize;
    final boolean ezekielEnraged,impBuffUncertain;
    ArchiveEncounterState(int status,int size,boolean enrage)
    {this(status,size,enrage,false,0);}
    private ArchiveEncounterState(int status,int size,boolean enrage,boolean impBuff,int manualSize)
    {partyStatus=status;partySize=size;ezekielEnraged=enrage;impBuffUncertain=impBuff;manualPartySize=manualSize;}
    ArchiveEncounterState withPartySize(int size)
    {return new ArchiveEncounterState(partyStatus,size,ezekielEnraged,impBuffUncertain,manualPartySize);}
    ArchiveEncounterState withEzekielEnrage()
    {return new ArchiveEncounterState(partyStatus,partySize,true,impBuffUncertain,manualPartySize);}

    ArchiveEncounterState withImpBuffUncertain()
    {return new ArchiveEncounterState(partyStatus,partySize,ezekielEnraged,true,manualPartySize);}

    ArchiveEncounterState withManualPartySize(int size)
    {return new ArchiveEncounterState(partyStatus,partySize,ezekielEnraged,impBuffUncertain,size>=1 && size<=8?size:0);}

    /** Client script 10039 shows/counts every nonzero five-bit slot, including 30/31. */
    static int partySize(int status,int ownSlot,int[] slots)
    {
        if(status<1 || status>3 || ownSlot<1 || ownSlot>8 || slots==null || slots.length!=8)return 0;
        int count=0;
        for(int slot:slots){if(slot<0 || slot>31)return 0;if(slot!=0)count++;}
        return slots[ownSlot-1]!=0?count:0;
    }
    static ArchiveEncounterState observe(Client client,NPC npc)
    {
        int status=-1,size=0;
        if(client!=null && (npc==null || ArchiveEncounterRules.isArchive(npc.getId()))) {
            status=client.getVarbitValue(16386);
            int[] slots=new int[8];for(int i=0;i<8;i++)slots[i]=client.getVarbitValue(16387+i);
            size=partySize(status,client.getVarbitValue(16395),slots);
        }
        boolean enrage=false;
        if(npc!=null && npc.getId()==16652) {
            int animation=npc.getAnimation(),pose=npc.getPoseAnimation();
            enrage=animation>=14694 && animation<=14697 || pose==14696;
            // Include an uncertain threshold interval rather than publishing an ordinary
            // roll during late entry into the below-one-third-health finale.
            int ratio=npc.getHealthRatio(),scale=npc.getHealthScale();
            if(ratio>=0 && scale>1 && ratio<=scale && (long)Math.max(0,ratio-1)*3<=scale-1)enrage=true;
        }
        return new ArchiveEncounterState(status,size,enrage);
    }
}
