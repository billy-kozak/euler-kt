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

abstract class Problem6(
    override val defaultKeyParam: Long = 100L
): EulerProblem<Long, Long> {
    override fun description(): String {
        return (
            "The sum of the squares of the first ten natural numbers is,\n\n" +
                "1^2 + 2^2 + ... + 10^2 = 385\n\n" +
                "The square of the sum of the first ten natural numbers is,\n\n" +
                "(1 + 2 + ... + 10)^2 = 55^2 = 3025\n\n" +
                "Hence the difference between the sum of the squares of the first ten natural numbers and the square of " +
                "the sum is 3025 − 385 = 2640.\n\n" +
                "Find the difference between the sum of the squares of the first one hundred natural numbers and the " +
                "square of the sum."
            )
    }

    override fun validate(result: Number): Boolean {
        return result == 25164150L
    }
}

class Problem6a(
    keyParam: Long = 100L
) : Problem6(keyParam) {

    override fun explain(): String {
        return "Solve using simple formulas for sum of squares and sum of naturals"
    }

    override fun run(keyParam: Long): Long {
        val sumOfNaturals = sumOfNaturalNumbers(keyParam)
        return (sumOfNaturals * sumOfNaturals) - sumOfSquares(keyParam)
    }

    private fun sumOfSquares(n: Long): Long {
        return (n * (n + 1) * (2 * n + 1)) / 6
    }

    private fun sumOfNaturalNumbers(n: Long): Long {
        return (n * (n + 1)) / 2
    }
}

class Problem6b(
    keyParam: Long = 100L
) : Problem6(keyParam) {

    override fun explain(): String {
        return "Solve using combined formula"
    }

    /*override fun run(keyParam: Long): Long {

        val n = keyParam
        val n2 = n * n
        val n3 = n2 * n
        val n4 = n3 * n

        return (3 * n4 + 2 * n3 - 3 * n2 - 2 * n) / 12
    }*/

    override fun run(keyParam: Long): Long {
        val n = keyParam
        return (n * (3 * n + 2) * (n - 1) * (n + 1)) / 12
    }
}