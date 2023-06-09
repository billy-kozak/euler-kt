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

package euler_kt.main.util.primes

import java.lang.IllegalArgumentException
import kotlin.math.ln
import kotlin.math.log2
import kotlin.math.roundToLong

fun primeNumberTheoremApproximateNthPrime(n: Int): Long {
    return (n * ln(n.toDouble())).roundToLong()
}

fun moreConservativeApproximateNthPrime(n: Int): Long {
    return (n * log2(n.toDouble())).roundToLong()
}

fun wheelFactorizationBijectionFunction(v: Long): Int {
    return longWheelFactorizationBijectionFunction(v).toInt()
}

fun longWheelFactorizationBijectionFunction(v: Long): Long {
    when(v) {
        2L -> return 0
        3L -> return 1
        5L -> return 3
    }

    val div = v / 30L
    val mod = v % 30L

    when(mod) {
        1L -> return (div * 8L + 3)
        7L -> return (div * 8L + 4)
        11L -> return (div * 8L + 5)
        13L -> return (div * 8L + 6)
        17L -> return (div * 8L + 7)
        19L -> return (div * 8L + 8)
        23L -> return (div * 8L + 9)
        29L -> return (div * 8L + 10)
    }

    // We can't avoid these lookups in the wheel factorization algorithm, but we also don't care about the
    // result, so we resolve to a valid index which we also don't care about. Since the algorithm never needs to
    // do a lookup for '2', we can use that as a placeholder.
    return 0
}

fun wheelIndex(v: Long): Int {

    if(v <= 5) {
        throw IllegalArgumentException("This function valid only for n > 5")
    }

    when(v % 30) {
        7L -> return 0
        11L -> return 1
        13L -> return 2
        17L -> return 3
        19L -> return 4
        23L -> return 5
        29L -> return 6
        1L -> return 7
    }

    throw IllegalArgumentException(
        "Invalid lookup for $v. This number cannot be reached in the wheel factorization algorithm."
    )
}