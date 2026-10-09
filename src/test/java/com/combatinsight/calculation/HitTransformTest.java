package com.combatinsight.calculation;
import org.junit.Test;
import static org.junit.Assert.*;
public class HitTransformTest {
 @Test public void dividesEachIntegerRollBeforeAveraging() {
  assertEquals(1.2,HitTransform.division(3,0).average(0,9,0),1e-12);
  assertEquals(3,HitTransform.division(3,0).maximum(9,0));
 }
 @Test public void verzikIsUniformRandomCapNotSimpleClamp() {
  HitTransform t=HitTransform.randomCap(10);
  assertEquals(5,t.meanForHit(40),1e-12);
  assertEquals(10,t.maximum(40,0));
  assertEquals(19.0/11,t.meanForHit(2),1e-12);
 }
 @Test public void scytheTransformsEachHitsplat() {
  MultiHitResult r=MultiHitWeapon.SCYTHE_OF_VITUR.calculate(0,40,0,1,3,5,false,false,HitTransform.randomCap(10));
  assertArrayEquals(new int[]{10,10,10},r.getMaximumHits());
  assertTrue(r.getExpectedDamagePerAttack()<15);
 }
 @Test public void sanguinestiProcIsTransformedBeforeAverage() {
  HitTransform t=HitTransform.randomCap(3);
  assertEquals(1.4658536585365853,t.averageWithProc(0,40,0,.2,8),1e-12);
 }
}
