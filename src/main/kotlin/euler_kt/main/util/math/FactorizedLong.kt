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

class LongFactor(val factor: Long, val exponent: Long = 1L) {
    override fun toString(): String {
        if(exponent == 1L) {
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
            return if(factors[0].exponent == 1L) {
                FactorizedLong(factors.subList(1, factors.size))
            } else {
                copyAWithReduction(0)
            }
        } else if(factors.last().factor == other){
            return if(factors.last().exponent == 1L) {
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
}