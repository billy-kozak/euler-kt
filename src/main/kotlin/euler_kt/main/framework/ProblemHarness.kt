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

abstract class ProblemHarness
protected constructor (
    protected val warmupTime: Long,
    protected val maximumWarmupTime: Long,
    protected val warmupTimeUnit:  TimeUnit,
    protected val warmupIterations: Int,
    protected val problemNumber: Int,
    protected val problem: EulerProblem<*, *>
) {

    @Volatile protected var keyParameter: Number = problem.defaultKeyParam;

    abstract fun benchmark(): ProblemResult

    protected fun warmup(): Measurement = runBlocking {
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

    protected fun measure(): Pair<Number, Long>  {
        val t0 = monoTimeMicros()
        val result = problem.run()
        val t1 = monoTimeMicros()

        return Pair(result, t1 - t0)
    }

    inner class Measurement {
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
}