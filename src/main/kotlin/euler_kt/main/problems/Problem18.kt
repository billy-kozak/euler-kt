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

package euler_kt.main.problems

import euler_kt.main.framework.EulerProblem
import euler_kt.main.util.strings.triangularize
import euler_kt.main.util.structures.FibbonacciHeap
import euler_kt.main.util.structures.MinHeap

private const val MAGIC_STRING: String = (
    "75\n" +
    "95 64\n" +
    "17 47 82\n" +
    "18 35 87 10\n" +
    "20 04 82 47 65\n" +
    "19 01 23 75 03 34\n" +
    "88 02 77 73 07 63 67\n" +
    "99 65 04 28 06 16 70 92\n" +
    "41 41 26 56 83 40 80 70 33\n" +
    "41 48 72 33 47 32 37 16 94 29\n" +
    "53 71 44 65 25 43 91 52 97 51 14\n" +
    "70 11 33 28 77 73 17 78 39 68 17 57\n" +
    "91 71 52 38 17 14 91 43 58 50 27 29 48\n" +
    "63 66 04 68 89 53 67 30 73 16 69 87 40 31\n" +
    "04 62 98 27 23 09 70 98 73 93 38 53 60 04 23"
)

private fun parseMagicString(): List<List<Int>> {
    return MAGIC_STRING.split("\n").map { it.split(" ").map { it.toInt() } }
}

/**
 * Problem 18: Maximum path sum
 *
 * This one is hard to define a key parameter for as, really, the basis for the problem is the input triangle.
 * We need to prevent JVM from constant folding the answer, however, so we'll take the key parameter as informing
 * the order in which we traverse neighbors of a given node so that the JVM (hopefully) can't figure out that all
 * paths lead to the same answer.
 */
class Problem18(override val defaultKeyParam: Int = 1) : EulerProblem<Int, Int> {

    private val triangle: List<List<Int>> = parseMagicString()

    override fun description(): String {
        return (
            "Problem 18: Maximum path sum\n" +
            "By starting at the top of the triangle below and moving to adjacent numbers on the row below, the " +
            "maximum total from top to bottom is 23.\n" +
            (
                "\n" +
                "3\n" +
                "7 4\n" +
                "2 4 6\n" +
                "8 5 9 3\n"
            ).triangularize() +
            "\n" +
            "That is, 3 + 7 + 4 + 9 = 23.\n" +
            "\n" +
            "Find the maximum total from top to bottom of the triangle below:\n" +
            MAGIC_STRING.triangularize() +
            "\n" +
            "NOTE: As there are only 16384 routes, it is possible to solve this problem by trying every route. " +
            "However, Problem 67, is the same challenge with a triangle containing one-hundred rows; it cannot be " +
            "solved by brute force, and requires a clever method! ;o)"
        )
    }

    override fun explain(): String {
        return "Solve using Dijkstra's algorithm."
    }

    override fun validate(result: Number): Boolean {
        return result == 1074
    }

    override fun run(keyParam: Int): Int {
        var maxPath = -1
        val q = FibbonacciHeap<DijkstraNode>()
        val heapNodeMatrix = mutableListOf<MutableList<MinHeap.HeapNode<DijkstraNode>?>>()

        heapNodeMatrix.add(mutableListOf(q.push(DijkstraNode(Vertex(triangle[0][0], 0, 0), triangle[0][0]))))

        for(i in 1 until triangle.size) {
            heapNodeMatrix.add(ArrayList())
        }

        fun enumerateNeighbors(vertex: Vertex): Sequence<Vertex> = sequence {
            if(vertex.row != triangle.size - 1) {
                val i = vertex.row + 1
                var j = vertex.col

                if(keyParam == 1) {
                    yield(Vertex(triangle[i][j], i, j))
                    j += 1
                    yield(Vertex(triangle[i][j], i, j))
                } else {
                    j += 1
                    yield(Vertex(triangle[i][j], i, j))
                    j -= 1
                    yield(Vertex(triangle[i][j], i, j))
                }
            }
        }

        fun neighborHeapNode(vertex: Vertex): MinHeap.HeapNode<DijkstraNode>? {
            return heapNodeMatrix[vertex.row].getOrNull(vertex.col)
        }

        fun putNeighborHeapNode(vertex: Vertex, node: MinHeap.HeapNode<DijkstraNode>) {
            val list = heapNodeMatrix[vertex.row]
            while(list.size <= vertex.col) {
                list.add(null)
            }
            list[vertex.col] = node
        }

        while(q.isNotEmpty()) {
            val dNode = q.pop()!!

            val distance = dNode.distance

            for (vertex in enumerateNeighbors(dNode.vertex)) {
                val alt = distance + vertex.value
                val hNode = neighborHeapNode(vertex)

                if(hNode == null) {
                    val node = q.push(DijkstraNode(vertex, alt))
                    putNeighborHeapNode(vertex, node)
                }
                else if (alt > hNode.data.distance) {
                    val node = q.swapOrAddIfNotPresent(hNode, DijkstraNode(hNode.data.vertex, alt))
                    heapNodeMatrix[hNode.data.vertex.row][hNode.data.vertex.col] = node
                }

                if(alt > maxPath) {
                    maxPath = alt
                }
            }
        }
        return maxPath
    }

    private class Vertex(
        val value: Int,
        val row: Int,
        val col: Int
    ) {
        override fun equals(other: Any?): Boolean {
            if(other !is Vertex) {
                return false
            }
            return this.row == other.row && this.col == other.col
        }

        override fun hashCode(): Int {
            return row + col
        }

        override fun toString(): String {
            return "Vertex($row, $col)->${value}"
        }
    }

    private class DijkstraNode(
        val vertex: Vertex,
        val distance: Int
    ): Comparable<DijkstraNode> {

        override fun compareTo(other: DijkstraNode): Int {
            return other.distance.compareTo(this.distance)
        }

        override fun toString(): String {
            return "DijkstraNode(v=$vertex, d=$distance)"
        }
    }
}