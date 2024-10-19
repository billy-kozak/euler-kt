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

package euler_kt.main.util.structures

class LazyList<T>(private val itr: Iterator<T>): List<T> {

    private val storedValues = mutableListOf<T>()
    override val size: Int by lazy {computeSize()}

    override fun get(index: Int): T {
        evaluateToIndex(index)
        return storedValues[index]
    }

    override fun isEmpty(): Boolean {
        return !itr.hasNext() && storedValues.isEmpty()
    }

    override fun iterator(): Iterator<T> {
        return object : Iterator<T> {
            private var index = 0
            override fun hasNext(): Boolean {
                return index < storedValues.size || itr.hasNext()
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
        evaluateFully()
        return storedValues.listIterator(index)
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<T> {
        evaluateToIndex(toIndex)
        return storedValues.subList(fromIndex, toIndex)
    }

    override fun lastIndexOf(element: T): Int {
        evaluateFully()
        return storedValues.lastIndexOf(element)
    }

    override fun indexOf(element: T): Int {
        val storedIndex = storedValues.indexOf(element)
        if(storedIndex != -1) {
            return storedIndex
        } else {
            while(true) {
                val next = getNext()
                if(next == null) {
                    return -1
                } else if(next == element) {
                    return storedValues.size - 1
                }
            }
        }
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        return elements.all {contains(it)}
    }

    override fun contains(element: T): Boolean {
        return indexOf(element) != -1
    }

    private fun evaluateToIndex(index: Int) {
        var readSize = index - storedValues.size + 1

        while(readSize > 0 && itr.hasNext()) {
            storedValues.add(itr.next())
            readSize -= 1
        }
    }

    private fun getNext(): T? {
        if(itr.hasNext()) {
            val idx = storedValues.size
            storedValues.add(itr.next())
            return storedValues[idx]
        } else {
            return null
        }
    }

    private fun evaluateFully() {
        evaluateToIndex(Int.MAX_VALUE)
    }

    private fun computeSize(): Int {
        evaluateFully()
        return storedValues.size
    }
}