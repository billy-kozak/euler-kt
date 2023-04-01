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

package euler_kt.main.framework

import java.util.concurrent.TimeUnit

class ThroughputResult(
    override val problem: Int,
    override val score: Double,
    val answer: Number,
    val valid: Boolean,
    val runTime: Long,
    val timeUnit: TimeUnit
) : ProblemResult  {
    override fun description(): String {
        return toString()
    }

    override fun toString(): String {
        if(valid) {
            return (
                "Problem $problem succeeded with correct answer $answer, " +
                "with a throughput of ${"%.3f".format(score)} in ${timeUnit.toSeconds(runTime)} seconds."
            )
        } else {
            return (
                "Problem $problem failed with wrong answer: '${answer}', " +
                "with a throughput of ${"%.3f".format(score)} in ${timeUnit.toSeconds(runTime)} seconds."
            )
        }
    }
}