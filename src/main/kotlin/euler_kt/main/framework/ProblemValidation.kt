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

class ProblemValidation (
    override val problem: Int,
    override val time: Double,
    val answer: Number,
    val valid: Boolean
) : ProblemResult {
    override fun description(): String {
        return toString()
    }

    override fun toString(): String {
        return (
            "Problem $problem " +
            if(!valid) "failed with wrong answer: '${answer}'." else "succeeded with correct answer " +
            "$answer, with a run time of ${"%.3f".format(time)}ms."
        )
    }
}