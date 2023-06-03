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

class GrowOnlyArrayList<T> : GrowOnlyList<T> {

    private val delegate: MutableList<T> = mutableListOf()

    override fun add(element: T) {
        delegate.add(element)
    }

    override val size: Int
        get() = delegate.size

    override fun get(index: Int): T {
        return delegate.get(index)
    }

    override fun isEmpty(): Boolean {
        return delegate.isEmpty()
    }

    override fun iterator(): Iterator<T> {
        return delegate.iterator()
    }

    override fun listIterator(): ListIterator<T> {
        return delegate.listIterator()
    }

    override fun listIterator(index: Int): ListIterator<T> {
        return delegate.listIterator(index)
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<T> {
        return delegate.subList(fromIndex, toIndex)
    }

    override fun lastIndexOf(element: T): Int {
        return delegate.lastIndexOf(element)
    }

    override fun indexOf(element: T): Int {
        return delegate.indexOf(element)
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        return delegate.containsAll(elements)
    }

    override fun contains(element: T): Boolean {
        return delegate.contains(element)
    }

}