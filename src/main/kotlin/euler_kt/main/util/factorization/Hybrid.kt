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

package euler_kt.main.util.factorization

import euler_kt.main.util.primes.Precompute
import euler_kt.main.util.primes.eratosthenesWithPrecompute

fun pollardRhoEratosthenesPrimeFactors(n: Long): Collection<Long> {

    var maxUnknown = 1L

    val pollard = pollardRhoFactorize(n).toSet()
    val confirmed = mutableSetOf<Long>()
    val unknown = mutableListOf<Long>()

    for(f in pollard) {
        if(f <= Precompute.largestPrime1024()) {
            if(Precompute.isPrime1024(f.toInt())) {
                confirmed.add(f)
            } else {
                confirmed.addAll(precomputedSieveTrial(f))
            }
        } else {
            if(f > maxUnknown) {
                maxUnknown = f
            }
            unknown.add(f)
        }
    }

    if(unknown.isEmpty()) {
        return confirmed
    }

    val extendedSieve = eratosthenesWithPrecompute(maxUnknown)

    for(f in unknown) {
        confirmed.addAll(sieveTrial(f, extendedSieve.iterator()))
    }

    return confirmed
}

private fun sieveTrial(n: Long, primes: Iterator<Long>): Sequence<Long> = sequence {
    var d = n

    for(p in primes) {
        if(d == p) {
            yield(p)
            break
        }
        if((d % p) == 0L) {
            yield(p)
            do {
                d = d / p
            } while(((d % p) == 0L))
        }
    }
}

private fun precomputedSieveTrial(n: Long): Sequence<Long> {
    return sieveTrial(n, Precompute.sequence().map {it.toLong()}.iterator())
}