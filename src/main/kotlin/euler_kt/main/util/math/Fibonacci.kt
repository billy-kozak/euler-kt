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

import kotlin.math.pow
import kotlin.math.roundToLong

const val GOLDEN_RATIO: Double = 1.618033988749895

private const val PHI = GOLDEN_RATIO
private const val PSI = 1 - PHI
private const val SQRT_FIVE = 2.23606797749979

private const val LOG10_PHI = 0.20898764024997873
private const val LOG10_SQRT5 = 0.3494850021680094

private const val LOG2_PHI = 0.6942419136306179
private const val LOG2_SQRT5 = 1.160964047443681

fun inverseFibonacciEstimate(n: Long): Double {
    return logBase(PHI, n * SQRT_FIVE)
}

fun inverseFibonacciEstimate(base: Long, power: Int): Double {
    return when(base) {
        10L -> (LOG10_SQRT5 + power) / LOG10_PHI
        2L -> (LOG2_SQRT5 + power) / LOG2_PHI
        else -> (logBase(base, SQRT_FIVE) + power) / logBase(base, PHI)
    }
}

fun inverseFibonacciFloorEstimate(n: Long): Long {
    return logBase(PHI, SQRT_FIVE * (n + 0.5)).toLong()
}

fun fibonacciEstimateByRounding(n: Int): Long {
    return fibonacciEstimateByRounding(n.toLong())
}


fun fibonacciEstimateByRounding(n: Long): Long {
    return (PHI.pow(n.toDouble()) / SQRT_FIVE).roundToLong()
}

fun fibonacciEstimateTraditional(n: Long): Long {
    return ((PHI.pow(n.toDouble()) - (PSI).pow(n.toDouble())) / SQRT_FIVE).roundToLong()
}