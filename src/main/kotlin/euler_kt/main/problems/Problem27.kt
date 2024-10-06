/*
 * This file is part of euler-kt
 * Copyright (C) 2024 Bill Kozak
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
import euler_kt.main.util.primes.Precompute
import euler_kt.main.util.primes.eratosthenesWithPrecompute
import euler_kt.main.util.primes.eratosthenesWithWheelFactorization

class PrimeLookupSet {

    private var primeList: List<Long>
    private var top: Long
    private val primeSet: MutableSet<Long>

    val size get() = primeList.size

    constructor(n: Long) {
        primeList = eratosthenesWithWheelFactorization(n)
        primeSet = primeList.toMutableSet()
        top = n
    }

    fun extend(n: Long) {
        val newPrimes = eratosthenesWithPrecompute(n, primeList)
        top = n
        for(i in primeList.size until newPrimes.size) {
            primeSet.add(newPrimes[i])
        }
        primeList = newPrimes
    }

    fun isPrime(n: Long): Boolean {
        while(n > top) {
            extend(top * 2)
        }
        return primeSet.contains(n)
    }

    fun get(index: Int): Long {
        return primeList[index]
    }
}

abstract class Problem27(override val defaultKeyParam: Int = 1000) : EulerProblem<Int, Int> {
    override fun description(): String {
        return """
            Problem 27: Quadratic primes
            Euler discovered the remarkable quadratic formula:
            
            n^2 + n + 41
            
            It turns out that the formula will produce 40 primes for the consecutive integer values 0 ≤ n ≤ 39. 
            However, when n = 40, 40^2 + 40 + 41 = 40(40 + 1) + 41 is divisible by 41, and certainly when n = 41, 
            41^2 + 41 + 41 is clearly divisible by 41.
            
            The incredible formula n^2 − 79n + 1601 was discovered, which produces 80 primes for the consecutive values 
            0 ≤ n ≤ 79. The product of the coefficients, −79 and 1601, is −126479.
            
            Considering quadratics of the form:
            
            n^2 + an + b, where |a| < 1000 and |b| ≤ 1000
            
            where |n| is the modulus/absolute value of n
            e.g. |11| = 11 and |−4| = 4
            
            Find the product of the coefficients, a and b, for the quadratic expression that produces the maximum number 
            of primes for consecutive values of n, starting with n = 0.
        """.trimIndent()
    }

    override fun validate(result: Number): Boolean {
        return if (defaultKeyParam == 2000){
            result == -126479
        } else {
            result == -59231
        }
    }
}

class Problem27b: Problem27() {

    override fun explain(): String {
        return """
            |Naive solution
        """.trimMargin()
    }

    override fun run(keyParam: Int): Int {
        /* We've set prime ceiling way too high here. The max value we ever test is less than 2000.
        * But I have no idea how to show that a lower value is more reasonable */
        val primeSet = PrimeLookupSet(2L * keyParam * keyParam)
        var maxN = 0L
        var maxA = 0
        var maxB = 0

        for(a in -keyParam + 1 until keyParam) {
            for(b in -keyParam until keyParam + 1) {
                var n = 0L
                while(primeSet.isPrime(n * n + a * n + b)) {
                    n++
                }
                if(n > maxN) {
                    maxN = n
                    maxA = a
                    maxB = b
                }
            }
        }

        return maxA * maxB
    }
}

class Problem27a: Problem27() {

    override fun explain(): String {
        return """
            |Exploit that b must be positive prime due to requirement that n=0 produces a prime
        """.trimMargin()
    }

    override fun run(keyParam: Int): Int {
        /* Setting the prime ceiling to keyParam is too low, and will cause us to later grow the prime set. But
        * even doing that results in a much lower run time than setting the ceiling to what we did in the naive
        * solution */
        /* Two times key param results in a very fast solution, but I have no real justification for picking this */
        val primeSet = PrimeLookupSet(2 * keyParam.toLong())
        var maxN = 0L
        var maxA = 0
        var maxB = 0

        for(a in -keyParam + 1 until keyParam) {
            var bIndex = 0
            do {
                val b = primeSet.get(bIndex)
                var n = 0L
                while(primeSet.isPrime(n * n + a * n + b)) {
                    n++
                }
                if(n > maxN) {
                    maxN = n
                    maxA = a
                    maxB = b.toInt()
                }
                bIndex++
            } while(b < keyParam && bIndex < primeSet.size)
        }

        return maxA * maxB
    }
}