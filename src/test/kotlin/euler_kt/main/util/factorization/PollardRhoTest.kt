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

package euler_kt.main.util.factorization

import kotlin.test.Test
import kotlin.test.assertContentEquals

class PollardRhoTest {
    @Test
    fun testSimple() {

        var factors = listOf(2L, 3L, 5L, 7L, 11L)
        var n = factors.fold(1L, Long::times)

        // This works, but is not a general solution as the initial parameters and number of each invocation were
        // guess and tested
        var r1 = pollardRhoFactorize(n)
        var r2 = pollardRhoFactorize(r1, x0=2, g=::pollardRhoPolly1)
        var r3 = pollardRhoFactorize(r2, x0=3, g=::pollardRhoPolly0)

        assertContentEquals(factors, r3.sorted().toList())
    }
}