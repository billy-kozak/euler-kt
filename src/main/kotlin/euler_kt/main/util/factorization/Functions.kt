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

import euler_kt.main.util.math.FactorizedLong
import euler_kt.main.util.primes.Precompute
import euler_kt.main.util.primes.eratosthenesWithWheelFactorization

@Suppress("unchecked_cast")
fun factorizeAllUpTo(n: Int): List<FactorizedLong> {
    val primes = if(n < Precompute.largestPrime1024()) {
        Precompute.startPrimeListFromPrecompute()
    } else {
        eratosthenesWithWheelFactorization(n.toLong())
    }
    val arr = arrayOfNulls<FactorizedLong>(n + 1)

    arr[0] = FactorizedLong(0)
    arr[1] = FactorizedLong(1)

    for(i in primes.indices) {
        val p = primes[i]
        if(p > n) {
            break
        }
        arr[p.toInt()] = FactorizedLong(p)
    }

    fun recursiveFactorize(i: Int): FactorizedLong {
        val r = arr[i]
        if(r != null) {
            return r
        }
        for(p in primes) {
            if(i % p == 0L) {
                val f = FactorizedLong(p) * recursiveFactorize(i / p.toInt())
                arr[i] = f
                return f
            }
        }
        throw AssertionError("Factorization of $i not available")
    }

    for(i in n downTo 2) {
        recursiveFactorize(i)
    }

    return arr.asList() as List<FactorizedLong>
}