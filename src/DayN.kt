import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class DayN {
    fun part1(input: List<String>): Long {
        return 0
    }

    fun part2(input: List<String>): Long {
        return 0
    }
}

@OptIn(ExperimentalTime::class)
@Suppress("DuplicatedCode")
fun main() {
    val solution = DayN()
    val name = solution.javaClass.name

    fun test() {
        val testInput = readInput("${name}_test")
        assert(solution.part1(testInput), 0L)
        assert(solution.part2(testInput), 0L)
    }

    fun run() {
        val input = readInput(name)
        val elapsed1 = measureTime {
            println("Part1: " + solution.part1(input))
        }
        println("Elapsed: $elapsed1")
        println()

        val elapsed2 = measureTime {
            println("Part2: " + solution.part2(input))
        }
        println("Elapsed: $elapsed2")
        println()
    }

    test()
    run()
}
