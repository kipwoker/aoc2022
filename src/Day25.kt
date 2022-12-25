import kotlin.math.pow
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class Day25 {
    fun convertFromSnafuNumber(input: String): Long {
        var result = 0L
        val n = input.length
        for ((index, c) in input.withIndex()) {
            val number = when (c) {
                '2' -> 2
                '1' -> 1
                '=' -> -2
                '-' -> -1
                '0' -> 0
                else -> throw RuntimeException("Unexpected $c")
            }
            val multiplier = 5.0.pow(n - 1.0 - index).toLong()
            result += multiplier * number
        }

        return result
    }

    fun convertToSnafuNumber(input: Long): String {
        val result = ArrayDeque<Char>()

        var overflow = 0
        var cursor = input
        while (cursor > 0) {
            var digit = cursor % 5 + overflow
            overflow = 0
            cursor /= 5
            if (digit > 2) {
                overflow = 1
                digit -= 5
            }

            val symbol = when (digit) {
                -2L -> '='
                -1L -> '-'
                0L -> '0'
                1L -> '1'
                2L -> '2'
                else -> throw RuntimeException("Unexpected $digit")
            }

            result.addFirst(symbol)
        }

        return result.joinToString(separator = "")
    }

    fun part1(input: List<String>): String {
        val total = input.sumOf { convertFromSnafuNumber(it) }
        return convertToSnafuNumber(total)
    }

    fun part2(input: List<String>): String {
        return "not implemented"
    }
}

@OptIn(ExperimentalTime::class)
@Suppress("DuplicatedCode")
fun main() {
    val solution = Day25()
    val name = solution.javaClass.name

    val execution = setOf(
        ExecutionMode.Test1,
//        ExecutionMode.Test2,
        ExecutionMode.Exec1
//        ExecutionMode.Exec2
    )

    fun test() {
        val expected1 = "2=-1=0"
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
