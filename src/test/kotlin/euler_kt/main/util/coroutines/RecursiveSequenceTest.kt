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

package euler_kt.main.util.coroutines

import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class RecursiveSequenceTest {
    @Test
    fun testSimple() {
        val seq = RecursiveSequence(1) { p ->
            if (p < 10) {
                emit(p)
                callRecursive(p + 1)
            }
        }
        assertContentEquals(sequenceOf(1, 2, 3, 4, 5, 6, 7, 8, 9), seq)
    }

    @Test
    fun testRecursiveReturnValues() {
        val seq = RecursiveSequence(0) { p ->
            if (p < 10) {
                emit(callRecursive(p + 1))
                return@RecursiveSequence p
            } else {
                return@RecursiveSequence 10
            }
        }
        assertContentEquals(sequenceOf(10, 9, 8, 7, 6, 5, 4, 3, 2, 1), seq)
    }

    @Test
    fun testLaziness() {
        val counter: AtomicInteger = AtomicInteger(0)

        val seq = RecursiveSequence(1) { p ->
            counter.incrementAndGet()
            if (p < 10) {
                emit(p)
                callRecursive(p + 1)
            }
        }
        assertContentEquals(listOf(1, 2, 3, 4, 5), seq.take(5).toList())
        assertEquals(5, counter.get())
    }

    @Test
    fun testTreeTraversal() {
        class Node {
            val value: Int?
            val left: Node?
            val right: Node?
            constructor(value: Int) {
                this.value = value
                this.left = null
                this.right = null
            }
            constructor(left: Node, right: Node) {
                this.value = null
                this.left = left
                this.right = right
            }
            fun isLeaf(): Boolean {
                return value != null
            }
        }

        val tree = Node(
            Node(Node(1), Node(2)),
            Node(Node(3), Node(4)),
        )

        val traverse = RecursiveSequence(tree) { node ->
            if (node.isLeaf()) {
                emit(node.value!!)
            } else {
                if(node.left != null) {
                    callRecursive(node.left)
                }
                if(node.right != null) {
                    callRecursive(node.right)
                }
            }
        }
        assertContentEquals(sequenceOf(1, 2, 3, 4), traverse)
        assertContentEquals(listOf(1, 2), traverse.take(2).toList())
    }
}