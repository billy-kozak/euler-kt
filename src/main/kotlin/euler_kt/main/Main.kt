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

import euler_kt.main.framework.EulerProblem
import euler_kt.main.framework.ProblemResult
import euler_kt.main.framework.runInSimpleHarness
import euler_kt.main.framework.runJmhHarness
import euler_kt.main.problems.Problem1
import picocli.CommandLine
import java.lang.AssertionError

fun main(args: Array<String>) {

    val problems: Map<Int, EulerProblem<*>> = mapOf(
        Pair(1, Problem1())
    )
    val progArgs = parseArgs(args, problems.keys)

    val runBenchmark = createBenchmarkFunction(progArgs)

    if(progArgs.runAll()) {
        problems.forEach {(problemNumber, problem) ->
            val result = runBenchmark(problem, problemNumber)
            println(result)
        }
    } else {
        val problemNumber = progArgs.getProblem()

        when(val problem = problems[problemNumber]) {
            null -> throw AssertionError("Should not be possible")
            else -> {
                val result = runBenchmark(problem, problemNumber)
                println(result)
            }
        }
    }
}

private fun createBenchmarkFunction(args: ProgArgs): (EulerProblem<*>, Int) -> ProblemResult {
    val runHarness: (EulerProblem<*>, Int) -> ProblemResult =
        if(args.runType() == ProgArgs.RunType.BENCHMARK) {
            ::runJmhHarness
        } else {
            ::runInSimpleHarness
        }

    val runAndPrintDescription: (EulerProblem<*>, Int) -> ProblemResult =
        if(args.printDescription()) {
            { problem, problemNumber ->
                println(problem.description())
                runHarness(problem, problemNumber)
            }
        } else {
            runHarness
        }

    return runAndPrintDescription
}


private fun parseArgs(args: Array<String>, implementedProblems: Set<Int>): ProgArgs {
    val progArgs = ProgArgs(implementedProblems)
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
