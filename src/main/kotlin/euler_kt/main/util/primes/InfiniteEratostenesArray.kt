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

package euler_kt.main.util.primes

import euler_kt.main.util.structures.FunctionBackedGrowOnlyList
import euler_kt.main.util.structures.GrowOnlyArrayList
import euler_kt.main.util.structures.GrowOnlyList

class InfiniteEratosthenesList private constructor(
    private val sequence: InfiniteEratosthenesSequence
): List<Long> {

    private val iterator: Iterator<Long> = sequence.iterator()
    override val size: Int get() = sequence.knownPrimes.size

    constructor(
        knownPrimes: GrowOnlyList<Long>,
        maxPrime0: Long = DEFAULT_MAX_PRIME_ZERO,
    ) : this(InfiniteEratosthenesSequence(knownPrimes, maxPrime0))

    constructor(maxPrime0: Long =DEFAULT_MAX_PRIME_ZERO)
        : this(InfiniteEratosthenesSequence(GrowOnlyArrayList(), maxPrime0))

    constructor(
        precompute: (Int) -> Long, precomputeSize: Int, maxPrime0: Long = DEFAULT_MAX_PRIME_ZERO
    ) : this(InfiniteEratosthenesSequence(FunctionBackedGrowOnlyList(precomputeSize, precompute), maxPrime0))

    override operator fun get(index: Int): Long {
        while(index >= sequence.knownPrimes.size) {
            iterator.next()
        }
        return sequence.knownPrimes[index]
    }

    override fun containsAll(elements: Collection<Long>): Boolean {
        return sequence.knownPrimes.containsAll(elements)
    }

    override fun contains(element: Long): Boolean {
        return sequence.knownPrimes.contains(element)
    }

    override fun isEmpty(): Boolean {
        return size != 0
    }

    override fun iterator(): Iterator<Long> {
        return sequence.knownPrimes.iterator()
    }

    override fun listIterator(): ListIterator<Long> {
        return sequence.knownPrimes.listIterator()
    }

    override fun listIterator(index: Int): ListIterator<Long> {
        return sequence.knownPrimes.listIterator(index)
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<Long> {
        while(toIndex >= sequence.knownPrimes.size) {
            iterator.next()
        }
        return sequence.knownPrimes.subList(fromIndex, toIndex)
    }

    override fun lastIndexOf(element: Long): Int {
        return sequence.knownPrimes.lastIndexOf(element)
    }

    override fun indexOf(element: Long): Int {
        return sequence.knownPrimes.indexOf(element)
    }
}