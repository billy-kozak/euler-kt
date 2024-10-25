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
import euler_kt.main.util.math.generateOrderedCombinations
import euler_kt.main.util.math.generateOrderedCombinationsOfIntegers

/**
 * Harness for Problem 32
 *
 * There's no obvious "key parameter" we can select to parameterize this solution, but we should try to pick one anyway
 * to aovoid any shenanigans with the JIT optimizer.
 *
 * So we'll 'modify' the problem slightly so that we only check for multiplicands and multipliers which, together,
 * have a length of 5 digits. Since the only possible pandigital products are 4 digits long, this doesn't actually
 * change the problem at all.
 *
 * We can prove that fact with some simple math.
 *
 * Running with any key param other than 5, still makes for a valid algorithm, but should always return 0.
 */
class Problem32(override val defaultKeyParam: Int = 5) : EulerProblem<Int, Int> {
    override fun description(): String {
        return """
            We shall say that an n-digit number is pandigital if it makes use of all the digits 1 to n exactly once; for 
            example, the 5-digit number, 15234, is 1 through 5 pandigital.
            
            The product 7254 is unusual, as the identity, 39 × 186 = 7254, containing multiplicand, multiplier, and 
            product is 1 through 9 pandigital.
            
            Find the sum of all products whose multiplicand/multiplier/product identity can be written as a 1 through 
            9 pandigital.
            
            HINT: Some products can be obtained in more than one way so be sure to only include it once in your sum.
        """.trimIndent()
    }

    override fun validate(result: Number): Boolean {
        return result == 45228
    }

    override fun run(keyParam: Int): Int {

        val pandigitals: MutableSet<Int> = mutableSetOf()

        generateOrderedCombinationsOfIntegers((9 downTo 1).toList(), keyParam).forEach { leftDigits ->

            // There are four possible places we could split the digits into a multiplicand and a multiplier
            // but instead we only check the splits where multiplicand < multiplier
            // So therefore the only two splits to check are:
            // 1 digit multiplicand, 4 digit multiplier and 2 digit multiplicand, 3 digit multiplier

            // By doing this we avoid counting the same multiplicand and multiplier twice under another permutation
            val a1 = leftDigits[0] * 10 + leftDigits[1]
            val b1 = leftDigits[2] * 100 + leftDigits[3] * 10 + leftDigits[4]
            val c1 = a1 * b1

            if(testPandigital(leftDigits, c1)) {
                // Even though we've done the work to ensure that we don't count both a * b and b * a, it is still
                // conceivable that we could count the same product twice if we could get there by two or more entirely
                // different multiplications which are all pandigital products.
                // And incredibly (!!) this so happens to be the case because:
                // 27 * 198 = 5346 and 18 * 297 = 5346
                pandigitals.add(c1)
            }

            val a2 = leftDigits[0]
            val b2 = leftDigits[1] * 1000 + b1
            val c2 = a2 * b2

            if(testPandigital(leftDigits, c2)) {
                pandigitals.add(c2)
            }
        }

        return pandigitals.sum()
    }

    private fun testPandigital(leftDigits: IntArray, product: Int): Boolean {
        val checkArray = IntArray(10) { 1 }
        checkArray[0] = 0

        for (i in leftDigits) {
            checkArray[i] -= 1
        }

        var p = product
        while (p > 0) {
            val digit = p % 10
            checkArray[digit] -= 1
            if(checkArray[digit] < 0) {
                return false
            }
            p /= 10
        }
        return checkArray.all{ it == 0 }
    }
}