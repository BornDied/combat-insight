/*
 * Copyright (c) 2018, Raqes <j.raqes@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.combatinsight.live;

import com.combatinsight.calculation.SpecialAttackWeapon;

/** Timing adapted from RuneLite Special Attack Counter; see third-party notices. */
final class SpecialHitTiming
{
    private SpecialHitTiming() { }
    static int delay(SpecialAttackWeapon weapon,int distance)
    {
        if(weapon==SpecialAttackWeapon.TONALZTICS_OF_RALOS || weapon==SpecialAttackWeapon.ELDER_MAUL)return 2;
        if(weapon==SpecialAttackWeapon.EYE_OF_AYAK)return 5;
        if(weapon==SpecialAttackWeapon.ACCURSED_SCEPTRE || weapon==SpecialAttackWeapon.SEERCULL)
        {
            if(distance<0 || distance>64)return -1;
            int cycles=46+distance*(weapon==SpecialAttackWeapon.ACCURSED_SCEPTRE?10:5);
            return 1+cycles/30;
        }
        return 1;
    }
}

