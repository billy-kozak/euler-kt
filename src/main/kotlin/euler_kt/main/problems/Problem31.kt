/*
 * This file is part of euler-kt
 * Copyright (C) 2024 Bill Kozak
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

abstract class Problem31(override val defaultKeyParam: Int = 200) : EulerProblem<Int, Int> {

    val coins = intArrayOf(200, 100, 50, 20, 10, 5, 2, 1)

    override fun description(): String {
        return """
            In the United Kingdom the currency is made up of pound (£) and pence (p). There are eight coins in general 
            circulation:

            1p, 2p, 5p, 10p, 20p, 50p, £1 (100p), and £2 (200p).
            It is possible to make £2 in the following way:

            1×£1 + 1×50p + 2×20p + 1×5p + 1×2p + 3×1p
            How many different ways can £2 be made using any number of coins?
        """.trimIndent()
    }

    override fun validate(result: Number): Boolean {
        return result == 73682
    }
}

class Problem31b: Problem31() {
    override fun explain(): String {
        return """
            Simple recursive solution
            
            The only optimization here is to realize that, if at any point, the recursive algorithm reaches a coin
            value of '1' then there is only one way to make change, so we can immediately return 1 without any need to 
            recurse further.
        """.trimIndent()
    }

    override fun run(keyParam: Int): Int {
        return calculate(0, keyParam)
    }

    private fun calculate(coinIdx: Int, remaining: Int): Int {
        var count = 0
        for(i in coinIdx until coins.size) {
            if(coins[i] == 1) {
                count += 1
                break
            }
            val r = remaining - coins[i]
            if(r == 0) {
                count += 1
            } else if(r > 0) {
                count += calculate(i, r)
            }
        }
        return count
    }
}

class Problem31a: Problem31() {
    override fun explain(): String {
        return """
            Dynamic programming solution
        """.trimIndent()
    }

    override fun run(keyParam: Int): Int {

        val amount = keyParam

        val dp = IntArray(amount + 1)
        dp[0] = 1

        for(coin in coins) {
            for(i in coin..amount) {
                dp[i] += dp[i - coin]
            }
        }

        return dp[amount]
    }
}