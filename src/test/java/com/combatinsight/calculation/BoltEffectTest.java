package com.combatinsight.calculation;
import org.junit.Test;
import static org.junit.Assert.*;
public class BoltEffectTest {
 @Test public void rubyUsesCurrentHealthAndZaryteCap() {
  BoltEffect.Result r=BoltEffect.calculate("ruby dragon bolts (e)",40,99,0.5,1000,0,false,false,false,false,false);
  assertEquals(100,r.maximum); assertEquals(.06*100+.94*.5*DamageRoll.averageSuccessfulHit(0,40,0),r.expected,1e-12);
  assertEquals(22,BoltEffect.calculate("ruby bolts (e)",1,99,1,100,0,true,true,false,false,false).maximum);
 }
 @Test public void diamondCanProcThroughFailedAccuracy() {
  BoltEffect.Result r=BoltEffect.calculate("diamond bolts (e)",40,99,0,1000,0,false,false,false,false,false);
  assertEquals(.1*DamageRoll.averageSuccessfulHit(0,46,0),r.expected,1e-12);
  assertEquals(.1,r.chance,1e-12);
 }
 @Test public void zaryteFailedAccuracyStillGetsOrdinaryRubyProc() {
  BoltEffect.Result r=BoltEffect.calculate("ruby bolts (e)",40,99,.5,1000,0,true,true,false,false,false);
  assertEquals((.5+.5*.06)*110,r.expected,1e-12);
 }
 @Test public void onyxDoesNotActivateOnUndead() {
  double base=.5*DamageRoll.averageSuccessfulHit(0,40,0);
  assertEquals(base,BoltEffect.calculate("onyx bolts (e)",40,99,.5,1000,0,false,false,false,true,false).expected,1e-12);
 }
 @Test public void missingRubyHealthAndUnsupportedEffectsAreExplicit() {
  assertNull(BoltEffect.calculate("ruby bolts (e)",40,99,.5,-1,0,false,false,false,false,false));
  assertNull(BoltEffect.calculate("jade bolts (e)",40,99,.5,100,0,false,false,false,false,false));
 }
}
