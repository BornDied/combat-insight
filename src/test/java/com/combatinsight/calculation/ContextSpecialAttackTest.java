package com.combatinsight.calculation;
import org.junit.Test;
import static org.junit.Assert.*;
public class ContextSpecialAttackTest {
 @Test public void halberdSecondHitHasItsOwnReducedRoll() {
  SpecialAttackResult r=ContextSpecialAttack.calculate(SpecialAttackWeapon.CRYSTAL_HALBERD,40,1000,1000,2,0,false,false,false,false);
  double first=CombatPrediction.hitChance(1000,1000),second=CombatPrediction.hitChance(750,1000);
  assertArrayEquals(new int[]{44,44},r.getMaximumHits());
  assertEquals(1-(1-first)*(1-second),r.getHitChance(),1e-12);
  assertEquals(DamageRoll.averageSuccessfulHit(0,44,0)*(first+second),r.getExpectedDamage(),1e-12);
  assertEquals(1,ContextSpecialAttack.calculate(SpecialAttackWeapon.DRAGON_HALBERD,40,1000,1000,1,0,false,false,false,false).getMaximumHits().length);
 }
 @Test public void fangUsesTrueMaxAndDistinctToaAccuracy() {
  SpecialAttackResult normal=ContextSpecialAttack.calculate(SpecialAttackWeapon.OSMUMTENS_FANG,40,1000,1000,1,0,true,false,false,false);
  assertEquals(40,normal.getTotalMaximumHit());
  assertEquals(CombatPrediction.fangHitChance(1500,1000),normal.getHitChance(),1e-12);
  assertEquals(23*normal.getHitChance(),normal.getExpectedDamage(),1e-12);
  double p=CombatPrediction.hitChance(1500,1000);
  assertEquals(1-(1-p)*(1-p),ContextSpecialAttack.calculate(SpecialAttackWeapon.OSMUMTENS_FANG,40,1000,1000,1,0,true,true,false,false).getHitChance(),1e-12);
 }
 @Test public void scorchingDoesNotInventDirectDamageOrAverageBurn() {
  SpecialAttackResult r=ContextSpecialAttack.calculate(SpecialAttackWeapon.SCORCHING_BOW,40,1000,1000,1,0,false,false,true,false);
  assertEquals(40,r.getTotalMaximumHit());
  assertEquals(CombatPrediction.hitChance(1000,1000),r.getHitChance(),1e-12);
  assertFalse(ContextSpecialAttack.calculate(SpecialAttackWeapon.SCORCHING_BOW,40,1000,1000,1,0,false,false,false,false).isMaximumAvailable());
 }
}
