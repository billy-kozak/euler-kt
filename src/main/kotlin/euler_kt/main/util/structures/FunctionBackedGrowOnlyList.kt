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

class FunctionBackedGrowOnlyList<T>(private val funcMax: Int, private inline val func: (Int) -> T) : GrowOnlyList<T> {

    private val extension = mutableListOf<T>()

    override val size: Int get() = funcMax + extension.size

    override fun get(index: Int): T {
        return if (index < funcMax) {
            func(index)
        } else {
            extension[index - funcMax]
        }
    }

    override fun isEmpty(): Boolean {
        return size == 0 && extension.isEmpty()
    }

    override fun iterator(): Iterator<T> {
        return object : Iterator<T> {
            private var index = 0
            override fun hasNext(): Boolean {
                return index < size
            }

            override fun next(): T {
                return get(index++)
            }
        }
    }

    override fun listIterator(): ListIterator<T> {
        return listIterator(0)
    }

    override fun listIterator(index: Int): ListIterator<T> {
        return object : ListIterator<T> {
            private var idx = index
            override fun hasNext(): Boolean {
                return index < size
            }

            override fun next(): T {
                return get(idx++)
            }

            override fun hasPrevious(): Boolean {
                return idx > 0
            }

            override fun nextIndex(): Int {
                return idx
            }

            override fun previous(): T {
                return get(--idx)
            }

            override fun previousIndex(): Int {
                return idx - 1
            }
        }
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<T> {
        if(fromIndex < 0 || toIndex > size || fromIndex > toIndex) {
            throw IndexOutOfBoundsException()
        }

        return FunctionBackedGrowOnlyList(toIndex - fromIndex) {i: Int -> this[i]}
    }

    override fun lastIndexOf(element: T): Int {
        val index = extension.lastIndexOf(element)
        if(index != -1) {
            return index + funcMax
        }

        for(i in funcMax - 1 downTo 0) {
            if(func(i) == element) {
                return i
            }
        }
        return -1;
    }

    override fun indexOf(element: T): Int {
        for(i in 0 .. funcMax) {
            if(func(i) == element) {
                return i
            }
        }

        val index = extension.indexOf(element)
        if(index != -1) {
            return index + funcMax
        }
        return -1;
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        for(element in elements) {
            if(!contains(element)) {
                return false
            }
        }
        return true
    }

    override fun contains(element: T): Boolean {
        for(i in 0 .. funcMax) {
            if(func(i) == element) {
                return true
            }
        }

        return extension.contains(element)
    }

    override fun add(element: T) {
        extension.add(element)
    }
}