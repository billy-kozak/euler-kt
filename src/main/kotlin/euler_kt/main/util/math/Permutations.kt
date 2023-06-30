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

package euler_kt.main.util.math

import java.math.BigInteger

private const val LARGEST_PERM_FOR_LONG = 18

fun Permute<Char>.lexiPermJoin(n: Long): String {
    return lexiPerm(n).joinToString("")
}

abstract class Permute <T> protected constructor(protected val tokens: List<T>) {
    abstract fun lexiPerm(n: Long): List<T>
    abstract fun lexiPerm(n: BigInteger): List<T>
}

private class SmallPermute <T> (tokens: List<T>): Permute<T>(tokens) {
    private val facts: Lazy<List<Long>> = lazy(this::computeFacts)

    override fun lexiPerm(n: Long): List<T> {
        val ret = ArrayList<T>(tokens.size)
        val remaining = tokens.indices.toMutableList()

        var x = n % facts.value.last()

        for(i in 0 until tokens.size - 1) {
            val d = facts.value[tokens.size - i - 1]
            val eIndex = x / d

            ret.add(tokens[remaining.removeAt(eIndex.toInt())])

            x %= d
        }

        ret.add(tokens[remaining[0]])

        return ret
    }

    override fun lexiPerm(n: BigInteger): List<T> {
        return lexiPerm((n % BigInteger.valueOf(facts.value.last())).toLong())
    }

    private fun computeFacts(): List<Long> {
        val ret = ArrayList<Long>(tokens.size + 1)
        ret.add(1L)
        var f = 1L
        for(i in 1..tokens.size) {
            f *= i
            ret.add(f)
        }
        return ret
    }
}

private class BigPermute <T> (tokens: List<T>): Permute<T>(tokens) {
    private val facts: Lazy<List<BigInteger>> = lazy(this::computeFacts)

    override fun lexiPerm(n: BigInteger): List<T> {
        val ret = ArrayList<T>(tokens.size)
        val remaining = tokens.indices.toMutableList()

        var x = n % facts.value.last()

        for(i in 0 until tokens.size - 1) {
            val d = facts.value[tokens.size - i - 1]
            val eIndex = x / d

            ret.add(tokens[remaining.removeAt(eIndex.toInt())])

            x %= d
        }

        ret.add(tokens[remaining[0]])

        return ret
    }

    override fun lexiPerm(n: Long): List<T> {
        return lexiPerm(n.toBigInteger())
    }

    private fun computeFacts(): List<BigInteger> {
        val ret = ArrayList<BigInteger>(tokens.size + 1)
        ret.add(BigInteger.ONE)
        var f = BigInteger.ONE
        for(i in 1..tokens.size) {
            f *= BigInteger.valueOf(i.toLong())
            ret.add(f)
        }
        return ret
    }
}

fun <T> permuteOf(tokens: List<T>): Permute<T> {
    return if(tokens.size <= LARGEST_PERM_FOR_LONG) {
        SmallPermute(tokens)
    } else {
        BigPermute(tokens)
    }
}