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
import java.math.BigInteger

class Problem16(override val defaultKeyParam: Int = 1000) : EulerProblem<Int, Int> {
    override fun description(): String {
        return "Problem 16: Power digit sum\n" +
                "2^15 = 32768 and the sum of its digits is 3 + 2 + 7 + 6 + 8 = 26.\n" +
                "\n" +
                "What is the sum of the digits of the number 2^1000?"
    }

    override fun validate(result: Number): Boolean {
        return result == 1366
    }

    override fun run(keyParam: Int): Int {
        val x = BigInteger.TWO.pow(keyParam)
        val s = x.toString()
        var sum = 0

        /* We can do this by dividing 10s instead, but, since this is already such a simple problem, I really don't
        feel like doing that optimization again.
         */
        for(c in s) {
            sum += c - '0'
        }
        return sum
    }
}