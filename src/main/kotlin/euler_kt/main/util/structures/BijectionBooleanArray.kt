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

package euler_kt.main.util.structures

class BijectionBooleanArray <in N: Number> private constructor (
    private inline val bijection: (N) -> Int, private val array: BooleanArray
) {
    constructor(
        max: N, bijection: (N) -> Int, init: (Int) -> Boolean
    ): this(bijection, BooleanArray(bijection(max) + 1, init))

    constructor(
        max: N, bijection: (N) -> Int
    ): this(bijection, BooleanArray(bijection(max) + 1))

    operator fun get(index: N): Boolean {
        val b = bijection(index)
        try {
            return array[b]
        } catch (e: ArrayIndexOutOfBoundsException) {
            throw ArrayIndexOutOfBoundsException("Index $index->${b} is out of bounds for array of size ${array.size}")
        } catch(e: NegativeArraySizeException) {
            throw NegativeArraySizeException("Index $index->${b} was negative")
        }
    }

    operator fun set(index: N, value: Boolean) {
        val b = bijection(index)
        try {
            array[b] = value
        } catch (e: ArrayIndexOutOfBoundsException) {
            throw ArrayIndexOutOfBoundsException("Index $index->${b} is out of bounds for array of size ${array.size}")
        } catch(e: NegativeArraySizeException) {
            throw NegativeArraySizeException("Index $index->${b} was negative")
        }
    }
}