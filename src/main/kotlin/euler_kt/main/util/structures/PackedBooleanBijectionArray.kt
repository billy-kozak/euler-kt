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

class PackedBooleanBijectionArray <in N: Number> private constructor (
    private inline val bijection: (N) -> Long, private val array: LongArray
) {
    constructor(
        max: N, bijection: (N) -> Long, init: (Int) -> Long
    ): this(bijection, LongArray(((bijection(max) + 65) / 64).toInt(), init))

    constructor(
        max: N, bijection: (N) -> Long
    ): this(bijection, LongArray(((bijection(max) + 65) / 64).toInt()))

    operator fun get(index: N): Boolean {
        val (byteIdx, bitIdx) = resolveIndex(bijection(index))
        val mask = 1L shl bitIdx

        try {
            return (array[byteIdx] and mask) != 0L
        } catch (e: ArrayIndexOutOfBoundsException) {
            throw ArrayIndexOutOfBoundsException(
                "Index $index->${byteIdx}.${bitIdx} is out of bounds for array of size ${array.size}"
            )
        } catch(e: NegativeArraySizeException) {
            throw NegativeArraySizeException("Index $index->${byteIdx}.${bitIdx} was negative")
        }
    }

    operator fun set(index: N, value: Boolean) {
        val (byteIdx, bitIdx) = resolveIndex(bijection(index))
        val mask = 1L shl bitIdx
        try {
            val byteVal = array[byteIdx]
            array[byteIdx] = if (value) {
                byteVal or mask
            } else {
                byteVal and mask.inv()
            }
        } catch (e: ArrayIndexOutOfBoundsException) {
            throw ArrayIndexOutOfBoundsException(
                "Index $index->${byteIdx}.${bitIdx} is out of bounds for array of size ${array.size}"
            )
        } catch(e: NegativeArraySizeException) {
            throw NegativeArraySizeException(
                "Index $index->${byteIdx}.${bitIdx} was negative"
            )
        }
    }

    private fun resolveIndex(idx: Long): Pair<Int, Int> {
        val byteIdx = (idx / 64L).toInt()
        val bitIdx = (idx % 64L).toInt()

        return Pair(byteIdx, bitIdx)
    }
}