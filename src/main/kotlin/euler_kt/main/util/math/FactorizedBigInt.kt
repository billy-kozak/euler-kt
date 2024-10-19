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

package euler_kt.main.util.math
import euler_kt.main.util.structures.LazyList
import java.math.BigInteger
import kotlin.math.log2

class FactorizedBigInt private constructor(
    val factors: List<LongFactor>, private val sorted: Boolean = false
): Iterable<LongFactor>, Comparable<FactorizedBigInt> {

    val value by lazy {computeValue()}
    val sortedFactors: List<LongFactor> by lazy {computeSortedFactors()}
    val log2Val by lazy {computeLog2Value()}

    val numFactors: Int by lazy {computeNumFactors()}

    val size get() = factors.size

    constructor(vararg primeFactors: LongFactor) : this(primeFactors.toList())
    constructor(primeFactors: Iterable<LongFactor>) : this(LazyList(primeFactors.iterator()))
    constructor(primeFactors: Sequence<LongFactor>) : this(LazyList(primeFactors.iterator()))
    constructor(primeFactors: Iterator<LongFactor>) : this(LazyList(primeFactors))

    fun pow(exponent: Int): FactorizedBigInt {
        return fromSortedFactors(sortedFactors.map { LongFactor(it.factor, it.exponent * exponent) })
    }

    fun multiplyByPrime(prime: Long, exponent: Int = 1): FactorizedBigInt {
        val newFactors = factors.toMutableList()
        for(i in 0 until newFactors.size) {
            val factor = newFactors[i]
            if(factor.factor == prime) {
                newFactors[i] = LongFactor(prime, factor.exponent + exponent)
                return FactorizedBigInt(newFactors)
            }
        }

        newFactors.add(LongFactor(prime, exponent))
        return FactorizedBigInt(newFactors)
    }

    fun longValue(): Long {
        return factors.fold(1L) {
                acc, factor -> acc * IntegerMath.pow(factor.factor, factor.exponent)
        }
    }

    fun isPrime(): Boolean {
        return factors.size == 1 && factors[0].exponent == 1
    }

    override fun iterator(): Iterator<LongFactor> {
        return factors.iterator()
    }

    override fun compareTo(other: FactorizedBigInt): Int {
        return log2Val.compareTo(other.log2Val)
    }

    override fun equals(other: Any?): Boolean {
        if(other is FactorizedBigInt) {
            return sortedFactors == other.sortedFactors
        } else {
            return false
        }
    }

    override fun hashCode(): Int {
        return sortedFactors.hashCode()
    }

    override fun toString(): String {
        return "FactorizedBigInt(${factors.joinToString(" * ") { it.toString() }})=${value}"
    }

    private fun computeLog2Value(): Double {
        return factors.asSequence().map{log2(it.factor.toDouble()) * it.exponent}.sum()
    }

    private fun computeSortedFactors(): List<LongFactor> {
        if(sorted) {
            return factors
        } else {
            return factors.sortedBy { it.factor }
        }
    }

    private fun computeNumFactors(): Int {
        var n = 1

        for(factor in factors) {
            n *= factor.exponent.toInt() + 1
        }

        return n
    }

    private fun computeValue(): BigInteger {
        return factors.fold(BigInteger.ONE) {
                acc, factor -> acc * BigInteger.valueOf(factor.factor).pow(factor.exponent)
        }
    }

    companion object {
        fun fromSortedFactors(factors: List<LongFactor>): FactorizedBigInt {
            return FactorizedBigInt(factors, true)
        }
    }
}