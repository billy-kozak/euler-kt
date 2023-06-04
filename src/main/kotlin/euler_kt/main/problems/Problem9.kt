/*
 * This file is part of euler-kt
 * Copyright (C) 2023 Bill Kozak
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
import euler_kt.main.util.math.IntegerMath

class Problem9 (
    override val defaultKeyParam: Int = 1000,
): EulerProblem<Int, Int> {
    override fun description(): String {
        return """A Pythagorean triplet is a set of three natural numbers, a < b < c, for which,
                
                a^2 + b^2 = c^2
                For example, 3^2 + 4^2 = 9 + 16 = 25 = 5^2.
                
                There exists exactly one Pythagorean triplet for which a + b + c = 1000.
                Find the product abc.
        """.trimIndent()
    }

    override fun explain(): String {
        return (
            "After some algebra, if we let x = 1000, then b = (x^2 - 2ax) / (2x - 2a) and c = x - a - b. " +
            "Since a, b, and c all positive integers, we can solve by iterating a from 1 to x / 2, to find the " +
            "first integer b."
        )
    }

    override fun validate(result: Number): Boolean {
        return result.toInt() == 31875000
    }

    override fun run(keyParam: Int): Int {
        var a = 1
        var b = 0
        while(a < keyParam / 2) {
            var dm = IntegerMath.divMod(keyParam * (keyParam - 2 * a), (keyParam - a) * 2)

            if(dm.mod == 0) {
                b = dm.div
                break
            }
            a += 1
        }
        val c = keyParam - a - b

        println("a = $a, b = $b, c = $c")

        return a * b * c
    }

}