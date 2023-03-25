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

import picocli.CommandLine.*
import picocli.CommandLine.Model.CommandSpec
import java.util.concurrent.Callable


@Command(
    name="euler-kt",
    mixinStandardHelpOptions=true,
    version=["euler-kt 0.1.0"],
    description=["Solve Project Euler problems in Kotlin"]
)
class ProgArgs constructor(private val implementedProblems: Set<Int>) : Callable<Int> {

    @Spec
    private lateinit var spec: CommandSpec

    @Option(names=["-p", "--problem"], description=["The problem number to solve"])
    private var problem: Int = 0

    @Option(names=["-d", "--description"], description=["Print description before solving"])
    private var description: Boolean = false

    @Option(names=["-a", "--all"], description=["Solve all implemented problems"])
    private var all: Boolean = false

    @ArgGroup(exclusive = true, multiplicity = "0..1")
    private var runType: RunTypeOption = RunTypeOption(validate = true)

    internal open class RunTypeOption() {
        constructor(validate: Boolean = false, benchmark: Boolean = false) : this() {
            this.validate = validate;
            this.benchmark = benchmark;
        }

        @Option(names=["-t", "--validate"], description=["Validate the solution to the problem. This is the default"])
        private var validate: Boolean = false

        @Option(names=["-b", "--benchmark"], description=["Benchmark the solution to the problem"])
        private var benchmark: Boolean = false

        fun runType(): RunType {
            if(validate) {
                return RunType.VALIDATE;
            } else if(benchmark) {
                return RunType.BENCHMARK;
            } else {
                throw IllegalStateException("Invalid run type");
            }
        }
    }

    enum class RunType {
        VALIDATE, BENCHMARK
    }

    fun printDescription(): Boolean {
        return description;
    }

    fun getProblem(): Int {
        return problem;
    }

    fun runType(): RunType {
        return runType.runType();
    }

    fun runAll(): Boolean {
        return all;
    }

    override fun call(): Int {
        validate();

        if(!all && problem == 0) {
            System.err.println("No problem specified. Use --help for usage information.")
            return 255;
        }
        return 0;
    }

    private fun validate() {
        if(problem == 0) {
            return;
        }

        if(problem < 0) {
            throw ParameterException(
                spec.commandLine(), "Invalid value for option --problem. Problem values must be positive numbers."
            )
        }

        if(!implementedProblems.contains(problem)) {
            throw ParameterException(
                spec.commandLine(), "Invalid value for option --problem. Problem $problem is not implemented."
            )
        }
    }
}