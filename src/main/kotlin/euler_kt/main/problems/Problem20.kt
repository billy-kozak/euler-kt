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
import euler_kt.main.util.math.FactorizedLong
import euler_kt.main.util.math.LongFactor
import euler_kt.main.util.primes.Precompute
import euler_kt.main.util.primes.eratosthenesWithWheelFactorization

class Problem20(override val defaultKeyParam: Int = 100) : EulerProblem<Int, Int> {
    override fun description(): String {
        return """
            Problem 20:
            n! means n × (n − 1) × ... × 3 × 2 × 1
            For example, 10! = 10 × 9 × ... × 3 × 2 × 1 = 3628800,
            and the sum of the digits in the number 10! is 3 + 6 + 2 + 8 + 8 + 0 + 0 = 27.
            
            Find the sum of the digits in the number 100!
        """.trimIndent()
    }

    override fun validate(result: Number): Boolean {
        return result == 648
    }

    override fun run(keyParam: Int): Int {
        val factorizedUpToN = factorizedNumbers(keyParam)

        var factorizedFactorial = factorizedUpToN.reduce{a, b -> a * b}

        /* we know that all prime factors up to n occur at least once. Therefore, 2 will be first factor and
        * 5 will be 3rd factor appearing in the factors list */
        /* We also know that exponent of 2 is greater than or equal to exponent of 5 for any n*/
        val tens = factorizedFactorial.factors[2].exponent

        val factors = ArrayList<LongFactor>(factorizedFactorial.factors.size)
        for(i in factorizedFactorial.factors.indices) {
            if(i == 0) {
                factors.add(LongFactor(2, factorizedFactorial.factors[i].exponent - tens))
            } else if(i != 2) {
                factors.add(factorizedFactorial.factors[i])
            }
        }

        /* I was hoping to find some other way of exploiting the prime factorization of n! to compute digisum but
        * have been unsuccessful so far. There is almost certainly a faster way to compute n! as a big int with
        * all tens divided out, but since I've done it this way already... I just don't feel that microoptimizing this
        * algorithm is interesting. */
        factorizedFactorial = FactorizedLong(factors)

        return factorizedFactorial.toBigInt().toString().map{it - '0'}.sum()
    }

    @Suppress("unchecked_cast")
    private fun factorizedNumbers(n: Int): List<FactorizedLong> {
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

        return arr.asList().subList(2, arr.size) as List<FactorizedLong>
    }
}