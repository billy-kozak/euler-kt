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

abstract class Problem28(override val defaultKeyParam: Int = 1001) : EulerProblem<Int, Int> {
    override fun description(): String {
        return """
            Starting with the number 1 and moving to the right in a clockwise direction a 5 by 5 spiral is formed as follows:
            
            21 22 23 24 25
            20  7  8  9 10
            19  6  1  2 11
            18  5  4  3 12
            17 16 15 14 13
            
            It can be verified that the sum of the numbers on the diagonals is 101.
            
            What is the sum of the numbers on the diagonals in a 1001 by 1001 spiral formed in the same way?
        """.trimIndent()
    }

    override fun validate(result: Number): Boolean {
        return result == 669171001
    }


}

class Problem28a: Problem28() {
    override fun explain(): String {
        return """
            |Solve using derived, closed-form solution
        """.trimMargin()
    }

    override fun run(keyParam: Int): Int {
        val x = keyParam / 2
        val x2 = x * x
        val x3 = x2 * x

        return (16 * x3 + 30 * x2 + 26 * x + 3) / 3
    }
}

class Problem28b: Problem28() {
    override fun explain(): String {
        return """
            |Solve using derived, closed-form solution, which avoids integer overflow to the maximum extent possible
        """.trimMargin()
    }

    override fun run(keyParam: Int): Int {
        val x = keyParam / 2

        // Since one of x, x + 1 or x + 2 must be divisible by three
        // We subtract x * (x + 1) * (x + 2) from the closed-form polynomial
        // This leaves us with two terms which are both evenly divisible by three.
        val t0 = when(x % 3) {
            0 -> (x / 3) * (x + 1) + (x + 2)
            1 -> ((x + 2) / 3) * (x) * (x + 1)
            else -> ((x + 1) / 3) * (x) * (x + 2)
        }
        // For the second term we simply factor 3 out of the polynomial
        // But for the first term, we must calculate which of the three x, x + 1, or x + 2 is divisible by three

        // This way, we can perform the division by three as early as possible, thereby avoiding integer overflow to the
        // maximum possible extent

        val x2 = x * x
        val x3 = x2 * x
        val t1 = (5 * x3 + 9 * x2 + 8 * x + 1)

        return t0 + t1
    }
}