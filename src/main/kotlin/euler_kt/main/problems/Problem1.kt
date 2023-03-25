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
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State

@State(Scope.Thread)
open class Problem1(override val defaultKeyParam: Int = 1000) : EulerProblem<Int, Int> {

    override fun description(): String {
        return (
            "If we list all the natural numbers below 10 that are multiples of 3 or 5, we get 3, 5, 6 and 9. The sum " +
            "of these multiples is 23.\n\n" +
            "Find the sum of all the multiples of 3 or 5 below 1000."
        )
    }

    @Benchmark
    fun jmhBenchmark() {
        run(defaultKeyParam)
    }

    override fun run(keyParam: Int): Int {
        return sumOfMultiples(3, keyParam) + sumOfMultiples(5, keyParam) - sumOfMultiples(15, keyParam)
    }

    override fun validate(result: Number): Boolean {
        return result == 234168
    }

    private fun sumOfMultiples(m: Int, n: Int): Int {
        val div = (n / m).toLong()
        val top = (n - (n % m)).toLong()

        return ((m + top) * div / 2).toInt()
    }
}