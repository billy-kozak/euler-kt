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

package euler_kt.main.util.math

import euler_kt.main.util.coroutines.RecursiveSequence
import euler_kt.main.util.primes.Precompute
import euler_kt.main.util.primes.eratosthenesWithWheelFactorization
import java.math.BigInteger

fun generateAllFactorizedLongsUpTo(top: Int): Sequence<FactorizedLong> {
    return generateAllFactorizedLongsUpTo(top.toLong())
}

fun generateAllFactorizedLongsUpTo(top: Int, primes: List<Long>): Sequence<FactorizedLong> {
    return generateAllFactorizedLongsUpTo(top.toLong(), primes)
}

fun generateAllFactorizedLongsUpTo(top: Long): Sequence<FactorizedLong> {
    val primes = if(top <= Precompute.largestPrime1024()) {
        Precompute.sequence().map{it.toLong()}.takeWhile { it < top }.toList()
    } else {
        eratosthenesWithWheelFactorization(top)
    }
    return generateAllFactorizedLongsUpTo(top, primes)
}
fun generateAllFactorizedLongsUpTo(top: Long, primes: List<Long>): Sequence<FactorizedLong> {
    class Param(val prev: FactorizedLong, val startIndex: Int)

    return RecursiveSequence(Param(FactorizedLong.ONE, 0)) { args ->
        val prev = args.prev
        val startIndex = args.startIndex

        for (i in startIndex until primes.size) {
            val prime = primes[i]
            val newFactor = prev.multiplyByPrime(prime)

            if (newFactor.value >= top) {
                return@RecursiveSequence
            }
            emit(newFactor)
            callRecursive(Param(newFactor, i))
        }
    }
}

class LongFactor(val factor: Long, val exponent: Int = 1) {
    override fun toString(): String {
        if(exponent == 1) {
             return factor.toString()
        } else {
            return "$factor^$exponent"
        }
    }
    override fun equals(other: Any?): Boolean {
        return when(other) {
            is LongFactor -> factor == other.factor && exponent == other.exponent
            else -> false
        }
    }
    override fun hashCode(): Int {
        val fHash = factor.hashCode()
        return fHash xor exponent.rotateLeft(fHash.countLeadingZeroBits())
    }
}

class FactorizedLong private constructor(
    val factors: List<LongFactor>, private val sorted: Boolean = false
): Iterable<LongFactor>, Comparable<FactorizedLong> {

    val sortedFactors: List<LongFactor> by lazy {computeSortedFactors()}
    val numFactors: Int by lazy {computeNumFactors()}
    val numPrimeFactors get() = factors.size
    val value by lazy {computeValue()}

    private var sortComputed = sorted

    constructor(vararg primeFactors: LongFactor) : this(primeFactors.toList())

    constructor(primeFactors: List<LongFactor>): this(primeFactors, false)

    constructor(vararg primeFactors: Long): this(primeFactors.map(::LongFactor).toList())

    companion object {
        val ONE = FactorizedLong(listOf(), true)
        fun fromSortedFactors(factors: List<LongFactor>): FactorizedLong {
            return FactorizedLong(factors, true)
        }
    }

    fun sumAllFactors(): Long {
        var s = 1L
        for(factor in factors) {
            s *= (IntegerMath.pow(factor.factor, factor.exponent + 1) - 1) / (factor.factor - 1)
        }
        return s
    }

    fun sumAllProperFactors(): Long {
        var s = 1L
        for(factor in factors) {
            s *= (IntegerMath.pow(factor.factor, factor.exponent + 1) - 1) / (factor.factor - 1)
        }
        return s - value
    }

    fun allFactors(): Sequence<Long> {
        return RecursiveSequence(Pair(1L, factors.size - 1)) { args ->
            var n = args.first
            val idx = args.second

            if(idx > 0) {
                for (exp in 0..factors[idx].exponent) {
                    callRecursive(Pair(n, idx - 1))
                    n *= factors[idx].factor
                }
            } else {
                for (exp in 0..factors[idx].exponent) {
                    emit(n)
                    n *= factors[idx].factor
                }
            }
        }
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return when(other) {
            is FactorizedLong -> value == other.value
            is Long -> value == other
            else -> false
        }
    }

    override fun iterator(): Iterator<LongFactor> {
        return factors.iterator()
    }

    override fun toString(): String {
        return "FactorizedLong(value=$value, factors=${factors})"
    }

    fun last(): LongFactor {
        return factors.last()
    }

    operator fun get(index: Int): LongFactor {
        return factors[index]
    }

    operator fun compareTo(x: Int): Int {
        return value.compareTo(x.toLong())
    }

    override operator fun compareTo(other: FactorizedLong): Int {
        return value.compareTo(other.value)
    }

    fun multiplyByPrime(prime: Long, exponent: Int = 1): FactorizedLong {
        if(sortComputed) {
            val itr = sortedFactors.iterator()
            val newFactors = mutableListOf<LongFactor>()

            while(itr.hasNext()) {
                val next = itr.next()
                if (next.factor == prime) {
                    newFactors.add(LongFactor(prime, next.exponent + exponent))
                    newFactors.addAll(itr.asSequence())
                    return FactorizedLong(newFactors, true)
                } else if(next.factor > prime) {
                    newFactors.add(LongFactor(prime, exponent))
                    newFactors.add(next)
                    newFactors.addAll(itr.asSequence())
                    return FactorizedLong(newFactors, true)
                } else {
                    newFactors.add(next)
                }
            }

            newFactors.add(LongFactor(prime, exponent))
            return FactorizedLong(newFactors, true)
        } else {
            val newFactors = factors.toMutableList()
            for(i in 0 until newFactors.size) {
                val factor = newFactors[i]
                if(factor.factor == prime) {
                    newFactors[i] = LongFactor(prime, factor.exponent + exponent)
                    return FactorizedLong(newFactors)
                }
            }
            newFactors.add(LongFactor(prime, exponent))
            return FactorizedLong(newFactors)
        }
    }

    operator fun times(other: Long): FactorizedLong {
        val primeFactors = mutableListOf<LongFactor>()

        val itr = factors.iterator()

        val foundFactor = run lambda@{
            while(itr.hasNext())  {
                val factor = itr.next()
                if(other == factor.factor) {
                    primeFactors.add(LongFactor(factor.factor, factor.exponent + 1))
                    return@lambda true
                } else {
                    primeFactors.add(factor)
                }
            }
            return@lambda false
        }

        if(!foundFactor) {
            primeFactors.add(LongFactor(other))
            return FactorizedLong(primeFactors)
        }
        while(itr.hasNext()) {
            primeFactors.add(itr.next())
        }

        return FactorizedLong(primeFactors)
    }

    operator fun times(other: FactorizedLong): FactorizedLong {
        val newFactors = mutableListOf<LongFactor>()
        var i = 0
        var j = 0

        while(i < factors.size && j < other.factors.size) {
            val f1 = factors[i]
            val f2 = other.factors[j]
            if(f1.factor == f2.factor) {
                newFactors.add(LongFactor(f1.factor, f1.exponent + f2.exponent))
                i += 1
                j += 1
            } else if(f1.factor > f2.factor) {
                newFactors.add(f2)
                j += 1
            } else {
                newFactors.add(f1)
                i += 1
            }
        }

        if(i != factors.size) {
            while(i < factors.size) {
                newFactors.add(factors[i])
                i += 1
            }
        } else if(j != other.factors.size) {
            while(j < other.factors.size) {
                newFactors.add(other.factors[j])
                j += 1
            }
        }

        return FactorizedLong(newFactors)
    }

    operator fun div(other: Long): FactorizedLong {

        fun copyAWithReduction(idx: Int): FactorizedLong {
            return FactorizedLong(List(factors.size) {
                if(it == idx) {
                    LongFactor(factors[0].factor, factors[0].exponent - 1)
                } else {
                    factors[it]
                }
            })
        }

        if(factors[0].factor == other) {
            return if(factors[0].exponent == 1) {
                FactorizedLong(factors.subList(1, factors.size))
            } else {
                copyAWithReduction(0)
            }
        } else if(factors.last().factor == other){
            return if(factors.last().exponent == 1) {
                FactorizedLong(factors.subList(0, factors.size - 1))
            } else {
                copyAWithReduction(factors.size - 1)
            }
        }

        val primeFactors = mutableListOf<LongFactor>()

        for(factor in factors) {
            if(factor.factor == other) {
                if(factor.exponent > 1L) {
                    primeFactors.add(LongFactor(factor.factor, factor.exponent - 1))
                }
            } else {
                primeFactors.add(factor)
            }
        }
        return FactorizedLong(primeFactors)
    }

    fun toBigInt(): BigInteger {
        var ret = BigInteger.valueOf(1L)
        for(factor in factors) {
            val bigFactor = BigInteger.valueOf(factor.factor).pow(factor.exponent.toInt())
            ret *= bigFactor
        }

        return ret
    }

    fun isCoprimeTo(other: FactorizedLong): Boolean {
        var i = 0
        var k = 0

        if(sortedFactors.isEmpty() != other.factors.isEmpty()) {
            return true
        }

        while(true) {
            val f1 = sortedFactors[i]
            val f2 = other.sortedFactors[k]
            if(f1.factor == f2.factor) {
                return false
            } else if(f1.factor < f2.factor) {
                i += 1
                if(i == factors.size) {
                    return true
                }
            } else {
                k += 1
                if(k == other.factors.size) {
                    return true
                }
            }
        }
    }

    private fun computeSortedFactors(): List<LongFactor> {
        if(sorted) {
            return factors
        } else {
            sortComputed = true
            return factors.sortedBy { it.factor }
        }
    }

    private fun computeValue(): Long {
        return factors.fold(1L) { acc, factor -> acc * IntegerMath.pow(factor.factor, factor.exponent) }
    }

    private fun computeNumFactors(): Int {
        var n = 1

        for(factor in factors) {
            n *= factor.exponent.toInt() + 1
        }

        return n
    }
}