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

import euler_kt.main.util.coroutines.RecursiveSequence
import java.util.*

class FibonacciHeap<T: Comparable<T>> private constructor(
    private val roots: MutableList<FibonacciHeap<T>.FibNode>,
    private var minimum: FibonacciHeap<T>.FibNode? = null,
    private var numNodes: Int = 0
): MinHeap<T>  {

    constructor (): this(mutableListOf())

    /**
     * Merge the two heaps into one large heap. Both original heaps are emptied.
     *
     * @param other The other heap to merge with this one.
     * @return The merged heap.
     */
    fun merge(other: FibonacciHeap<T>): FibonacciHeap<T> {
        val newRoots = mutableListOf<FibNode>()

        newRoots.addAll(roots)
        newRoots.addAll(other.roots)

        val newMinimum = if(minimum == null) {
            other.minimum
        } else if(other.minimum == null) {
            minimum
        } else if(compareValues(minimum!!.data, other.minimum!!.data) <= 0) {
            minimum
        } else {
            other.minimum
        }
        val newNumNodes = numNodes + other.numNodes

        clear()
        other.clear()

        return FibonacciHeap(newRoots, newMinimum, newNumNodes)
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        return elements.all { contains(it) }
    }

    override fun peekMinNode(): MinHeap.HeapNode<T>? {
        return minimum
    }

    override fun containsNode(node: MinHeap.HeapNode<T>): Boolean {
        return node is FibNode && !node.isRemoved()
    }

    override fun clear() {
        roots.clear()
        minimum = null
    }

    /**
     * Elements are iterated in no particular order. Remove using iterator is not supported.
     */
    override fun iterator(): MutableIterator<T> {
        val seq = RecursiveSequence<T, List<FibNode>, Unit>(roots) { nodes ->
            for(node in nodes) {
                emit(node.data)
                callRecursive(node.children())
            }
        }
        val itr = seq.iterator()

        return object: MutableIterator<T> {
            override fun hasNext(): Boolean {
                return itr.hasNext()
            }

            override fun next(): T {
                return itr.next()
            }

            override fun remove() {
                throw UnsupportedOperationException("Remove not supported")
            }
        }
    }

    override fun remove(): T {
        if(minimum == null) {
            throw NoSuchElementException("Heap is empty")
        }
        val ret = pop()
        return ret ?: throw NoSuchElementException("Heap is empty")
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        var ret = false
        for(element in elements) {
            if(!contains(element)) {
                remove(element)
                ret = true
            }
        }
        return ret
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        var ret = false
        for(element in elements) {
            if(remove(element)) {
                ret = true
            }
        }
        return ret
    }

    override fun remove(element: T): Boolean {
        val node = findNode(element) ?: return false
        removeNode(node)
        return true
    }

    override fun isEmpty(): Boolean {
        return roots.isEmpty()
    }

    override fun poll(): T? {
        if(minimum == null) {
            return null
        }
        val retNode = minimum!!
        val ret = retNode.data

        roots.remove(minimum!!)
        roots.addAll(retNode.cutAllChildren())
        /*
            Alternatively, we can implement an array of size log2(numNodes).
            This is described on https://en.wikipedia.org/wiki/Fibonacci_heap

            I haven't tested, but I don't agree that it's faster than simple nested loops for any heap state
            which is likely to occur in most real world applications.
         */
        findSameDegree@while(true) {
            for(i in roots.indices) {
                for(j in i + 1 until roots.size) {
                    if(roots[i].degree() == roots[j].degree()) {
                        if(compareValues(roots[i].data, roots[j].data) < 0) {
                            roots[i].addChild(roots[j])
                            roots.removeAt(j)
                        } else {
                            roots[j].addChild(roots[i])
                            roots.removeAt(i)
                        }
                        continue@findSameDegree
                    }
                }
            }
            break
        }

        minimum = roots.minOrNull()
        numNodes -= 1

        retNode.setRemoved()

        return ret
    }

    override fun element(): T {
        return peek() ?: throw NoSuchElementException("Heap is empty")
    }

    override fun peek(): T? {
        return minimum?.data
    }

    override fun offer(p0: T): Boolean {
        return add(p0)
    }

    override fun contains(element: T): Boolean {
        return findNode(element) != null
    }

    override fun addAll(elements: Collection<T>): Boolean {
        for(element in elements) {
            add(element)
        }
        return true
    }

    override fun add(element: T): Boolean {
        push(element)
        return true
    }

    override fun push(element: T): MinHeap.HeapNode<T> {
        val newNode = FibNode(element)
        roots.add(newNode)
        if(minimum == null || compareValues(element, minimum!!.data) < 0) {
            minimum = newNode
        }
        numNodes += 1
        return newNode
    }

    override fun removeNode(node: MinHeap.HeapNode<T>): T {
        if(node !is FibNode) {
            throw IllegalArgumentException("Node is not a Fibonacci node")
        }

        node.makeInfinitelySmall()
        keyDecreased(node)
        pop()

        return node.data
    }

    override fun findNode(data: T): MinHeap.HeapNode<T>? {
        val search = DeepRecursiveFunction<List<FibNode>, FibNode?> { nodes ->
            for(node in nodes) {
                if(node.data == data) {
                    return@DeepRecursiveFunction node
                }
            }
            for(node in nodes) {
                if(compareValues(node.data, data) > 0) {
                    val ret = callRecursive(node.children())
                    if(ret != null) {
                        return@DeepRecursiveFunction ret
                    }
                }
            }
            return@DeepRecursiveFunction null
        }

        return search(roots)
    }

    override fun swap(oldData: T, newData: T): MinHeap.HeapNode<T> {
        val node = findNode(oldData) ?: throw NoSuchElementException("Node not found")
        return swap(node, newData)
    }

    override fun swap(node: MinHeap.HeapNode<T>, newData: T): MinHeap.HeapNode<T> {

        if(node !is FibNode) {
            throw IllegalArgumentException("Node is not a Fibonacci node")
        }

        val cmp = compareValues(newData, node.data)

        if(cmp < 0) {
            node.updateData(newData)
            keyDecreased(node)
        } else if(cmp > 0){
            removeNode(node)
            add(newData)
        }

        return node
    }


    private fun keyDecreased(node: FibNode) {
        if(node.parent() != null && compareValues(node, node.parent()) < 0) {
            var parent = node.parent()
            var thisNode = node
            while(parent != null) {
                if(!thisNode.isInfinitelySmall()) {
                    /* if infinitely small, it means we are trying to remove this node */
                    roots.add(thisNode)
                }
                if(parent.cutChild(thisNode)) {
                    /* parent was already marked */
                    thisNode = parent
                    parent = parent.parent()
                } else {
                    break
                }
            }
        }

        if(compareValues(node, minimum) < 0) {
            minimum = node
        }
    }

    override val size: Int get() = numNodes

    private inner class FibNode(
        data: T,
        private var parent: FibNode? = null,
        children: MutableList<FibNode>? = null
    ): MinHeap.HeapNode<T>, Comparable<FibNode> {

        private var children: MutableList<FibNode> = children ?: ArrayList()
        private var marked: Boolean = false
        private var infinitelySmall = false
        private var mutableData = data
        private var removed = false

        override val data: T get() = mutableData

        fun makeInfinitelySmall() {
            infinitelySmall = true
        }

        fun isInfinitelySmall(): Boolean {
            return infinitelySmall
        }

        fun degree(): Int {
            return children.size
        }

        fun parent(): FibNode? {
            return parent
        }

        fun children(): List<FibNode> {
            return children
        }

        fun cutChild(child: FibNode): Boolean {
            val ret = marked
            children.remove(child)
            child.parent = null
            child.marked = false
            marked = true

            return ret
        }

        fun cutAllChildren(): Sequence<FibNode> = sequence {
            marked = true
            for(child in children) {
                child.parent = null
                child.marked = false
                yield(child)
            }
            children.clear()
        }

        fun addChild(child: FibNode) {
            children.add(child)
            child.parent = this
        }

        fun updateData(newData: T) {
            mutableData = newData
        }

        fun setRemoved() {
            removed = true
        }

        fun isRemoved(): Boolean {
            return removed
        }

        override fun compareTo(other: FibNode): Int {
            return if(infinitelySmall) {
                -1
            } else {
                compareValues(data, other.data)
            }
        }
    }
}