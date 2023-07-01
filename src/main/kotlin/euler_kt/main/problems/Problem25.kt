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
import euler_kt.main.util.math.inverseFibonacciEstimate
import java.math.BigInteger
import kotlin.math.roundToInt

abstract class Problem25(override val defaultKeyParam: Int = 1000) : EulerProblem<Int, Int> {
    override fun description(): String {
        return """
            Problem 25:
            The Fibonacci sequence is defined by the recurrence relation:
            
            Fn = Fn−1 + Fn−2, where F1 = 1 and F2 = 1.
            Hence the first 12 terms will be:
            
            F1 = 1
            F2 = 1
            F3 = 2
            F4 = 3
            F5 = 5
            F6 = 8
            F7 = 13
            F8 = 21
            F9 = 34
            F10 = 55
            F11 = 89
            F12 = 144
            The 12th term, F12, is the first term to contain three digits.
            
            What is the index of the first term in the Fibonacci sequence to contain 1000 digits?
        """.trimIndent()
    }

    override fun validate(result: Number): Boolean {
        return result == 4782
    }
}

class Problem25a(defaultKeyParam: Int = 1000) : Problem25(defaultKeyParam) {

    override fun explain(): String {
        return """
            |Compute using closed form properties of fibonacci sequence.
        """.trimMargin()
    }

    /**
     * Solve problem 25
     *
     * There is significant floating point error in this solution which will eventually yield wrong results for high
     * enough N. As this _is_ actually quite accurate for exact integer powers, however, it's still working very well
     * even for such a large input as 10^999.
     *
     * If we could use 80 bit floats in JVM it could be even more accurate. I suppose we could write a JNI
     * module if it's something we ever needed.
     */
    override fun run(keyParam: Int): Int {
        return inverseFibonacciEstimate(10, keyParam - 1).roundToInt()
    }
}

class Problem25b(defaultKeyParam: Int = 1000) : Problem25(defaultKeyParam) {

    override fun explain(): String {
        return "Naive solution using big ints to simply compute the fibonacci sequence."
    }

    override fun run(keyParam: Int): Int {
        var fn = BigInteger.ONE
        var fnMinus1 = BigInteger.ONE

        var n = 2

        while(fn.toString().length < keyParam) {
            val temp = fn
            n++
            fn += fnMinus1
            fnMinus1 = temp
        }

        return n
    }
}