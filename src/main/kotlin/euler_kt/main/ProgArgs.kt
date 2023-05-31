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
    version=["euler-kt 1.0.0"],
    description=["Solve Project Euler problems in Kotlin"]
)
class ProgArgs constructor(
    private val problemExists: (Int) -> Boolean,
    private val variantExists: (Int, Int) -> Boolean,
) : Callable<Int> {

    @Spec
    private lateinit var spec: CommandSpec

    @Option(names=["-p", "--problem"], description=["The problem number to solve"])
    private var problem: Int = 0

    @Option(names=["-d", "--description"], description=["Print description before solving"])
    private var description: Boolean = false

    @Option(names=["-e", "--explain"], description=["Print explanation of how chosen solver works, before solving"])
    private var explain: Boolean = false

    @Option(names=["--variant"], description=["Use the numbered solution variant instead of the default"])
    private var variant: UInt = 0u

    @Option(names=["-a", "--all"], description=["Solve all implemented problems"])
    private var all: Boolean = false

    @Option(
        names=["-w", "--warmup-time"],
        description=["Warmup time for benchmark in ms. Applies only to benchmark runs. Default is 1000ms."]
    )
    private var warmupTime: Long = 1000

    @Option(
        names=[ "--maximum-warmup-time"],
        description=[
            "Maximum warmup time for benchmark in ms. Applies only to benchmark runs (but not JMH). Default is" +
            " 3x the warmup time. if a single test run lasts longer than this time. Then, rather than running " +
            " measurement iterations, we will report the warmup time as the measurement time."
        ]
    )
    private var maximumWarmupTime: Long = 0

    @Option(
        names=["--warmup-iterations"],
        description=["Number of warmup iterations for benchmark. Applies only to benchmark runs. Default is 2."]
    )
    private var warmupIterations: Int = 2

    @Option(names=["-i", "--iterations"], description=[
        "Number of iterations for benchmark. Applies only to benchmark runs. Default is 10."
    ])
    private var iterations: Int = 10

    @Option(names=["--measurement-time"], description=[
        "How long to run the throughput benchmark in seconds. Applies only to throughput runs. Default is 10s."
    ])
    private var measurementTime: Long = 10

    @ArgGroup(exclusive = true, multiplicity = "0..1")
    private var runType: RunTypeOption = RunTypeOption(validate = true)

    internal open class RunTypeOption() {
        constructor(validate: Boolean = false, benchmark: Boolean = false) : this() {
            this.validate = validate;
            this.benchmark = benchmark;
        }

        @Option(names=["-t", "--validate"], description=["Validate the solution to the problem. This is the default"])
        private var validate: Boolean = false

        @Option(names=["-j", "--jmh-benchmark"], description=["Benchmark the solution using JMH"])
        private var jmh: Boolean = false

        @Option(names=["-b", "--benchmark"], description=["Benchmark using builtin framework"])
        private var benchmark: Boolean = false

        @Option(
            names=["-r", "--throughput"],
            description=[
                "Measure how many times problem can be solved per time period. Uses builtin benchmark framework"
            ]
        )
        private var throughput: Boolean = false

        fun runType(): RunType {
            if(validate) {
                return RunType.VALIDATE
            } else if(benchmark) {
                return RunType.BENCHMARK
            } else if(jmh) {
                return RunType.JMH_BENCHMARK
            } else if(throughput) {
                return RunType.THROUGHPUT
            } else {
                throw IllegalStateException("Invalid run type");
            }
        }
    }

    enum class RunType {
        VALIDATE, BENCHMARK, JMH_BENCHMARK, THROUGHPUT
    }

    fun measurementTime(): Long {
        return measurementTime;
    }

    fun printDescription(): Boolean {
        return description;
    }

    fun printExplanation(): Boolean {
        return explain
    }

    fun getProblem(): Int {
        return problem;
    }

    fun getVariant(): UInt {
        return variant
    }

    fun runType(): RunType {
        return runType.runType();
    }

    fun runAll(): Boolean {
        return all;
    }

    fun warmupTime(): Long {
        return warmupTime;
    }

    fun maximumWarmupTime(): Long {
        if(maximumWarmupTime <= 0) {
            return warmupTime * 3
        } else {
            return maximumWarmupTime
        }
    }

    fun warmupIterations(): Int {
        return warmupIterations
    }

    fun iterations(): Int {
        return iterations
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

        if(!problemExists(problem)) {
            throw ParameterException(
                spec.commandLine(), "Invalid value for option --problem. Problem $problem is not implemented."
            )
        }
        if(!variantExists(problem, variant.toInt())) {
            throw ParameterException(
                spec.commandLine(),
                "Invalid value for option --variant. Variant $variant does not exist for problem $problem."
            )
        }
    }
}