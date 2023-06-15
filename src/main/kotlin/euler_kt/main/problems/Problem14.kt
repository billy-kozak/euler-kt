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



abstract class Problem14(override val defaultKeyParam: Long = 1000000L): EulerProblem<Long, Long> {

    protected val problemNumbers = parseMagicString()

    override fun description(): String {
        return """
            The following iterative sequence is defined for the set of positive integers:
            
            n → n/2 (n is even)
            n → 3n + 1 (n is odd)
            
            Using the rule above and starting with 13, we generate the following sequence:
            
            13 → 40 → 20 → 10 → 5 → 16 → 8 → 4 → 2 → 1
            It can be seen that this sequence (starting at 13 and finishing at 1) contains 10 terms. 
            Although it has not been proved yet (Collatz Problem), it is thought that all starting numbers finish at 1.
            
            Which starting number, under one million, produces the longest chain?
            
            NOTE: Once the chain starts the terms are allowed to go above one million.
        """.trimIndent()
    }

    override fun validate(result: Number): Boolean {
        return result == 837799L
    }
}

class Problem14a(defaultKeyParam: Long = 1000000L) : Problem14(defaultKeyParam) {

    override fun explain(): String {
        return "Using memoization and fast divide powers of two"
    }

    override fun run(keyParam: Long): Long {
        /* Note: hash map turns out to be much slower */
        val cache = LongArray(keyParam.toInt() / 2)

        fun LongArray.getCachedLen(n: Long): Long {
            val idx = n / 2L
            if(idx >= this.size) {
                return 0L
            } else {
                return this[idx.toInt()]
            }
        }

        fun LongArray.setCachedLen(n: Long, len: Long) {
            if(n % 2 == 0L) {
                return
            }
            val idx = n / 2L
            if(idx < this.size) {
                this[idx.toInt()] = len
            }
        }

        var maxLen = 0L
        var candidate = 0L

        for (i in 1 until keyParam) {
            var len = 0L
            var j = i

            while(j != 1L) {
                val twos = j.countTrailingZeroBits()
                len += twos
                j = j shr twos

                if(j == 1L) {
                    break
                }

                val c = cache.getCachedLen(j)
                if(c != 0L) {
                    len += c
                    break
                }
                j = 3 * j + 1
            }
            cache.setCachedLen(i, len)

            if(len > maxLen) {
                maxLen = len
                candidate = i
            }
        }
        return candidate
    }
}

class Problem14b(defaultKeyParam: Long = 1000000L) : Problem14(defaultKeyParam) {

    override fun explain(): String {
        return "Naive solution"
    }

    override fun run(keyParam: Long): Long {
        var max = 0L
        var maxStart = 0L
        for (i in 1 until keyParam) {
            val len = naiveCollatzLength(i)
            if (len > max) {
                max = len
                maxStart = i
            }
        }
        return maxStart
    }

    private fun naiveCollatzLength(n: Long): Long {
        var len = 1L
        var i = n
        while (i != 1L) {
            if (i % 2 == 0L) {
                i /= 2
            } else {
                i = 3 * i + 1
            }
            len++
        }
        return len
    }
}