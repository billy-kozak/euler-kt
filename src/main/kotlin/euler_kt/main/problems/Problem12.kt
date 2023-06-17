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
import euler_kt.main.util.math.IntegerMath
import euler_kt.main.util.math.LongFactor
import euler_kt.main.util.primes.InfiniteEratosthenesList
import kotlin.math.sqrt

fun InfiniteEratosthenesList.nthPrimorial(n: Int): FactorizedLong {
    val factors = Array(n) {LongFactor(this[it])}
    return FactorizedLong(*factors)
}

private fun factorMaxima(primorialLow: FactorizedLong, primorialHigh: FactorizedLong): FactorizedLong {

    val primeHigh = primorialHigh.factors.last().factor

    var maxFactors = primorialLow.numFactors
    var maxima = primorialHigh

    fun FactorizedLong.tooHigh(): Boolean {
        var powersProduct = 1L
        for(f in factors) {
            if(f.exponent > 1) {
                powersProduct *= IntegerMath.pow(f.factor, f.exponent - 1)
            }
        }
        return powersProduct >= primeHigh
    }

    val search = DeepRecursiveFunction<Pair<Int, FactorizedLong>, Unit> {
        val idx = it.first
        val candidate = it.second

        if(candidate.tooHigh()) {
            return@DeepRecursiveFunction
        }

        if(candidate.numFactors > maxFactors) {
            maxima = candidate
            maxFactors = candidate.numFactors
        } else if(candidate.numFactors == maxFactors) {
            if(candidate < maxima) {
                maxima = candidate
            }
        }

        callRecursive(Pair(idx, candidate * primorialLow.factors[idx].factor))

        if(idx + 1 < primorialLow.factors.size) {
            callRecursive(Pair(idx + 1, candidate * primorialLow.factors[idx + 1].factor))
        }
    }

    search(Pair(0, FactorizedLong(1L)))

    return maxima
}

/**
 * This makes a good deal of sense, but I don't know how to prove that this method works.
 *
 * Idea is:
 * 1. The number with max factors between any two consecutive primorials is always greater than the same number
 * between the previous two primorials.
 * 2. Each of these local maxima has prime factors only from the lower bound primorial.
 */
private fun lowerBoundForNFactorsByCustomSearch(n: Int, primes: InfiniteEratosthenesList): FactorizedLong {
    val nPrimesStart = IntegerMath.log2Ciel(n)

    var primorialHigh = primes.nthPrimorial(nPrimesStart)
    var primorialLow = primorialHigh / primorialHigh.last().factor

    while(true) {
        val maxima = factorMaxima(primorialLow, primorialHigh)
        if(maxima.numFactors < n) {
            return maxima
        }
        primorialHigh = primorialLow
        primorialLow = primorialHigh / primorialHigh.last().factor
    }
}

/**
 * According to https://oeis.org/A046522
 */
private fun lowerBoundForNFactors(n: Int): Long {
    return 2 * IntegerMath.sqrt(n.toLong())
}


fun initialNFromLowerBound(lowerBound: Long): Long {
    return ((-0.5) + sqrt(0.25 + 2.0 * lowerBound.toDouble())).toLong()
}

fun triangleNumber(n: Long): Long {
    return n * (n + 1) / 2
}

fun factorByTrialDiv(n: Long, prmies: List<Long>): FactorizedLong {
    var remaining = n
    val factors = mutableListOf<LongFactor>()
    var idx = 0

    while(remaining > 1) {
        val prime = prmies[idx]
        if(remaining % prime == 0L) {
            var exponent = 0
            while(remaining % prime == 0L) {
                exponent++
                remaining /= prime
            }
            factors.add(LongFactor(prime, exponent.toLong()))
        }
        idx++
    }
    return FactorizedLong(factors)
}

abstract class Problem12(override val defaultKeyParam: Int = 501): EulerProblem<Long, Int> {
    override fun description(): String {Long
        return (
            "The sequence of triangle numbers is generated by adding the natural numbers. " +
                "So the 7th triangle number would be:\n" +
                "1 + 2 + 3 + 4 + 5 + 6 + 7 = 28. The first ten terms would be:\n\n" +
                "    1, 3, 6, 10, 15, 21, 28, 36, 45, 55, ... \n" +
                "Let us list the factors of the first seven triangle numbers:\n\n" +
                "    1: Long, Long1\n" +
                "    3: 1, 3\n" +
                "    6: 1, 2, 3, 6\n" +
                "    10: 1, 2, 5, 10\n" +
                "    15: 1, 3, 5, 15\n" +
                "    21: 1, 3, 7, 21\n" +
                "    28: 1, 2, 4, 7, 14, 28\n\n" +
                "We can see that 28 is the first triangle number to have over five divisors.\n" +
                "What is the value of the first triangle number to have over five hundred divisors?"
            )
    }

    override fun validate(result: Number): Boolean {
        return result.toLong() == 76576500L
    }
}

class Problem12a(defaultKeyParam: Int = 501) : Problem12(defaultKeyParam) {

    override fun explain(): String {
        return """
            |Estimate lower bound for n factors using custom method, then test each triangle number greater than the
            |lower bound. Also exploit properties of closed form for triangle numbers to make factorization faster.
        """.trimMargin()
    }

    override fun run(keyParam: Int): Long {
        val primes = InfiniteEratosthenesList()
        var bound = lowerBoundForNFactorsByCustomSearch(keyParam, primes)

        var n = initialNFromLowerBound(bound.value)
        var nFactorized = factorByTrialDiv(n, primes)

        while(true) {
            val nNext = n + 1
            /* Note: possible small optimization here because nNext is coprime with n.
               Since number of factors of n is pretty small, however, it's probably not worth it. */
            val nNextFactorized =  factorByTrialDiv(nNext, primes)

            val exp2 = if((n % 2L) == 0L) {
                nFactorized.factors[0].exponent
            } else {
                nNextFactorized.factors[0].exponent
            }
            val triangleFactors = ((nFactorized.numFactors * nNextFactorized.numFactors) / (exp2 + 1)) * exp2

            if(triangleFactors >= keyParam) {
                return triangleNumber(n)
            }

            n = nNext
            nFactorized = nNextFactorized
        }
    }
}

class Problem12b(defaultKeyParam: Int = 501) : Problem12(defaultKeyParam) {

    override fun explain(): String {
        return """
            |Use efficient factorization based on trial division of known primes, but otherwise, " +
            |naive implementation.
        """.trimMargin()
    }

    override fun run(keyParam: Int): Long {
        val primes = InfiniteEratosthenesList()

        var t = 1L
        var n = 2L
        while(factorByTrialDiv(t, primes).numFactors < keyParam) {
            t += n
            n += 1
        }
        return t
    }
}

class Problem12c(defaultKeyParam: Int = 501) : Problem12(defaultKeyParam) {

    override fun explain(): String {
        return """
            |Estimate lower bound for n factors according to d(n) <= 2 * sqrt(n), then test each triangle number greater
            |than the lower bound. Also exploit properties of closed form for triangle numbers to make factorization 
            |faster.
        """.trimMargin()
    }

    override fun run(keyParam: Int): Long {
        val primes = InfiniteEratosthenesList()
        var bound = lowerBoundForNFactors(keyParam)

        var n = initialNFromLowerBound(bound)
        var nFactorized = factorByTrialDiv(n, primes)

        while(true) {
            val nNext = n + 1
            /* Note: possible small optimization here because nNext is coprime with n.
               Since number of factors of n is pretty small, however, it's probably not worth it. */
            val nNextFactorized =  factorByTrialDiv(nNext, primes)

            val exp2 = if((n % 2L) == 0L) {
                nFactorized.factors[0].exponent
            } else {
                nNextFactorized.factors[0].exponent
            }
            val triangleFactors = ((nFactorized.numFactors * nNextFactorized.numFactors) / (exp2 + 1)) * exp2

            if(triangleFactors >= keyParam) {
                return triangleNumber(n)
            }

            n = nNext
            nFactorized = nNextFactorized
        }
    }
}