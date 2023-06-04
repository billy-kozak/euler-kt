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
import euler_kt.main.util.primes.eratosthenesSequence

class Problem10(override val defaultKeyParam: Long = 2000000) : EulerProblem<Long, Long> {
    override fun description(): String {
        return "Summation of primes\n" +
                "Problem 10\n" +
                "The sum of the primes below 10 is 2 + 3 + 5 + 7 = 17.\n" +
                "\n" +
                "Find the sum of all the primes below two million."
    }

    override fun validate(result: Number): Boolean {
        return result.toLong() == 142913828922L
    }

    override fun run(keyParam: Long): Long {
        return eratosthenesSequence(keyParam).sum()
    }
}