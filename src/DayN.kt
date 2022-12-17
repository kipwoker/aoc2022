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

    fun test() {
        val expected1 = "not expect anything"
        val expected2 = "not expect anything"

        val testInput = readInput("${name}_test")
        println("Test part 1")
        assert(solution.part1(testInput), expected1)
        println("> Passed")
        println("Test part 2")
        assert(solution.part2(testInput), expected2)
        println("> Passed")
        println()
        println("=================================")
        println()
    }

    fun run() {
        val input = readInput(name)
        val elapsed1 = measureTime {
            println("Part 1: " + solution.part1(input))
        }
        println("Elapsed: $elapsed1")
        println()

        val elapsed2 = measureTime {
            println("Part 2: " + solution.part2(input))
        }
        println("Elapsed: $elapsed2")
        println()
    }

    test()
    run()
}
