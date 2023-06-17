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
import euler_kt.main.util.cache.Memoize
import euler_kt.main.util.math.SigmaSumLong
import euler_kt.main.util.math.nChooseK

abstract class Problem15(override val defaultKeyParam: Int = 20) : EulerProblem<Long, Int> {
    override fun description(): String {
            return """
                Starting in the top left corner of a 2×2 grid, and only being able to move to the right and down, 
                there are exactly 6 routes to the bottom right corner.
                
                How many such routes are there through a 20×20 grid?
        """.trimIndent()
    }

    override fun validate(result: Number): Boolean {
        return result == 137846528820L
    }
}

class Problem15a(defaultKeyParam: Int = 20) : Problem15(defaultKeyParam) {
    override fun explain(): String {
        return "Use formula (2 * n choose n) where n is width/height of square lattice."
    }

    /** This is counting center values of pascal's triangle.
     * ... thanks co-pilot for spoiling that */
    override fun run(keyParam: Int): Long {
        val n = (2 * keyParam)
        val k = keyParam

        return nChooseK(n, k)
    }
}

class Problem15b(defaultKeyParam: Int = 20) : Problem15(defaultKeyParam) {
    override fun explain(): String {
        return "Count paths by nested sum. Correct, but unbearably slow without memoization."
    }

    /** This is counting center values of pascal's triangle.
     * ... thanks co-pilot for spoiling that */
    override fun run(keyParam: Int): Long {
        /* for n * 2 latices, the number of paths is the (n+1)th triangle number */
        val f: (n: Long) -> Long = {n -> ((n + 1L) * (n + 2L) / 2L)}
        var sigma = SigmaSumLong(f)

        if(keyParam == 1) {
            /* For (n * 1) latices, the number of paths is n + 1 */
            return keyParam + 1L
        } else if(keyParam == 2) {
            return f(keyParam.toLong())
        }

        for(i in 4..keyParam) {
            val sigmaLast = sigma
            sigma = SigmaSumLong(Memoize{ n -> sigmaLast(0, n)})
        }
        return sigma(0, keyParam)
    }
}

