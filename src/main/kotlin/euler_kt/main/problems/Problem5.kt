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
import euler_kt.main.util.primes.eratosthenesWithPrecompute

class Problem5(
    override val defaultKeyParam: Int = 20
) : EulerProblem<Int, Int> {

    override fun description(): String {
        return (
            "2520 is the smallest number that can be divided by each of the numbers from 1 to 10 without any " +
            "remainder.\n\n" +
            "What is the smallest positive number that is evenly divisible by all of the numbers from 1 to 20?"
        )
    }

    override fun validate(result: Number): Boolean {
        return result == 232792560
    }

    override fun run(keyParam: Int): Int {
        val primes = eratosthenesWithPrecompute(keyParam.toLong())
        var result = 1
        for (p in primes) {
            var pint = p.toInt()
            if(pint > keyParam) {
                break
            }
            var power = pint
            while ((power * pint) < keyParam) {
                power *= pint
            }
            result *= power
        }
        return result
    }
}