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

import euler_kt.main.util.functions.oddLongToNaturalInt
import euler_kt.main.util.primes.Precompute.Companion.startPrimeListFromPrecompute
import euler_kt.main.util.structures.BijectionBooleanArray
import euler_kt.main.util.structures.FunctionBackedGrowOnlyList

private val WHEEL_INCREMENTS: List<Int> = listOf(
    4, 2, 4, 2, 4, 6, 2, 6
)

fun eratosthenes(n: Long): List<Long> {

    val primes = mutableListOf<Long>(2)
    val sieve = BijectionBooleanArray(n + 1, ::oddLongToNaturalInt)

    if(n < 2) {
        return listOf()
    }

    var i = 3L
    while(i <= n) {
        if(!sieve[i]) {
            primes.add(i)
            var j = i * i
            while(j <= n) {
                sieve[j] = true
                j += i * 2
            }
        }
        i += 2
    }
    return primes
}

fun eratosthenesWithWheelFactorization(n: Long): List<Long> {

    val primes = mutableListOf<Long>()

    // save space, by building a mark list which is only valid for the "spokes" of the wheel factorization algorithm
    val sieve = BijectionBooleanArray(
        (((n / 30) + 1) * 30) + 1, ::wheelFactorizationBijectionFunction
    )

    for(i in arrayOf(2, 3, 5)) {
        if(i <= n) {
            primes.add(i.toLong())
        }
    }

    var i = 7L
    var incIdx = 0

    while(i <= n) {
        if(!sieve[i]) {
            primes.add(i)
            var j = i * i
            while(j <= n) {
                sieve[j] = true
                j += i * 2
            }
        }
        i += WHEEL_INCREMENTS[incIdx]
        incIdx = (incIdx + 1) % WHEEL_INCREMENTS.size
    }

    return primes
}

fun eratosthenesWithPrecompute(n: Long, primes: List<Long>): List<Long> {
    return  eratosthenesWithPrecompute(n, FunctionBackedGrowOnlyList(primes.size){primes[it]})
}
fun eratosthenesWithPrecompute(n: Long): List<Long> {
    return  eratosthenesWithPrecompute(n, startPrimeListFromPrecompute())
}

private fun eratosthenesWithPrecompute(n: Long, primes: FunctionBackedGrowOnlyList<Long>): List<Long> {
    val maxPrime = primes.last()

    if(n <= maxPrime) {
        return primes
    }

    val sieve = BijectionBooleanArray(
        (((n / 30) + 1) * 30) + 1
    ) {
        when(val idx = wheelFactorizationBijectionFunction(it)) {
            0 -> 0
            else -> idx - wheelFactorizationBijectionFunction(maxPrime) + 1
        }
    }

    for(i in 1 until primes.size) {
        val p = primes[i]
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
            primes.add(i)
            var j = i * i
            while(j <= n) {
                sieve[j] = true
                j += i * 2
            }
        }
        i += WHEEL_INCREMENTS[incIdx]
        incIdx = (incIdx + 1) % WHEEL_INCREMENTS.size
    }

    return primes
}

fun eratosthenesSequence(n: Long) = sequence {
    // save space, by building a mark list which is only valid for the "spokes" of the wheel factorization algorithm
    val sieve = BijectionBooleanArray(
        (((n / 30) + 1) * 30) + 1, ::wheelFactorizationBijectionFunction
    )

    for(i in arrayOf(2L, 3L, 5L)) {
        if(i <= n) {
            yield(i)
        }
    }

    var i = 7L
    var incIdx = 0

    while(i <= n) {
        if(!sieve[i]) {
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