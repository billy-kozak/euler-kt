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
import euler_kt.main.util.factorization.factorizeAllUpTo

class Problem21(override val defaultKeyParam: Int = 10000) : EulerProblem<Int, Int> {
    override fun description(): String {
        return """
            Let d(n) be defined as the sum of proper divisors of n (numbers less than n which divide evenly into n).
            If d(a) = b and d(b) = a, where a ≠ b, then a and b are an amicable pair and each of a and b are called
            amicable numbers.
            
            For example, the proper divisors of 220 are 1, 2, 4, 5, 10, 11, 20, 22,
            44, 55 and 110; therefore d(220) = 284. The proper divisors of 284 are 1,
            2, 4, 71 and 142; so d(284) = 220.
            
            Evaluate the sum of all the amicable numbers under 10000.
        """.trimIndent()
    }

    override fun validate(result: Number): Boolean {
        return result == 31626
    }

    override fun run(keyParam: Int): Int {
        val factorized = factorizeAllUpTo(keyParam)
        val sums = IntArray(keyParam)
        var count = 0

        /** We know that prime numbers can't be amicable, so might as well start from 4 */
        for(i in 4 until keyParam) {
            val factorSum = factorized[i].sumAllProperFactors()
            if(factorSum >= keyParam) {
                continue
            }
            sums[i] = factorSum.toInt()

            if (sums[factorSum.toInt()] == i && i != factorSum.toInt()) {
                count += i + factorSum.toInt()
            }
        }

        return count
    }
}