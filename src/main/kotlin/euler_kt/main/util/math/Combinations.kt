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

fun nChooseK(n: Int, k: Int): Long {
    return nChooseK(n.toLong(), k.toLong())
}

fun nChooseK(n: Long, k: Long): Long {
    var r = 1L

    val m = if(k > ((n +1) / 2L)) {
        n - k
    } else {
        k
    }

    for(j in 0L until m) {
        r = (r * (n - j)) / (j + 1L)
    }
    return r
}