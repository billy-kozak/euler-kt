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

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PermutationsTest {
    @Test
    fun testLexicographicalThreeItems() {
        val p = permuteOf("012".toList())
        assertEquals("012", p.lexiPermJoin(0))
        assertEquals("021", p.lexiPermJoin(1))
        assertEquals("102", p.lexiPermJoin(2))
        assertEquals("120", p.lexiPermJoin(3))
        assertEquals("201", p.lexiPermJoin(4))
        assertEquals("210", p.lexiPermJoin(5))
    }
}