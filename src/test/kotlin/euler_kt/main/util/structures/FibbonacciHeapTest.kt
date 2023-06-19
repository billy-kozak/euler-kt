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

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FibbonacciHeapTest {
    @Test
    fun testAlreadySorted() {
        val heap = FibbonacciHeap<Int>()

        heap.add(1)
        heap.add(2)
        heap.add(3)
        heap.add(4)
        heap.add(5)

        assertEquals(1, heap.peek())
        assertEquals(1, heap.poll())
        assertEquals(2, heap.peek())
        assertEquals(2, heap.poll())
        assertEquals(3, heap.peek())
        assertEquals(3, heap.poll())
        assertEquals(4, heap.peek())
        assertEquals(4, heap.poll())
        assertEquals(5, heap.peek())
        assertEquals(5, heap.poll())
    }

    @Test
    fun testBasicPriorityQueueFunctionality() {
        val heap = FibbonacciHeap<Int>()

        val elements = listOf(5, 4, 3, 2, 1, 7, 0, -3, 12, 107, 123, 54)
        heap.addAll(elements)

        val poppedOff = sequence {
            while (heap.isNotEmpty()) {
                yield(heap.poll())
            }
        }.toList()
        assertContentEquals(elements.sorted(), poppedOff)
    }

    @Test
    fun testDecreaseKey() {
        val heap = FibbonacciHeap<Int>()

        val inputVector = mutableListOf(5, 4, 3, 2, 1, 7, 0, -3, 12, 107, 123, 54)
        heap.addAll(inputVector)

        heap.swap(12, -4)

        val outputVector = ArrayList(inputVector)
        outputVector.remove(12)
        outputVector.add(-4)

        val poppedOff = sequence {
            while (heap.isNotEmpty()) {
                yield(heap.poll())
            }
        }.toList()

        assertContentEquals(outputVector.sorted(), poppedOff)
    }

    @Test
    fun testIncreaseKey() {
        val heap = FibbonacciHeap<Int>()

        val inputVector = mutableListOf(5, 4, 3, 2, 1, 7, 0, -3, 12, 107, 123, 54)
        heap.addAll(inputVector)

        heap.swap(12, 37)

        val outputVector = ArrayList(inputVector)
        outputVector.remove(12)
        outputVector.add(37)

        val poppedOff = sequence {
            while (heap.isNotEmpty()) {
                yield(heap.poll())
            }
        }.toList()

        assertContentEquals(outputVector.sorted(), poppedOff)
    }

    @Test
    fun testPushPopPushPop() {
        val heap = FibbonacciHeap<Int>()

        heap.add(5)
        heap.add(2)

        assertEquals(2, heap.pop())

        heap.add(7)

        assertEquals(5, heap.pop())

        heap.add(-1)
        heap.add(3)

        assertEquals(3, heap.size)

        assertEquals(-1, heap.pop())
        assertEquals(3, heap.pop())
        assertEquals(7, heap.pop())

        assertTrue(heap.isEmpty())
        assertEquals(0, heap.size)
    }
}