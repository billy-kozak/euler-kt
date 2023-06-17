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

import java.math.BigInteger


class SigmaSum(private val f: (n: Int) -> Int): (Int, Int) -> Int {
    override operator fun invoke (start: Int, end: Int): Int {
        var sum = 0
        for(i in start..end) {
            sum += f(i)
        }
        return sum
    }
}

class SigmaSumLong(private val f: (n: Long) -> Long): (Long, Long) -> Long {
    override operator fun invoke (start: Long, end: Long): Long {
        var sum = 0L
        for(i in start..end) {
            sum += f(i)
        }
        return sum
    }

    operator fun invoke (start: Int, end: Int): Long {
        var sum = 0L
        for(i in start..end) {
            sum += f(i.toLong())
        }
        return sum
    }
}

class SigmaSumBigInteger(private val f: (n: BigInteger) -> BigInteger) {
    operator fun invoke (start: BigInteger, end: BigInteger): BigInteger {
        var sum = BigInteger.ZERO
        var n = start
        while(n <= end) {
            sum += f(n)
            n++
        }
        return sum
    }

    operator fun invoke (start: Int, end: Int): BigInteger {
        var sum = BigInteger.ZERO
        for(i in start..end) {
            sum += f(i.toBigInteger())
        }
        return sum
    }

    operator fun invoke (start: Long, end: Long): BigInteger {
        var sum = BigInteger.ZERO
        for(i in start..end) {
            sum += f(i.toBigInteger())
        }
        return sum
    }
}