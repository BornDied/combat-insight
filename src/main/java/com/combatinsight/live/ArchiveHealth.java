package com.combatinsight.live;

/** Published Wiki HP table cells, October 24. Zero means unknown, never zero HP.
 * Do not interpolate: the strategy page's scaling percentage conflicts with these tables.
 */
final class ArchiveHealth
{
    private ArchiveHealth() { }
    static int maximum(int id,int size)
    {
        if(size<1 || size>8)return 0;
        int[] hp;
        switch(id) {
            case 16640: return 0;
            case 16641: return 0;
            case 16652:hp=new int[]{0,0,4350,5610,6870,8130,9390,0};break;
            case 16659:hp=new int[]{0,0,800,0,1260,0,0,1950};break;
            case 16660:hp=new int[]{0,0,1600,2060,2520,2980,3440,3910};break;
            default:return 0;
        }
        return hp[size-1];
    }
}
