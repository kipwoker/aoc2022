import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class DayN {
    fun part1(input: List<String>): String {
        return "not implemented"
    }

    fun part2(input: List<String>): String {
        return "not implemented"
    }
}

@OptIn(ExperimentalTime::class)
@Suppress("DuplicatedCode")
fun main() {
    val solution = DayN()
    val name = solution.javaClass.name

    val execution = setOf(
        ExecutionMode.Test1,
        ExecutionMode.Test2,
        ExecutionMode.Exec1,
        ExecutionMode.Exec2
    )

    fun test() {
        val expected1 = "not expect anything"
        val expected2 = "not expect anything"

        val testInput = readInput("${name}_test")
        if (execution.contains(ExecutionMode.Test1)) {
            println("Test part 1")
            assert(solution.part1(testInput), expected1)
            println("> Passed")
        }
        if (execution.contains(ExecutionMode.Test2)) {
            println("Test part 2")
            assert(solution.part2(testInput), expected2)
            println("> Passed")
            println()
        }
        println("=================================")
        println()
    }

    fun run() {
        val input = readInput(name)
        if (execution.contains(ExecutionMode.Exec1)) {
            val elapsed1 = measureTime {
                println("Part 1: " + solution.part1(input))
            }
            println("Elapsed: $elapsed1")
            println()
        }

        if (execution.contains(ExecutionMode.Exec2)) {
            val elapsed2 = measureTime {
                println("Part 2: " + solution.part2(input))
            }
            println("Elapsed: $elapsed2")
            println()
        }
    }

    test()
    run()
}
