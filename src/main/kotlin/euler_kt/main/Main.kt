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
package euler_kt.main

import euler_kt.main.framework.*
import euler_kt.main.problems.*
import euler_kt.main.util.io.printIndented
import picocli.CommandLine
import java.util.concurrent.TimeUnit

fun <T> List<T>.getOrThrow(index: Int, e: Throwable): T {
    return this[index] ?: throw e
}

fun <K, V> Map<K, V>.getOrThrow(index: K, e: Throwable): V {
    return this[index] ?: throw e
}

fun main(args: Array<String>) {

    val problems: Map<Int, List<EulerProblem<*, *>>> = mapOf(
        Pair(1, listOf(Problem1())),
        Pair(2, listOf(Problem2())),
        Pair(3, listOf(Problem3a(), Problem3b(), Problem3c(), Problem3d())),
        Pair(4, listOf(Problem4())),
        Pair(5, listOf(Problem5())),
        Pair(6, listOf(Problem6a(), Problem6b())),
        Pair(7, listOf(Problem7a(), Problem7b())),
        Pair(8, listOf(Problem8a(), Problem8b(), Problem8c())),
    )
    val progArgs = parseArgs(
        args,
        { problem -> problem in problems},
        { problem, variant -> (problems[problem]?.size ?: 0) > variant }
    )

    val runBenchmark = createBenchmarkFunction(progArgs)

    if(progArgs.runAll()) {
        problems.forEach {(problemNumber, solutions) ->
            val problem = solutions.getOrThrow(0, AssertionError("Should not be possible"))
            val result = runBenchmark(problem, problemNumber)
            println(result)
        }
    } else if(progArgs.runType() == ProgArgs.RunType.LIST) {
        problems.forEach {(problemNumber, solutions) ->
            println("Problem $problemNumber:\n")
            printIndented(4, solutions[0].description())
            println()
            solutions.forEachIndexed { index, solution ->
                println("      Solution $index: ${solution.explain()}")
            }
            println()
        }
    } else {
        val problemNumber = progArgs.getProblem()
        val solutionVariant = progArgs.getVariant().toInt()
        val except = AssertionError("Should not be possible")

        val problem = problems.getOrThrow(problemNumber, except).getOrThrow(solutionVariant, except)
        val result = runBenchmark(problem, problemNumber)
        println(result)
    }
}

private fun createBenchmarkFunction(args: ProgArgs): (EulerProblem<*, *>, Int) -> ProblemResult {
    val runHarness: (EulerProblem<*, *>, Int) -> ProblemResult =
        if(args.runType() == ProgArgs.RunType.JMH_BENCHMARK) {
            { problem, problemNumber ->
                runJmhHarness(
                    problem,
                    problemNumber,
                    args.warmupTime(),
                    args.warmupIterations(),
                    args.iterations()
                )
            }
        } else if(args.runType() == ProgArgs.RunType.BENCHMARK) {
            { problem, problemNumber ->
                runBenchmark(
                    problem,
                    problemNumber,
                    args.warmupTime(),
                    args.maximumWarmupTime(),
                    args.warmupIterations(),
                    args.iterations()
                )
            }
        } else if(args.runType() == ProgArgs.RunType.THROUGHPUT) {
            { problem, problemNumber ->
                runThroughput(
                    problem,
                    problemNumber,
                    args.warmupTime(),
                    args.maximumWarmupTime(),
                    args.warmupIterations(),
                    args.measurementTime(),
                    TimeUnit.SECONDS
                )
            }
        }
        else {
            ::runInSimpleHarness
        }

    val runAndPrintDescription: (EulerProblem<*, *>, Int) -> ProblemResult =
        {
            problem, problemNumber ->
            if(args.printDescription()) {
                println(problem.description())
            }
            if(args.printExplanation()) {
                println(problem.explain())
            }
            runHarness(problem, problemNumber)
        }

    return runAndPrintDescription
}


private fun parseArgs(
    args: Array<String>, problemExists: (Int) -> Boolean, variantExists: (Int, Int) -> Boolean
): ProgArgs {
    val progArgs = ProgArgs(problemExists, variantExists)
    var cmd = CommandLine(progArgs)

    val exitCode = cmd.execute(*args)

    if(exitCode != 0) {
        System.exit(exitCode)
    }

    if(cmd.isUsageHelpRequested) {
        System.exit(0)
    }

    return progArgs
}
