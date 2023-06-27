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

package euler_kt.main.util.sorting

import euler_kt.main.util.coroutines.RecursiveSequence

/**
 * Perform an MSB radix sort
 *
 * Best used where the most significant radix of the type is easily known. For instance, T is an integer and the
 * maximum integer is known or if sorting strings alphabetically.
 *
 * @param getRadix a function that returns the nth radix of an element of the input collection where zero is the most
 *  significant radix.
 *  @param maxRadix the highest possible radix value.
 *  @param radixIterations the number of recursive radix sorts to perform. Once this value is reached, the builtin
 *   sorting algorithm is used to sort the bucket.
 *
 *   @return The sorted list.
 */
fun <T: Comparable<T>> Iterable<T>.hybridMsbRadixSort(
    radixIterations: Int, maxRadix: Int, getRadix: (T, Int) -> Int
): Sequence<T> {

    val f: Sequence<T> = RecursiveSequence(Pair(this, 0)) { args ->
        val radixArray = arrayOfNulls<MutableList<T>?>(maxRadix + 1)
        val items = args.first
        val depth = args.second

        for(item in items) {
            radixArray.getListOrConstruct(getRadix(item, depth)).add(item)
        }

        if((depth + 1) == radixIterations) {
            for(list in radixArray) {
                if(list != null) {
                    emitAll(list.sorted())
                }
            }
        } else {
            for(list in radixArray) {
                if(list != null) {
                    callRecursive(Pair(list, depth + 1))
                }
            }
        }
    }

    return f
}

private fun <T> Array<MutableList<T>?>.getListOrConstruct(idx: Int): MutableList<T> {
    var r = this[idx]
    if(r == null) {
        r = mutableListOf()
        this[idx] = r
    }
    return r
}