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
import euler_kt.main.util.primes.*

abstract class Problem7(
    override val defaultKeyParam: Int = 10001
): EulerProblem<Long, Int> {
    override fun description(): String {
        return (
            "By listing the first six prime numbers: 2, 3, 5, 7, 11, and 13, we can see that the 6th prime is " +
            "13.\n\n" +
            "What is the 10 001st prime number?"
        )
    }

    override fun validate(result: Number): Boolean {
        return result == 104743L
    }
}

class Problem7a(
    defaultKeyParam: Int = 10001
): Problem7(defaultKeyParam) {

    override fun explain(): String {
        return "Solve using eratosthenes wheel factorization and prime number theorem to approximate nth prime"
    }

    override fun run(keyParam: Int): Long {
        val approximatePrimeVal = primeNumberTheoremApproximateNthPrime(keyParam) * 2
        val sequence = InfiniteEratosthenesSequence(approximatePrimeVal)
        return sequence.take(keyParam).last()
    }
}

class Problem7b(
    defaultKeyParam: Int = 10001
): Problem7(defaultKeyParam) {

    override fun explain(): String {
        return "Solve using eratosthenes starting at precompute and prime number theorem to approximate nth prime"
    }

    override fun run(keyParam: Int): Long {
        val approximatePrimeVal = primeNumberTheoremApproximateNthPrime(keyParam) * 2
        val sequence = InfiniteEratosthenesSequence(Precompute.startPrimeListFromPrecompute(), approximatePrimeVal)
        return sequence.take(keyParam).last()
    }
}

class Problem7c(
    defaultKeyParam: Int = 1000000000
): Problem7(defaultKeyParam) {

    override fun validate(result: Number): Boolean {
        return result == 22801763489L
    }

    override fun explain(): String {
        return "Solve for n = 1 billion"
    }

    override fun run(keyParam: Int): Long {
        val approximatePrimeVal = moreConservativeApproximateNthPrime(keyParam)
        val sequence = eratosthenesSequence(approximatePrimeVal)
        return sequence.take(keyParam).last()
    }
}