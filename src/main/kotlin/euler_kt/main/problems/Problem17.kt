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

private const val DEFAULT_MAX = 1000

private val SUB_TWENTY: IntArray = intArrayOf(
    0, // N/A zero
    3, // one
    3, // two
    5, // three
    4, // four
    4, // five
    3, // six
    5, // seven
    5, // eight
    4, // nine
    3, // ten
    6, // eleven
    6, // twelve
    8, // thirteen
    8, // fourteen
    7, // fifteen
    7, // sixteen
    9, // seventeen
    8, // eighteen
    8, // nineteen
)

private val TENS: IntArray = intArrayOf(
    0, // N/A 10^0
    0, // N/A 10^1
    6, // twenty 10^2
    6, // thirty 10^3
    5, // forty 10^4
    5, // fifty 10^5
    5, // sixty 10^6
    7, // seventy 10^7
    6, // eighty 10^8
    6, // ninety 10^9
)
private val HUNDRED: Int = 7 // "hundred"
private val HUNDRED_AND: Int = 10 // "hundred and"
private val ONE_THOUSAND: Int = 11 // "one thousand"

abstract class Problem17(override val defaultKeyParam: Int = DEFAULT_MAX) : EulerProblem<Int, Int> {

    override fun description(): String {
        return "Problem 17: Number letter counts\n" +
            "If the numbers 1 to 5 are written out in words: one, two, three, four, five, then there are\n" +
            "3 + 3 + 5 + 4 + 4 = 19 letters used in total.\n" +
            "\n" +
            "If all the numbers from 1 to 1000 (one thousand) inclusive were written out in words, how many\n" +
            "letters would be used?\n" +
            "\n" +
            "NOTE: Do not count spaces or hyphens. For example, 342 (three hundred and forty-two) contains 23\n" +
            "letters and 115 (one hundred and fifteen) contains 20 letters. The use of \"and\" when writing out\n" +
            "numbers is in compliance with British usage."
    }

    override fun validate(result: Number): Boolean {
        return result == 21124
    }
}

class Problem17a(defaultKeyParam: Int = DEFAULT_MAX) : Problem17(defaultKeyParam) {

    override fun explain(): String {
        return "Exploit patterns to do calculate sum of words in large blocks (e.g. 1-99, 100-199, etc.) " +
            "using multiplications instead of additions (but still pretend like we don't know we're going to exactly " +
            "1000)"
    }

    override fun run(keyParam: Int): Int {
        var tensSum = SUB_TWENTY.slice(1..9).sum()
        var oneToTwenty = SUB_TWENTY.sum()
        var oneToHundred = oneToTwenty + 8 * tensSum + 10 * TENS.sum()

        if(keyParam > 1000) {
            throw NotImplementedError("Numbers over 1000 are not implemented")
        }
        var sum = 0
        // pretend like we don't know that we're always going to exactly 1000

        var hundreds = keyParam / 100

        if(hundreds >= 1) {
            sum += oneToHundred
            for (i in 1 until hundreds) {
                sum += (SUB_TWENTY[i] + HUNDRED) + (SUB_TWENTY[i] + HUNDRED_AND) * 99 + oneToHundred
            }
        }

        val finalHundredLength =  if (hundreds > 0) (SUB_TWENTY[hundreds] + HUNDRED_AND) else 0
        val remHundred = keyParam % 100

        if(remHundred in 1..19) {
            for(i in 1..remHundred) {
                sum += SUB_TWENTY[i] + finalHundredLength
            }
        } else if(remHundred > 0) {
            val tens = remHundred / 10
            val remTens = remHundred % 10
            sum += oneToTwenty + (finalHundredLength * 19)
            for(i in 2..tens) {
                sum += finalHundredLength * 10 + TENS[i] * 10 + tensSum
            }
            if(remTens > 0) {
                for(i in 1..remTens) {
                    sum += SUB_TWENTY[i] + finalHundredLength
                }
            }
        } else if(hundreds == 10) {
            sum += ONE_THOUSAND
        } else if(hundreds > 0) { // and remHundred == 0
            sum += SUB_TWENTY[hundreds] + HUNDRED
        }

        return sum
    }
}

class Problem17b(defaultKeyParam: Int = DEFAULT_MAX) : Problem17(defaultKeyParam) {

    override fun explain(): String {
        return "Simple solution, just to prove that all my constants are correct"
    }

    override fun run(keyParam: Int): Int {
        var sum = 0
        for(i in 1..keyParam) {
            val hundred = i / 100
            val rem100 = i % 100
            val tens = rem100 / 10
            val rem10 = rem100 % 10

            if(i == 1000) {
                sum += ONE_THOUSAND
                continue
            }
            if(hundred > 0) {
                if(rem100 > 0) {
                    sum += SUB_TWENTY[hundred] + HUNDRED_AND
                } else {
                    sum += SUB_TWENTY[hundred] + HUNDRED
                    continue
                }
            }

            if(tens >= 2) {
                sum += TENS[tens]
            } else {
                sum += SUB_TWENTY[rem100]
                continue
            }
            if(rem10 > 0) {
                sum += SUB_TWENTY[rem10]
            }
        }

        return sum
    }
}