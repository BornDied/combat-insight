package com.combatinsight.calculation;
import org.junit.Test;
import java.lang.reflect.*;
import static org.junit.Assert.*;
public class ZulrahDistributionTest {
 private HitTransform z(){return HitTransform.zulrah();}
 private Object invoke(Class<?> c,Object... args) throws Exception {
  for(Method m:c.getMethods()) if(m.getName().equals("calculate")&&m.getParameterCount()==args.length) return m.invoke(null,args);
  throw new AssertionError("Per-hit encounter transform overload missing for "+c.getSimpleName());
 }
 @Test public void rubyBoltProcRerollsIndependentlyOfNormalDistribution() throws Exception {
  BoltEffect.Result r=(BoltEffect.Result)invoke(BoltEffect.class,"ruby dragon bolts (e)",80,99,1.0,500,0,false,false,false,false,false,z());
  assertEquals(50,r.maximum);assertEquals(.06*47.5+.94*(1+1275+30*47.5)/81,r.expected,1e-10);
 }
 @Test public void zaryteDiamondTransformsItsExpandedRollRange() throws Exception {
  BoltEffect.Result r=(BoltEffect.Result)invoke(BoltEffect.class,"diamond dragon bolts (e)",80,99,1.0,500,0,true,true,false,false,false,z());
  assertEquals(50,r.maximum);assertEquals((1+1275+50*47.5)/101,r.expected,1e-10);
 }
 @Test public void voidwakerTransformsUniformFortyToOneTwenty() throws Exception {
  SpecialAttackResult r=(SpecialAttackResult)invoke(SpecialAttackCalculator.class,SpecialAttackWeapon.VOIDWAKER,80,true,10000,100,true,0,false,false,false,true,true,true,100,100,0,-1,false,false,z());
  assertEquals(50,r.getTotalMaximumHit());assertEquals((495+70*47.5)/81,r.getExpectedDamage(),1e-10);
 }
 @Test public void halberdTransformsEachHitInsteadOfTotal() throws Exception {
  SpecialAttackResult r=(SpecialAttackResult)invoke(ContextSpecialAttack.class,SpecialAttackWeapon.CRYSTAL_HALBERD,80,10000,0,5,0,false,false,false,false,false,z());
  assertEquals(100,r.getTotalMaximumHit());
  double p=CombatPrediction.hitChance(10000,0)+CombatPrediction.hitChance(7500,0);
  assertEquals(p*(1+1275+38*47.5)/89,r.getExpectedDamage(),1e-10);
 }
 @Test public void darkBowBasicTransformsEachArrow() {
  MultiHitResult r=MultiHitWeapon.DARK_BOW.calculate(0,80,0,1,5,8,false,false,z());
  assertEquals(100,r.getTotalMaximumHit());
  assertEquals(2*(1+1275+30*47.5)/81,r.getExpectedDamagePerAttack(),1e-10);
 }
}
