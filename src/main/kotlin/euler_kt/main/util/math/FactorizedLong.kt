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
import java.math.BigInteger

class LongFactor(val factor: Long, val exponent: Int = 1) {
    override fun toString(): String {
        if(exponent == 1) {
             return factor.toString()
        } else {
            return "$factor^$exponent"
        }
    }
}

class FactorizedLong: Iterable<LongFactor> {

    val factors: List<LongFactor>
    val numFactors: Int by lazy {computeNumFactors()}
    val numPrimeFactors get() = factors.size
    val value by lazy {computeValue()}

    constructor(vararg primeFactors: LongFactor) {
        this.factors = primeFactors.toList()
    }

    constructor(primeFactors: List<LongFactor>) {
        this.factors = primeFactors
    }

    constructor(vararg primeFactors: Long) {
        this.factors = primeFactors.map(::LongFactor).toList()
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

    operator fun compareTo(x: FactorizedLong): Int {
        return value.compareTo(x.value)
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
}