/*
 * This file is part of euler-kt
 * Copyright (C) 2024 Bill Kozak
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package euler_kt.main.problems

import euler_kt.main.framework.EulerProblem

abstract class Problem26(override val defaultKeyParam: Int = 1000) : EulerProblem<Int, Int> {
    override fun description(): String {
        return """
            Problem 26: Reciprocal cycles
            A unit fraction contains 1 in the numerator. The decimal representation of the unit fractions with 
            denominators 2 to 10 are given:
            
            1/2	= 	0.5
            1/3	= 	0.(3)
            1/4	= 	0.25
            1/5	= 	0.2
            1/6	= 	0.1(6)
            1/7	= 	0.(142857)
            1/8	= 	0.125
            1/9	= 	0.(1)
            1/10	= 	0.1
            Where 0.1(6) means 0.166666..., and has a 1-digit recurring cycle. It can be seen that 1/7 has a 6-digit recurring cycle.
            
            Find the value of d < 1000 for which 1/d contains the longest recurring cycle in its decimal fraction part.
        """.trimIndent()
    }

    override fun validate(result: Number): Boolean {
        return result == 983
    }
}

class Problem26b : Problem26() {
    override fun explain(): String {
        return """
            |Naive solution
        """.trimMargin()
    }

    override fun run(keyParam: Int): Int {
        var maxCycle = 0
        var owner = 0

        for (d in 2 until keyParam) {

            val cycleLength = reciprocalCycleLength(d)

            if (cycleLength > maxCycle) {
                maxCycle = cycleLength
                owner = d
            }
        }
        return owner;
    }

    private fun reciprocalCycleLength(d: Int): Int {

        var rem = 10

        val modList = mutableListOf<Int>()

        while(true) {
            val mod = rem % d

            if (mod == 0) {
                return 0
            }

            val cycleStart = modList.indexOf(mod)
            if(cycleStart != -1) {
                return modList.size - cycleStart
            }

            modList.add(mod)
            rem = mod * 10
        }
    }
}

class Problem26a : Problem26() {
    override fun explain(): String {
        return """
            |Optimized Solution
        """.trimMargin()
    }

    override fun run(keyParam: Int): Int {
        var maxCycle = 0
        var owner = 0

        var d = keyParam - 1

        // Cycle length can be no longer than the number itself, because the cycle ends whenever the remainder of
        // a division is the same as of any previous division
        while (d > maxCycle) {
            val cycleLength = reciprocalCycleLength(d)

            if (cycleLength > maxCycle) {
                maxCycle = cycleLength
                owner = d
            }

            d -= 1
        }
        return owner;
    }

    private fun reciprocalCycleLength(d: Int): Int {

        var rem = 10
        val modMap = hashMapOf<Int, Int>()
        var i = 0

        while(true) {
            val mod = rem % d
            if (mod == 0) {
                return 0
            }

            val cycleStart = modMap[mod]
            if(cycleStart != null) {
                return i - cycleStart
            }

            modMap[mod] = i
            rem = mod * 10
            i += 1
        }
    }
}