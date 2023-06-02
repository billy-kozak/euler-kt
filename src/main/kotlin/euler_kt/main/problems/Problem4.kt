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
import euler_kt.main.util.math.IntegerMath

class Problem4(
    override val defaultKeyParam: Int = 3
) : EulerProblem<Int, Int> {


    override fun description(): String {
        return (
            "A palindromic number reads the same both ways. The largest palindrome made from the product of two " +
            "2-digit numbers is 9009 = 91 × 99.\n\n" +
            "Find the largest palindrome made from the product of two 3-digit numbers."
        )
    }

    override fun validate(result: Number): Boolean {
        return result == 906609
    }

    override fun run(keyParam: Int): Int {

        val minFactor = IntegerMath.pow(10, keyParam - 1)
        val maxFactor = minFactor * 10 - 1

        var maxPalindrome = 0

        for (i in maxFactor downTo minFactor) {
            if(i * i < maxPalindrome) {
                break
            }
            for (j in i downTo minFactor) {
                val product = i * j
                if(product < maxPalindrome) {
                    break
                }
                if(isPalindrome(product)) {
                    maxPalindrome = maxPalindrome.coerceAtLeast(product)
                    break
                }
            }
        }
        return maxPalindrome
    }

    private fun isPalindrome(n: Int): Boolean {
        val nDigits = IntegerMath.log10Floor(n) + 1

        for (i in 0 until nDigits / 2) {
            if (IntegerMath.digit(n, i) != IntegerMath.digit(n, nDigits - i - 1)) {
                return false
            }
        }

        return true
    }
}