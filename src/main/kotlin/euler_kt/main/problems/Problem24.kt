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
import euler_kt.main.util.math.lexiPermJoin
import euler_kt.main.util.math.permuteOf

class Problem24(override val defaultKeyParam: Int = 1000000) : EulerProblem<Long, Int> {
    override fun description(): String {
        return """
            Problem 24:
            A permutation is an ordered arrangement of objects. For example, 3124 is one possible permutation of the
            digits 1, 2, 3 and 4. If all of the permutations are listed numerically or alphabetically, we call it
            lexicographic order. The lexicographic permutations of 0, 1 and 2 are:
            
            012   021   102   120   201   210
            
            What is the millionth lexicographic permutation of the digits 0, 1, 2, 3, 4, 5, 6, 7, 8 and
            9?
        """.trimIndent()
    }

    override fun validate(result: Number): Boolean {
        return result == 2783915460L
    }

    override fun run(keyParam: Int): Long {
        return permuteOf("0123456789".toList()).lexiPermJoin(keyParam.toLong() - 1).toLong()
    }
}