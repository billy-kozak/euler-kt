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

import java.util.*

/**
 * Works as a priority queue, but also implements several common heap operations, not available on Java PriorityQueues.
 */
interface MinHeap<T: Comparable<T>>: Queue<T> {

    interface HeapNode<T> {
        val data: T
    }

    /**
     * Adds the specified element to this heap and returns the newly created node object.
     *
     * @return the node representing the data's location within the heap. Can be used to speed up some operations.
     */
    fun push(element: T): HeapNode<T>

    /**
     * Retrieves and removes the minimum element of this heap, or returns null if this heap is empty.
     *
     * @return minimum element of this heap, or null if this heap is empty
     */
    fun pop(): T? {
        return poll()
    }

    /**
     * Returns minimum heap node without removing it.
     *
     * Heap nodes can be used for more efficient operations on the heap, such as updating the priority of a node.
     *
     * @return minimum heap node if the heap is not empty, null otherwise
     */
    fun peekMinNode(): HeapNode<T>?

    /**
     * Swap new data into the place of the given heap node. Enables updating priority as efficiently as possible.
     *
     * @param node node to swap out. Must be a node which is a member of this heap.
     * @param newData new data to swap in
     * @return The node representing the new data's location within the heap.
     */
    fun swap(node: HeapNode<T>, newData: T): HeapNode<T>

    /**
     * Swap new data into the place of old data. Not as efficient as swapping a node (most of the time).
     *
     * @param oldData data to swap out.
     * @param newData new data to swap in
     *
     * @throws NoSuchElementException if oldData is not a member of this heap
     *
     * @return The node representing the new data's location within the heap.
     */
    fun swap(oldData: T, newData: T): HeapNode<T>

    /**
     * Find a node anywhere in the heap.
     *
     * Nodes contain the use data passed into the heap and can be used to make some heap operations more efficient.
     *
     * @param data data to search for. Both the priority and data must be equal to the data in the heap for it to match.
     */
    fun findNode(data: T): HeapNode<T>?

    /**
     * Remove a node from anywhere in the heap.
     *
     * Removing via node is more efficient than removing via data (most of the time).
     *
     * @param node node to remove. Must be a node which is a member of this heap.
     * @return data contained in the node
     */
    fun removeNode(node: HeapNode<T>): T

    /**
     * Return true if this node a member of this heap.
     *
     * **Warning:** The given node must have been returned by this heap at some point. If returned from another
     * heap the results are undefined.
     *
     * @param node node to check
     * @return true if this node a member of this heap, false otherwise
     */
    fun containsNode(node: HeapNode<T>): Boolean

    /**
     * Swap new data into the place of old data if it is present, otherwise add it.
     *
     * **Warning:** The given node must have been returned by this heap at some point. If returned from another
     * heap the results are undefined.
     *
     * @param node node to swap out. Must be a node which was at some point a member of this heap.
     * @param newData new data to swap in
     *
     * @return The node representing the new data's location within the heap.
     */
    fun swapOrAddIfNotPresent(node: HeapNode<T>, newData: T): HeapNode<T> {
        return if(containsNode(node)) {
            swap(node, newData)
        } else {
            push(newData)
        }
    }
}