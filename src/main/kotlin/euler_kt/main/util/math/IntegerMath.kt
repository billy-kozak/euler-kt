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

class IntegerMath private constructor() {

    companion object {
        private val MAX_POWERS_OF_TEN_FOR_LEAD_ZEROES = byteArrayOf(
            19, 18, 18, 18, 18, 17, 17, 17, 16, 16, 16, 15, 15, 15, 15, 14, 14, 14, 13, 13, 13, 12, 12, 12,
            12, 11, 11, 11, 10, 10, 10, 9, 9, 9, 9, 8, 8, 8, 7, 7, 7, 6, 6, 6, 6, 5, 5, 5, 4, 4, 4, 3, 3, 3,
            3, 2, 2, 2, 1, 1, 1, 0, 0, 0
        )

        fun pow(b: Int,  e: Int): Int {

            if(e < 0) {
                throw IllegalArgumentException("Exponent must be non-negative")
            }
            if(e == 0) {
                return 1
            } else if(e == 1) {
                return b
            }

            var r = b
            var p = e

            while(p >= 2) {
                r *= r
                p -= 2
            }

            if(p == 1) {
                r *= b
            }
            return r
        }
        fun pow(b: Long,  e: Long): Long {

            if(e < 0) {
                throw IllegalArgumentException("Exponent must be non-negative")
            }
            if(e == 0L) {
                return 1
            }

            var r = b
            var p = e

            while(p >= 2) {
                r *= r
                p -= 2
            }

            if(p == 1L) {
                r *= b
            }
            return r
        }

        fun log10Floor(n: Int): Int {
            return log10Floor(n.toLong())
        }

        /* Implementation found in guava source code */
        fun log10Floor(n: Long): Int {
            val maxPow10: Int = MAX_POWERS_OF_TEN_FOR_LEAD_ZEROES[n.countLeadingZeroBits()].toInt()

            return if (n >= pow(10, maxPow10)) {
                maxPow10
            } else {
                maxPow10 - 1
            }
        }

        fun digit(n: Int, i: Int): Int {
            val low = pow(10, i)
            val high = low * 10

            return (n % high) / low
        }
    }
}