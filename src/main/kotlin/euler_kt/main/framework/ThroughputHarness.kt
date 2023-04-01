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

package euler_kt.main.framework

import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class ThroughputHarness private constructor(
    warmupTime: Long,
    maximumWarmupTime: Long,
    warmupTimeUnit: TimeUnit,
    warmupIterations: Int,
    problemNumber: Int,
    problem: EulerProblem<*, *>,
    private val measurementTime: Long,
    private val measurementTimeUnit: TimeUnit
) : ProblemHarness(
    warmupTime,
    maximumWarmupTime,
    warmupTimeUnit,
    warmupIterations,
    problemNumber,
    problem
) {

    override fun benchmark(): ProblemResult {
        val warmupMeasurement = warmup()

        if(warmupMeasurement.getRunTime() >= warmupTimeUnit.toMicros(maximumWarmupTime)) {
            return measurementToThroughputResult(warmupMeasurement)
        }
        val measurement = measureThroughput()
        return measurementToThroughputResult(measurement)
    }

    private fun measureThroughput(): Measurement = runBlocking {
        val ret = Measurement()
        val t0  = monoTimeMicros()

        val job = launch {
            while ((monoTimeMicros() - t0) < measurementTimeUnit.toMicros(measurementTime)) {
                val r = measure()
                ret.add(r.second, r.first)
                yield()
            }
        }
        delay(measurementTimeUnit.toMillis(measurementTime))
        job.cancelAndJoin()
        return@runBlocking ret
    }

    private fun measurementToThroughputResult(m: Measurement): ThroughputResult {
        return ThroughputResult(
            problemNumber,
            m.getRuns() * (measurementTimeUnit.toMicros(measurementTime).toDouble() / m.getRunTime().toDouble()),
            m.getAnswer(),
            m.getValid(),
            measurementTime,
            measurementTimeUnit
        )
    }

    class Builder {

        private var warmupTime: Long = 1
        private var maximumWarmupTime: Long = 10
        private var warmupTimeUnit: TimeUnit = TimeUnit.SECONDS
        private var warmupIterations: Int = 10
        private var problem: EulerProblem<*,*>? = null
        private var problemNumber: Int = 0
        private var measurementTime: Long = 10
        private var measurementTimeUnit: TimeUnit = TimeUnit.SECONDS

        fun measurementTime(measurementTime: Long, measurementTimeUnit: TimeUnit = TimeUnit.SECONDS) = apply {
            this.measurementTime = measurementTime
            this.measurementTimeUnit = measurementTimeUnit
        }
        fun warmupTime(warmupTime: Long) = apply {
            this.warmupTime = warmupTime
        }
        fun maximumWarmupTime(maximumWarmupTime: Long) = apply {
            this.maximumWarmupTime = maximumWarmupTime
        }
        fun warmupTimeUnit(warmupTimeUnit: TimeUnit) = apply {
            this.warmupTimeUnit = warmupTimeUnit
        }
        fun warmupIterations(warmupIterations: Int) = apply {
            this.warmupIterations = warmupIterations
        }
        fun problem(problemNumber: Int, problem: EulerProblem<*,*>) = apply {
            this.problemNumber = problemNumber
            this.problem = problem
        }

        fun build(): ThroughputHarness {
            val localProblem = problem ?: throw IllegalStateException("Problem must be set")

            return ThroughputHarness(
                warmupTime,
                maximumWarmupTime,
                warmupTimeUnit,
                warmupIterations,
                problemNumber,
                localProblem,
                measurementTime,
                measurementTimeUnit
            )
        }
    }
}