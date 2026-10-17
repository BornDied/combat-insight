package com.combatinsight.live;
import com.combatinsight.calculation.*;
import org.junit.Test;
import static org.junit.Assert.*;
public class ZulrahDamageTest {
 @Test public void aboveFiftyRerollsWithoutChangingOriginalRollRange() {
  for(int id:new int[]{2042,2043,2044}) {
   HitTransform t=RaidDamageRules.transform(id,AttackType.MAGIC,false,"tumeken's shadow",99);
   assertEquals(50,t.maximum(80,0));
   assertEquals(50,t.meanForHit(50),0);
   assertEquals(47.5,t.meanForHit(51),0);
   assertEquals((1+1275+30*47.5)/81,t.average(0,80,0),1e-10);
  }
 }
}
