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

package euler_kt.main.util.primes

import kotlin.test.Test
import kotlin.test.assertContentEquals


class EratosthenesTest {
    @Test
    fun testEratWithWheel() {
        val max = Precompute.largestPrime1024().toLong()
        val simple = eratosthenes(max)
        val wheel = eratosthenesWithWheelFactorization(max)

        assertContentEquals(
            simple,
            wheel
        )
    }
}