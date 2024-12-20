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
        Pair(7, listOf(Problem7a(), Problem7b(), Problem7c())),
        Pair(8, listOf(Problem8a(), Problem8b(), Problem8c())),
        Pair(9, listOf(Problem9())),
        Pair(10, listOf(Problem10())),
        Pair(11, listOf(Problem11a(),Problem11b(), Problem11c())),
        Pair(12, listOf(Problem12a(), Problem12b(), Problem12c())),
        Pair(13, listOf(Problem13a(), Problem13b(), Problem13c())),
        Pair(14, listOf(Problem14a(), Problem14b())),
        Pair(15, listOf(Problem15a(), Problem15b())),
        Pair(16, listOf(Problem16())),
        Pair(17, listOf(Problem17a(), Problem17b())),
        Pair(18, listOf(Problem18())),
        Pair(19, listOf(Problem19())),
        Pair(20, listOf(Problem20())),
        Pair(21, listOf(Problem21())),
        Pair(22, listOf(Problem22a(), Problem22b())),
        Pair(23, listOf(Problem23a(), Problem23b(), Problem23c())),
        Pair(24, listOf(Problem24())),
        Pair(25, listOf(Problem25a(), Problem25b())),
        Pair(26, listOf(Problem26a(), Problem26b())),
        Pair(27, listOf(Problem27a(), Problem27b())),
        Pair(28, listOf(Problem28a(), Problem28b())),
        Pair(29, listOf(Problem29())),
        Pair(30, listOf(Problem30())),
        Pair(31, listOf(Problem31a(), Problem31b())),
        Pair(32, listOf(Problem32())),
        Pair(33, listOf(Problem33()))
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
