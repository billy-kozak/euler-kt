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
import euler_kt.main.util.factorization.factorizeAllUpTo
import euler_kt.main.util.streams.toTreeSet
import euler_kt.main.util.structures.toTreeSet
import kotlin.streams.toList

private const val SMALLEST_ABUNDANT_NUMBER = 12
private const val SMALLEST_ABUNDANT_SUM = 24

abstract class Problem23(override val defaultKeyParam: Int = 28123) : EulerProblem<Long, Int> {
    override fun description(): String {
        return """
            Problem 23:
            A perfect number is a number for which the sum of its proper divisors is exactly equal to the number. For
            example, the sum of the proper divisors of 28 would be 1 + 2 + 4 + 7 + 14 = 28, which means that 28 is a
            perfect number.
            
            A number n is called deficient if the sum of its proper divisors is less than n and it is called abundant if
            this sum exceeds n.
            
            As 12 is the smallest abundant number, 1 + 2 + 3 + 4 + 6 = 16, the smallest number that can be written as
            the sum of two abundant numbers is 24. By mathematical analysis, it can be shown that all integers greater
            than 28123 can be written as the sum of two abundant numbers. However, this upper limit cannot be reduced
            any further by analysis even though it is known that the greatest number that cannot be expressed as the
            sum of two abundant numbers is less than this limit.
            
            Find the sum of all the positive integers which cannot be written as the sum of two abundant numbers.
        """.trimIndent()
    }

    override fun validate(result: Number): Boolean {
        return result == 4179871L
    }
}

class Problem23a(defaultKeyParam: Int = 28123) : Problem23(defaultKeyParam) {

    override fun explain(): String {
        return """
            Factorize all n up to keyParam, and filter to abundant numbers, then mark off abundant sums from an array.
            Add up the un-marked numbers at the end.
        """.trimIndent()
    }

    /**
     * Somewhat surprisingly, faster than any solution using efficient data structure lookups to test each number to
     * see if it can be summed from two abundant numbers.
     *
     * Without the optimization where we first compute the highest possible abundant sum and then subtract as we find
     * abundant sums (as opposed to marking all abundant sums from a boolean array and then looping over the array
     * again) this is close to the tree/hash set lookup solution, but still, this solution is a bit faster.
     *
     * It makes some amount of sense, as this is simpler and is doing only operations that CPUs are best at (looping
     * over arrays and doing simple arithmetic) whereas the fancier solutions are doing a lot of branching and pointer
     * chasing.
     *
     * I suspect that with higher N the tree/hash lookup methods would eventually win out (but higher N for this
     * problem doesn't even make sense as 28123 is the known largest possible abundant sum).
     */
    override fun run(keyParam: Int): Long {
        val factorized = factorizeAllUpTo(
            keyParam + 1 - SMALLEST_ABUNDANT_NUMBER
        ).subList(SMALLEST_ABUNDANT_NUMBER - 1, keyParam - SMALLEST_ABUNDANT_NUMBER + 2)

        val abundantNumbers = factorized
            .stream()
            .filter {it.sumAllProperFactors() > it.value}
            .map{it.value}
            .toList()

        val abundantArr = BooleanArray(keyParam + 1)
        var result = triangleNumber(keyParam.toLong())

        for(i in abundantNumbers.indices) {
            for(j in i until abundantNumbers.size) {
                val x = abundantNumbers[i].toInt()
                val y = abundantNumbers[j].toInt()
                if(x + y > keyParam) {
                    break
                }
                if(!abundantArr[x + y]) {
                    result -= x + y
                    abundantArr[x + y] = true
                }

            }
        }

        return result
    }
}

class Problem23b(defaultKeyParam: Int = 28123) : Problem23(defaultKeyParam) {

    override fun explain(): String {
        return """
            Factorize all n up to keyParam, and filter to abundant numbers, then use a tree set and hash set together
            to test for abundant sums.
        """.trimIndent()
    }

    override fun run(keyParam: Int): Long {
        val factorized = factorizeAllUpTo(
            keyParam + 1 - SMALLEST_ABUNDANT_NUMBER
        ).subList(SMALLEST_ABUNDANT_NUMBER - 1, keyParam - SMALLEST_ABUNDANT_NUMBER + 2)

        val abundantList = factorized
            .filter {it.sumAllProperFactors() > it.value}
            .map{it.value}

        val abundantTree = abundantList
            .toTreeSet()

        val abundantSet = abundantList
            .toHashSet()

        fun isAbundantSum(n: Long): Boolean {
            val candidates = abundantTree.headSet(n)

            for(candidate in candidates) {
                if(abundantSet.contains(n - candidate)) {
                    return true
                } else if(n - candidate < SMALLEST_ABUNDANT_NUMBER) {
                    return false
                }
            }
            return false
        }

        var result = triangleNumber(SMALLEST_ABUNDANT_SUM - 1L)
        for(i in SMALLEST_ABUNDANT_SUM + 1..keyParam) {
            if(!isAbundantSum(i.toLong())) {
                result += i
            }
        }

        return result
    }
}

class Problem23c(defaultKeyParam: Int = 28123) : Problem23(defaultKeyParam) {

    override fun explain(): String {
        return """
            Factorize all n up to keyParam, and filter to abundant numbers, then use a tree set to test for abundant 
            sums.
        """.trimIndent()
    }

    override fun run(keyParam: Int): Long {
        val factorized = factorizeAllUpTo(
            keyParam + 1 - SMALLEST_ABUNDANT_NUMBER
        ).subList(SMALLEST_ABUNDANT_NUMBER - 1, keyParam - SMALLEST_ABUNDANT_NUMBER + 2)

        val abundantNumbers = factorized
            .stream()
            .filter { it.sumAllProperFactors() > it.value }
            .map { it.value }
            .toTreeSet()

        fun isAbundantSum(n: Long): Boolean {
            val candidates = abundantNumbers.headSet(n)

            for (candidate in candidates) {
                if (candidates.contains(n - candidate)) {
                    return true
                } else if (n - candidate < SMALLEST_ABUNDANT_NUMBER) {
                    return false
                }
            }
            return false
        }

        var result = triangleNumber(SMALLEST_ABUNDANT_SUM - 1L)
        for (i in SMALLEST_ABUNDANT_SUM + 1..keyParam) {
            if (!isAbundantSum(i.toLong())) {
                result += i
            }
        }

        return result
    }
}