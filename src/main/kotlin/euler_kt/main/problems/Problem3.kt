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
import euler_kt.main.util.primes.eratosthenes
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import kotlin.math.nextUp
import kotlin.math.sqrt

@State(Scope.Thread)
open class Problem3(override val defaultKeyParam: Long = 600851475143L) : EulerProblem<Long, Long> {

    override fun description(): String {
        return (
            "The prime factors of 13195 are 5, 7, 13 and 29.\n\n" +
            "What is the largest prime factor of the number 600851475143?"
        )
    }

    override fun validate(result: Number): Boolean {
        return result == 6857L
    }

    @Benchmark
    fun jmhBenchmark() {
        run(defaultKeyParam)
    }

    override fun run(keyParam: Long): Long {
        val primes = eratosthenes(sqrt(keyParam.toDouble().nextUp()).nextUp().toLong())

        for (i in primes.size - 1 downTo 0) {
            val p = primes[i]
            if (keyParam % p == 0L) {
                return p
            }
        }

        return keyParam
    }

}