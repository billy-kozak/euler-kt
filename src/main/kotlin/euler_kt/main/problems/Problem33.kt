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
import euler_kt.main.util.math.FactorizedLong
import euler_kt.main.util.math.IntegerMath
import euler_kt.main.util.math.euclidGCD
import euler_kt.main.util.math.generateAllFactorizedLongsUpTo

open class SimpleFraction(val numerator: Int, val denominator: Int) {

    fun simplify(): SimpleFraction {
        val gcd = euclidGCD(numerator, denominator)
        return SimpleFraction(numerator / gcd, denominator / gcd)
    }

    operator fun times(other: SimpleFraction): SimpleFraction {
        return SimpleFraction(numerator * other.numerator, denominator * other.denominator)
    }

    override fun toString(): String {
        return "Fraction($numerator/$denominator)"
    }
}

class DigitizedFraction (
    numerator: Int,  denominator: Int, val numerDigits: List<Int>, val denomDigits: List<Int>
) : SimpleFraction(numerator, denominator) {
    constructor(numerator: Int, denominator: Int): this(
        numerator, denominator, getDigits(numerator), getDigits(denominator)
    )

    constructor(fraction: SimpleFraction) : this(fraction.numerator, fraction.denominator)

    fun toSimpleFraction(): SimpleFraction {
        return SimpleFraction(numerator, denominator)
    }

    override fun toString(): String {
        return "DigitizedFraction($numerator/$denominator)"
    }
}

class Candidate(val simplest: SimpleFraction, val bases: List<DigitizedFraction>, val candidate: DigitizedFraction)

fun getDigits(n: Int): List<Int> {
    var vn = n
    val digits = mutableListOf<Int>()
    while(vn > 0) {
        digits.add(vn % 10)
        vn /= 10
    }
    return digits
}

class Problem33(override val defaultKeyParam: Int = 2) : EulerProblem<Int, Int> {
    override fun description(): String {
        return """
            The fraction 49/98 is a curious fraction, as an inexperienced mathematician in attempting to simplify it may 
            incorrectly believe that 49/98 = 4/8, which is correct, is obtained by cancelling the 9s.
            
            We shall consider fractions like, 30/50 = 3/5, to be trivial examples.
            
            There are exactly four non-trivial examples of this type of fraction, less than one in value, and containing 
            two digits in the numerator and denominator.
            
            If the product of these four fractions is given in its lowest common terms, find the value of the
             denominator.
        """.trimIndent()
    }

    override fun validate(result: Number): Boolean {
        return result == 100
    }

    override fun run(keyParam: Int): Int {
        val bottom = IntegerMath.pow(10, keyParam -1)
        val top = IntegerMath.pow(10, keyParam)

        val baseNumbers = (sequenceOf(FactorizedLong.ONE) + generateAllFactorizedLongsUpTo(bottom)).toList()
        val baseFractions = generateBaseFractions(baseNumbers)

        return generateCandidates(baseFractions, bottom, top)
            .filter{isDigitCancelingFraction(it)}
            .map{it.candidate.toSimpleFraction()}
            .fold(SimpleFraction(1, 1)) {acc, d -> d * acc}
            .simplify().denominator
    }

    private fun isDigitCancelingFraction(candidate: Candidate): Boolean {
        var numerDigits = candidate.candidate.numerDigits.toMutableList()
        var denomDigits = candidate.candidate.denomDigits.toMutableList()

        if(!cancelDigits(numerDigits, denomDigits)) {
            return false
        }

        for(base in candidate.bases) {
            if(sameDigits(base.numerDigits, numerDigits) && sameDigits(base.denomDigits, denomDigits)) {
                if(candidate.candidate.numerator != (base.numerator * 10)) {
                    return true
                }
            }
        }

        return false
    }

    private fun sameDigits(a: List<Int>, b: List<Int>): Boolean {
        val remaining = a.toMutableList()
        for(d in b) {
            if(!remaining.remove(d)) {
                return false
            }
        }
        return remaining.isEmpty()
    }

    private fun cancelDigits(nDigits: MutableList<Int>, dDigits: MutableList<Int>): Boolean {
        for(n in nDigits) {
            for(d in dDigits) {
                if(n == d) {
                    nDigits.remove(n)
                    dDigits.remove(d)
                    return true
                }
            }
        }
        return false
    }

    private fun generateBaseFractions(baseNumbers: List<FactorizedLong>): Sequence<SimpleFraction> = sequence {
        for(i in baseNumbers.indices) {
            for(j in i + 1 until baseNumbers.size) {
                val a = baseNumbers[i]
                val b = baseNumbers[j]
                if(a.isCoprimeTo(b)) {
                    val aInt = a.value.toInt()
                    val bInt = b.value.toInt()
                    if(aInt < bInt) {
                        yield(SimpleFraction(aInt, bInt))
                    } else {
                        yield(SimpleFraction(bInt, aInt))
                    }
                }
            }
        }
    }

    private fun generateCandidates(
        baseFractions: Sequence<SimpleFraction>, bottom: Int, top: Int
    ): Sequence<Candidate> = sequence {
        for(simplest in baseFractions) {
            var bases = mutableListOf(DigitizedFraction(simplest))
            var m = simplest.numerator + 1

            var n = simplest.numerator * m
            var d = simplest.denominator * m

            while(n < bottom) {
                bases.add(DigitizedFraction(n, d))
                m += 1
                n = simplest.numerator * m
                d = simplest.denominator * m
            }

            while(d < top) {
                // When m is power of ten, it is "trivial" according to problem statement and doesn't count
                yield(Candidate(simplest, bases, DigitizedFraction(n, d)))
                m += 1
                n = simplest.numerator * m
                d = simplest.denominator * m
            }
        }
    }
}