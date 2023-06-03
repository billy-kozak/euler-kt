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

private const val MAGIC_STRING: String =
    "73167176531330624919225119674426574742355349194934" +
    "96983520312774506326239578318016984801869478851843" +
    "85861560789112949495459501737958331952853208805511" +
    "12540698747158523863050715693290963295227443043557" +
    "66896648950445244523161731856403098711121722383113" +
    "62229893423380308135336276614282806444486645238749" +
    "30358907296290491560440772390713810515859307960866" +
    "70172427121883998797908792274921901699720888093776" +
    "65727333001053367881220235421809751254540594752243" +
    "52584907711670556013604839586446706324415722155397" +
    "53697817977846174064955149290862569321978468622482" +
    "83972241375657056057490261407972968652414535100474" +
    "82166370484403199890008895243450658541227588666881" +
    "16427171479924442928230863465674813919123162824586" +
    "17866458359124566529476545682848912883142607690042" +
    "24219022671055626321111109370544217506941658960408" +
    "07198403850962455444362981230987879927244284909188" +
    "84580156166097919133875499200524063689912560717606" +
    "05886116467109405077541002256983155200055935729725" +
    "71636269561882670428252483600823257530420752963450"


private fun charDigitToLong(char: Char): Long {
    return when(char) {
        '0' -> 0L
        '1' -> 1L
        '2' -> 2L
        '3' -> 3L
        '4' -> 4L
        '5' -> 5L
        '6' -> 6L
        '7' -> 7L
        '8' -> 8L
        '9' -> 9L
        else -> throw IllegalArgumentException("char must be a digit")
    }
}

abstract class Problem8 (
    override val defaultKeyParam: Int = 13,
    protected val digits: List<Long> = MAGIC_STRING.map(::charDigitToLong)
): EulerProblem<Long, Int> {

    override fun description(): String {
        var formattedMagicString = ""
        for (i in MAGIC_STRING.indices step 50) {
            formattedMagicString += MAGIC_STRING.slice(i..i + 49) + "\n"
        }

        return (
            "The four adjacent digits in the -digit number that have the greatest product are 9 * 9 * 8 * 9 = 5832.\n" +
                "\n" + formattedMagicString + "\n" +
                "Find the thirteen adjacent digits in the 1000-digit number that have the greatest product. What is the " +
                "value of this product?"
            )
    }

    override fun validate(result: Number): Boolean {
        return result == 23514624000L
    }
}
class Problem8a (
    defaultKeyParam: Int = 13,
    digits: List<Long> = MAGIC_STRING.map(::charDigitToLong)
): Problem8(defaultKeyParam, digits) {

    override fun explain(): String {
        return (
            "Compute each product of N adjacent digits with early exit and skip forward if a digit " +
            "in the window is '0'."
        )
    }

    override fun run(keyParam: Int): Long {
        if(keyParam > digits.size) {
            return digits.reduce { a, b -> a * b }
        }

        var i = 0
        var maxProduct = 0L

        while(i < digits.size - keyParam) {
            var product = 1L
            var j = i

            while(j < i + keyParam) {
                product *= digits[j]
                if(product == 0L) {
                    i = j
                    break
                }
                j += 1
            }
            maxProduct = maxProduct.coerceAtLeast(product)
            i += 1
        }

        return maxProduct
    }
}

class Problem8b (
    defaultKeyParam: Int = 13,
    digits: List<Long> = MAGIC_STRING.map(::charDigitToLong)
): Problem8(defaultKeyParam, digits) {

    override fun explain(): String {
        return (
            "Naive solution. Compute every product of N adjacent digits."
        )
    }

    override fun run(keyParam: Int): Long {
        var maxProduct = 0L
        for(i in 0..digits.size - keyParam) {
            var product = 1L

            /* Note: this is much faster than using a stream reduce */
            /* Note: a while loop is the same speed */
            for(j in i until i + keyParam) {
                product *= digits[j].toInt()
            }
            maxProduct = maxProduct.coerceAtLeast(product)
        }

        return maxProduct
    }
}

class Problem8c (
    defaultKeyParam: Int = 13,
    digits: List<Long> = MAGIC_STRING.map(::charDigitToLong)
): Problem8(defaultKeyParam, digits) {

    override fun explain(): String {
        /* Note: this IS significantly faster than zero skip without the divide by first digit rotation */
        return (
            "Skip strings which have a zero, and also minimize multiplications by, where possible, dividing out the " +
            "first digit of the previous string and multiplying only the last digit of the current string."
        )
    }

    override fun run(keyParam: Int): Long {
        if(keyParam > digits.size) {
            return digits.reduce { a, b -> a * b }
        }

        var i = 0
        var maxProduct = 0L
        var lastProduct = 0L

        while(i < digits.size - keyParam) {
            var product = 1L
            var j = i

            if(lastProduct == 0L) {
                while (j < i + keyParam) {
                    product *= digits[j]
                    if (product == 0L) {
                        i = j
                        break
                    }
                    j += 1
                }
            } else {
                product = lastProduct / digits[i - 1] * digits[i + keyParam - 1]
            }

            maxProduct = maxProduct.coerceAtLeast(product)
            lastProduct = product
            i += 1
        }

        return maxProduct
    }
}