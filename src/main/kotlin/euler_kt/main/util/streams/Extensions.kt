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

package euler_kt.main.util.streams

import java.util.*
import java.util.stream.Stream

fun <K: Comparable<K>, V> Stream<Pair<K, V>>.toTreeMap(): TreeMap<K, V> {
    val map = TreeMap<K, V>()
    this.forEach {(k, v) -> map[k] = v}
    return map
}

fun <T: Comparable<T>> Stream<T>.toTreeSet(): TreeSet<T> {
    val set = TreeSet<T>()
    this.forEach {set.add(it)}
    return set
}

fun <T> Stream<T>.toHashSet(): HashSet<T> {
    val set = HashSet<T>()
    this.forEach {set.add(it)}
    return set
}

