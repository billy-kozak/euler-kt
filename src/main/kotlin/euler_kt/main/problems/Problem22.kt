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
import euler_kt.main.util.io.readTextResourceAsUtf8
import euler_kt.main.util.sorting.hybridMsbRadixSort

/**
 * Implement problem 22
 *
 * The output of this problem is based solely on the input file names.txt, but since we don't want to chance the
 * optimizer recognizing the function as pure no-args and optimizing to constant return, we'll add a dummy parameter
 * which we will multiply to each word score so that it can't be optimized.
 *
 * The alternative would be to make the names list an input parameter, but this is awkward to do given the current
 * architecture, mainly because of the JMH benchmark library. Furthermore, we don't want the resource I/O to become
 * part of the profile time, because it's likely to dominate the run time if we do so, and so we want to load it in
 * during class construction instead.
 */
abstract class Problem22(override val defaultKeyParam: Int = 1) : EulerProblem<Int, Int> {

    protected val names: List<String> = readTextResourceAsUtf8("problems/p22/names.txt")
        .split(",")
        .filter{it.isNotBlank()}
        .map{it.substring(1, it.length - 1)}

    override fun description(): String {
        return """
            Problem 22:
            Using names.txt (right click and 'Save Link/Target As...'), a 46K text file containing over five-thousand
            first names, begin by sorting it into alphabetical order. Then working out the alphabetical value for each
            name, multiply this value by its alphabetical position in the list to obtain a name score.
            
            For example, when the list is sorted into alphabetical order, COLIN, which is worth 3 + 15 + 12 + 9 + 14 =
            53, is the 938th name in the list. So, COLIN would obtain a score of 938 × 53 = 49714.
            
            What is the total of all the name scores in the file?
        """.trimIndent()
    }

    override fun validate(result: Number): Boolean {
        return result == 871198282
    }
}

class Problem22a(defaultKeyParam: Int = 1): Problem22(defaultKeyParam) {

    override fun explain(): String {
        return "Simple solution, based on streams and builtin sort."
    }

    override fun run(keyParam: Int): Int {
        return names
            .sorted()
            .mapIndexed{index, name ->
                name.map {c -> (c - 'A' + 1)}.sum() * (index + keyParam)
            }.sum()
    }
}

class Problem22b(defaultKeyParam: Int = 1): Problem22(defaultKeyParam) {

    override fun explain(): String {
        return "Radix sort solution"
    }

    override fun run(keyParam: Int): Int {

        return names
            .hybridMsbRadixSort(1, 26 * 27 * 27, ::getStrRadix)
            .mapIndexed{index, name ->
                /* This for loop does seem to be significantly faster than the map->sum of 22a */
                var r = 0
                for(c in name) {
                    r += (c - 'A' + 1)
                }
                r * (index + keyParam)
            }.sum()
    }

    private fun getAlphaValue(s: String, i: Int): Int {
        return if(i >= s.length) {
            0
        } else {
            s[i] - 'A' + 1
        }
    }

    /**
     * Three character radix value.
     *
     * We can cheat a bit because we know that no name has less than two characters.
     *
     * According to tests, the three character variant seems to be fastest.
     */
    private fun getStrRadix(s: String, i: Int): Int {
        val r1 = getAlphaValue(s, i * 3)
        val r2 = getAlphaValue(s, i * 3 + 1)
        val r3 = getAlphaValue(s, i * 3 + 2)

        return r1 * 26 * 27 + r2 * 27 + r3
    }
}

