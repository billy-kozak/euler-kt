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
import euler_kt.main.util.coroutines.RecursiveSequence
import euler_kt.main.util.math.IntegerMath
import kotlin.math.log10

class Problem30(override val defaultKeyParam: Int = 5) : EulerProblem<Int, Int> {
    override fun description(): String {
        return """
            Surprisingly there are only three numbers that can be written as the sum of fourth powers of their digits:
            
            1634 = 1^4 + 6^4 + 3^4 + 4^4
            8208 = 8^4 + 2^4 + 0^4 + 8^4
            9474 = 9^4 + 4^4 + 7^4 + 4^4
            
            As 1 = 1^4 is not a sum it is not included.
            
            The sum of these numbers is 1634 + 8208 + 9474 = 19316.
            
            Find the sum of all the numbers that can be written as the sum of fifth powers of their digits.
        """.trimIndent()
    }

    override fun validate(result: Number): Boolean {
        return when (defaultKeyParam) {
            4 -> result == 19316
            5 -> result == 443839
            else -> false
        }
    }

    override fun run(keyParam: Int): Int {

        val numDigits = calculateDigits(keyParam)
        val digiValues = precomputePowers(keyParam)

        return generateCandidates(numDigits)
            .map { digits ->
                Pair(digits, digits.map { digiValues[it.toInt()] }.sum())
            }
            .filter { candidate ->
                val digits = candidate.first

                val checkArray = ByteArray(10)

                for(digit in digits) {
                    val digiInt = digit.toInt()

                    if(digit.toInt() == 0) {
                        continue
                    }
                    checkArray[digiInt] = (checkArray[digiInt] + 1).toByte()
                }

                var value = candidate.second
                while(value > 0) {
                    val digit = value % 10
                    value /= 10

                    if(digit == 0) {
                        continue
                    }
                    if(checkArray[digit].toInt() == 0) {
                        return@filter false
                    }

                    checkArray[digit] = (checkArray[digit] - 1).toByte()
                }

                return@filter checkArray.all{it.toInt() == 0}
            }
            .map {
                it.second
            }
            .filter { it > 9} // Any number less than 10 is "not a sum" according to problem statement
            .sum()
    }
    private fun precomputePowers(power: Int): IntArray {
        return IntArray(10) { IntegerMath.pow(it, power) }
    }

    private fun generateCandidates(numDigits: Int): Sequence<ByteArray> {
        class Param(val depth: Int, val digits: ByteArray?)

        return RecursiveSequence(Param(0, null)) {
            val depth = it.depth

            if(depth == 0) {
                for(i in 9 downTo 1) {
                    val arr = ByteArray(numDigits)
                    arr[0] = i.toByte()
                    callRecursive(Param(1, arr))
                }
            } else if(depth < (numDigits - 1)) {
                val digits = it.digits!!
                val maxDig = digits[depth - 1]

                for(i in maxDig downTo 0) {
                    digits[depth] = i.toByte()
                    callRecursive(Param(depth + 1, digits))
                }
            } else {
                val digits = it.digits!!
                val maxDig = digits[depth - 1]

                for(i in maxDig downTo 0) {
                    digits[depth] = i.toByte()
                    emit(digits)
                }
            }
        }
    }

    private fun calculateDigits(keyParam: Int): Int {

        var n = 1
        var t = keyParam * log10(9.0)

        while((n - log10(n.toDouble())) <= t) {
            n++
        }

        return n
    }
}