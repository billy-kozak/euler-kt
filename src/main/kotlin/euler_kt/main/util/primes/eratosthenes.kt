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
import java.lang.Long.max

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

fun eratosthenesWithPrecompute(n: Long): List<Long> {
    val primes = startPrimeListFromPrecompute()
    val maxPrime = primes.last()
    val sieve = BijectionBooleanArray(
        n + 1
    ) { oddLongToNaturalInt(it - maxPrime + 1)}

    if(n < 2) {
        return listOf()
    }

    for(i in 1 until primes.size) {
        val p = primes[i]
        var nextMultiple = ((maxPrime / p) + 1) * p
        if(nextMultiple % 2 == 0L) {
            nextMultiple += p
        }
        var j = max(nextMultiple, p * p)

        while(j <= n) {
            sieve[j] = true
            j += p * 2
        }
    }

    for(i in maxPrime + 2..n step 2) {
        if(!sieve[i]) {
            primes.add(i)
            var j = i * i
            while(j <= n) {
                sieve[j] = true
                j += i * 2
            }
        }
    }

    return primes
}