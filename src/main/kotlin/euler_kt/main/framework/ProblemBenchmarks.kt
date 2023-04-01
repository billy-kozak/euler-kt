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

import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.Options
import org.openjdk.jmh.runner.options.OptionsBuilder
import org.openjdk.jmh.runner.options.TimeValue

import java.util.concurrent.TimeUnit
import kotlin.jvm.optionals.getOrElse


fun runInSimpleHarness(problem: EulerProblem<*, *>, problemNumber: Int): ValidationResult {
    val t0 = System.nanoTime();
    val result = problem.run()
    val t1 = System.nanoTime()

    val valid = problem.validate(result)

    // return problem result
    return ValidationResult(problemNumber, (t1 - t0).toDouble() / 1000000, result, valid)
}

fun runJmhHarness(
        problem: EulerProblem<*, *>,
        problemNumber: Int,
        warmupTimeMs: Long = 1000,
        warmupIterations: Int = 2,
        measurementIterations: Int = 2
): BenchmarkResult {

    val opt: Options = OptionsBuilder()
        .include(problem.javaClass.simpleName + ".*")
        .mode(Mode.AverageTime)
        .timeUnit(TimeUnit.MILLISECONDS)
        .warmupTime(TimeValue.milliseconds(warmupTimeMs))
        .warmupIterations(warmupIterations)
        .measurementIterations(measurementIterations)
        .measurementTime(TimeValue.seconds(1))
        .threads(2)
        .forks(1)
        .shouldFailOnError(true)
        .shouldDoGC(true)
        .output("/dev/null")
        .build()

    val result = Runner(opt).run()
    val time = result.stream().map{it.aggregatedResult.primaryResult.score}.findFirst().getOrElse{Double.NaN}

    for(run in result) {
        run.secondaryResults.forEach({ println(it) })
    }

    return BenchmarkResult(problemNumber, time)
}

fun runBenchmark(
    problem: EulerProblem<*, *>,
    problemNumber: Int,
    warmupTimeMs: Long = 1000,
    maximumWarmupTimeMs: Long = 30000,
    warmupIterations: Int = 2,
    measurementIterations: Int = 2
): ProblemResult {

    val builder: AverageTimeHarness.Builder = AverageTimeHarness.Builder()

    builder.measurementIterations(measurementIterations)

    val harness: AverageTimeHarness = AverageTimeHarness.Builder()
        .measurementIterations(measurementIterations)
        .warmupTime(warmupTimeMs)
        .maximumWarmupTime(maximumWarmupTimeMs)
        .warmupIterations(warmupIterations)
        .warmupTimeUnit(TimeUnit.MILLISECONDS)
        .problem(problemNumber, problem)
        .build()

    return harness.benchmark()
}

fun runThroughput(
    problem: EulerProblem<*, *>,
    problemNumber: Int,
    warmupTimeMs: Long,
    maximumWarmupTimeMs: Long,
    warmupIterations: Int,
    measurementTime: Long,
    measurementTimeUnit: TimeUnit
): ProblemResult {

    val harness = ThroughputHarness.Builder()
        .warmupTime(warmupTimeMs)
        .maximumWarmupTime(maximumWarmupTimeMs)
        .warmupIterations(warmupIterations)
        .warmupTimeUnit(TimeUnit.MILLISECONDS)
        .problem(problemNumber, problem)
        .measurementTime(measurementTime, measurementTimeUnit)
        .build()

    return harness.benchmark()
}