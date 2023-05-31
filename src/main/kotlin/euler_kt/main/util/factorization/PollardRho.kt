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

package euler_kt.main.util.factorization

import euler_kt.main.util.coroutines.RecursiveSequence
import euler_kt.main.util.math.euclidGCD
import kotlin.math.abs

fun pollardRhoPolly0(x: Long): Long {
    return x * x + 1
}

fun pollardRhoPolly1(x: Long): Long {
    return x * x - 1
}

fun pollardRho(n: Long, x0: Long = 2, g: (Long) -> Long = ::pollardRhoPolly0): Long {
    var x = x0
    var y = x0
    var d = 1L

    while(d == 1L) {
        x = g(x) % n
        y = g(g(y)) % n
        d = euclidGCD(abs(x - y), n)
    }
    return d
}

fun pollardRhoFactorize(
    n: Long, x0: Long = 2, g: (Long) -> Long = ::pollardRhoPolly0
): Sequence<Long> = RecursiveSequence(n) { t ->

    val q = pollardRho(t, x0, g)
    var d = t / q


    when(q) {
        1L -> {
            emit(t)
        }
        t -> {
            emit(q)
        }
        else -> {
            callRecursive(q)
            callRecursive(d)
        }
    }
}

fun pollardRhoFactorize(
    factors: Iterable<Long>, x0: Long = 2, g: (Long) -> Long = ::pollardRhoPolly0
): Sequence<Long> = sequence {
    for(factor in factors) {
        yieldAll(pollardRhoFactorize(factor, x0 = x0, g = g))
    }
}

fun pollardRhoFactorize(
    factors: Sequence<Long>, x0: Long = 2, g: (Long) -> Long = ::pollardRhoPolly0
): Sequence<Long> = sequence {
    for(factor in factors) {
        yieldAll(pollardRhoFactorize(factor, x0 = x0, g = g))
    }
}

