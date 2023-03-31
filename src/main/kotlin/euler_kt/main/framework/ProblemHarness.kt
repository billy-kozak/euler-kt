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

class ProblemHarness
private constructor (
    private val warmupTime: Long,
    private val maximumWarmupTime: Long,
    private val warmupTimeUnit:  TimeUnit,
    private val warmupIterations: Int,
    private val measurementIterations: Int,
    private val problemNumber: Int,
    private val problem: EulerProblem<*, *>
) {

    @Volatile private var keyParameter: Number;

    init{
        keyParameter = problem.defaultKeyParam
    }

    fun benchmark(): ProblemResult {

        val warmupMeasurement = warmup()

        if(warmupMeasurement.getRunTime() >= warmupTimeUnit.toMicros(maximumWarmupTime)) {
            return ValidationResult(
                problemNumber,
                warmupMeasurement.averageRunTime() / 1000.0,
                warmupMeasurement.getAnswer(),
                warmupMeasurement.getValid(),
                warmupMeasurement.getRuns()
            )
        }

        val measurement = Measurement()

        for(i in 0 until measurementIterations) {
            val r = measure()
            measurement.add(r.second, r.first)
        }

        return ValidationResult(
            problemNumber,
            measurement.averageRunTime() / 1000.0,
            measurement.getAnswer(),
            measurement.getValid(),
            measurement.getRuns()
        )
    }

    private fun warmup(): Measurement = runBlocking {
        val ret = Measurement()
        val t0  = monoTimeMicros()

        for(i in 0 until warmupIterations) {
            val job = launch {
                while ((monoTimeMicros() - t0) < warmupTimeUnit.toMicros(maximumWarmupTime)) {
                    val r = measure()
                    ret.add(r.second, r.first)
                    yield()
                }
            }
            delay(warmupTimeUnit.toMillis(warmupTime))
            job.cancelAndJoin()

            if ((monoTimeMicros() - t0) < warmupTimeUnit.toMicros(maximumWarmupTime)) {
                break
            }
        }
        return@runBlocking ret
    }

    private fun measure(): Pair<Number, Long>  {
        val t0 = monoTimeMicros()
        val result = problem.run()
        val t1 = monoTimeMicros()

        return Pair(result, t1 - t0)
    }

    private inner class Measurement {
        private var runTime: Long = 0
        private var valid: Boolean = true
        private var runs: Int = 0
        private var answer: Number? = null

        fun add(runTime: Long, answer: Number) {
            this.runTime += runTime
            this.runs += 1

            this.valid = this.valid && problem.validate(answer)
            if(this.answer == null || !this.valid) {
                this.answer = answer
            }
        }

        fun averageRunTime(): Long {
            return runTime / runs
        }

        fun getRunTime(): Long = runTime
        fun getValid(): Boolean = valid
        fun getRuns(): Int = runs
        fun getAnswer(): Number = answer ?: throw IllegalStateException("Answer not set")
    }

    class Builder {
        private var warmupTime: Long = 1
        private var maximumWarmupTime: Long = 10
        private var warmupTimeUnit:  TimeUnit = TimeUnit.SECONDS
        private var warmupIterations: Int = 10
        private var measurementIterations: Int = 10
        private var problem: EulerProblem<*,*>? = null
        private var problemNumber: Int = 0

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
        fun measurementIterations(measurementIterations: Int) = apply {
            this.measurementIterations = measurementIterations
        }
        fun problem(problemNumber: Int, problem: EulerProblem<*,*>) = apply {
            this.problemNumber = problemNumber
            this.problem = problem
        }

        fun build(): ProblemHarness {
            val localProblem = problem ?: throw IllegalStateException("Problem must be set")
            return ProblemHarness(
                warmupTime,
                maximumWarmupTime,
                warmupTimeUnit,
                warmupIterations,
                measurementIterations,
                problemNumber,
                localProblem
            )
        }
    }
}