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

package euler_kt.main.util.primes

import euler_kt.main.util.structures.BijectionBooleanArray
import euler_kt.main.util.structures.FunctionBackedGrowOnlyList
import euler_kt.main.util.structures.GrowOnlyArrayList
import euler_kt.main.util.structures.GrowOnlyList

private const val DEFAULT_MAX_PRIME_ZERO = 1000000L
private val WHEEL_INCREMENTS: List<Int> = listOf(
    4, 2, 4, 2, 4, 6, 2, 6
)

class EratosthenesSequence constructor(
    private val maxPrime0: Long,
    private val knownPrimes: GrowOnlyList<Long>
) : Sequence<Long> {

    public constructor(maxPrime0: Long =DEFAULT_MAX_PRIME_ZERO) : this(maxPrime0, GrowOnlyArrayList())

    public constructor(
        precompute: (Int) -> Long, precomputeSize: Int, maxPrime0: Long = DEFAULT_MAX_PRIME_ZERO
    ) : this(maxPrime0, FunctionBackedGrowOnlyList(precomputeSize, precompute))

    private val eratosthenes: suspend SequenceScope<Long>.(n: Long) -> Unit = { n ->
        // save space, by building a mark list which is only valid for the "spokes" of the wheel factorization algorithm
        val sieve = BijectionBooleanArray(
            (((n / 30) + 1) * 30) + 1, ::wheelFactorizationBijectionFunction
        )

        for(i in arrayOf(2, 3, 5)) {
            if(i <= n) {
                knownPrimes.add(i.toLong())
                yield(i.toLong())
            }
        }

        var i = 7L
        var incIdx = 0

        while(i <= n) {
            if(!sieve[i]) {
                knownPrimes.add(i)
                yield(i)
                var j = i * i
                while(j <= n) {
                    sieve[j] = true
                    j += i * 2
                }
            }
            i += WHEEL_INCREMENTS[incIdx]
            incIdx = (incIdx + 1) % WHEEL_INCREMENTS.size
        }
    }

    private val eratosthenesWithPrecompute: suspend SequenceScope<Long>.(n: Long) -> Unit = { n ->
        val maxPrime = knownPrimes.last()

        val sieve = BijectionBooleanArray(
            (((n / 30) + 1) * 30) + 1
        ) {
            when(val idx = wheelFactorizationBijectionFunction(it)) {
                0 -> 0
                else -> idx - wheelFactorizationBijectionFunction(maxPrime) + 1
            }
        }

        for(i in 1 until knownPrimes.size) {
            val p = knownPrimes[i]
            var nextMultiple = ((maxPrime / p) + 1) * p
            if(nextMultiple % 2 == 0L) {
                nextMultiple += p
            }
            var j = nextMultiple.coerceAtLeast(p * p)

            while(j <= n) {
                sieve[j] = true
                j += p * 2
            }
        }

        var incIdx = wheelIndex(maxPrime)
        var i = maxPrime + WHEEL_INCREMENTS[incIdx]

        incIdx = (incIdx + 1) % WHEEL_INCREMENTS.size

        while(i <= n) {
            if(!sieve[i]) {
                knownPrimes.add(i)
                yield(i)
                var j = i * i
                while(j <= n) {
                    sieve[j] = true
                    j += i * 2
                }
            }
            i += WHEEL_INCREMENTS[incIdx]
            incIdx = (incIdx + 1) % WHEEL_INCREMENTS.size
        }
    }

    private val sequence: Sequence<Long> = sequence {

        if(knownPrimes.isEmpty()) {
            eratosthenes(maxPrime0)
        } else {
            yieldAll(knownPrimes)
        }

        var n = knownPrimes.last() + maxPrime0

        while(true) {
            eratosthenesWithPrecompute(n)
            n += maxPrime0
        }
    }

    override fun iterator(): Iterator<Long> {
        return sequence.iterator()
    }
}