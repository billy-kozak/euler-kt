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

private const val YEAR_START = 1901
private val DAYS_IN_MONTH = arrayOf(
    31, // January
    28, // February
    31, // March
    30, // April
    31, // May
    30, // June
    31, // July
    31, // August
    30, // September
    31, // October
    30, // November
    31 // December
)

private fun daysInFeb(year: Int): Int {
    return if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) {
        29
    } else {
        28
    }
}

private fun daysInMonth(month: Int, year: Int): Int {
    return when (month) {
        1 -> daysInFeb(year)
        else -> DAYS_IN_MONTH[month]
    }
}

class Problem19(override val defaultKeyParam: Int = 2001) : EulerProblem<Int, Int> {
    override fun description(): String {
        return """
            Problem 19:
            You are given the following information, but you may prefer to do some research for yourself.
            
            1 Jan 1900 was a Monday.
            Thirty days has September,
            April, June and November.
            All the rest have thirty-one,
            Saving February alone,
            Which has twenty-eight, rain or shine.
            And on leap years, twenty-nine.
            A leap year occurs on any year evenly divisible by 4, but not on a century unless it is divisible by 400.
            How many Sundays fell on the first of the month during the twentieth century (1 Jan 1901 to 31 Dec 2000)?
        """.trimIndent()
    }

    override fun validate(result: Number): Boolean {
        return result == 171
    }

    override fun run(keyParam: Int): Int {
        var year = 1900
        var month = 0
        var dayOfWeek = 1

        /** There is probably some clever way to exploit the pattern in the days in months to make this faster.
         * But I'm just not interested; it's pretty mundane work. */
        /** On two consecutive non-leap years, the first of the month is one day of week later. A leap year advances
         * the day of week by one more day. That could probably be exploited. */

        while(year < YEAR_START) {
            dayOfWeek = (dayOfWeek + daysInMonth(month, year)) % 7
            month = (month + 1) % 12
            if (month == 0) {
                year++
            }
        }

        var sundays = 0
        while (year < keyParam) {
            if (dayOfWeek == 0) {
                sundays++
            }
            dayOfWeek = (dayOfWeek + daysInMonth(month, year)) % 7
            month = (month + 1) % 12
            if (month == 0) {
                year++
            }
        }

        return sundays
    }
}