import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class Day20 {
    data class Cell(val value: Long, val order: Int)

    fun parse(input: List<String>): List<Long> {
        return input.map { it.toLong() }
    }

    private fun mix(list: List<Cell>): List<Cell> {
        val result = list.toMutableList()
        val n = list.size
        for (i in 1..n) {
            val (index, cell) = getByOrder(result, i) ?: break
            if (cell.value == 0L) {
                continue
            }
            var shift = (cell.value + index) % (n - 1)
            if (shift < 0) {
                shift += n - 1
            }

            result.removeAt(index)
            result.add(shift.toInt(), cell)
        }

        return result
    }

    private fun find(index: Int, list: List<Cell>): Long {
        return list[index % list.size].value
    }

    private fun getByOrder(list: List<Cell>, order: Int): Pair<Int, Cell>? {
        for ((index, el) in list.withIndex()) {
            if (el.order == order) {
                return index to el
            }
        }

        return null
    }

    private fun calc(mixed: List<Cell>): Long {
        val zeroIndex = mixed.indexOfFirst { it.value == 0L }
        val r1000 = find(zeroIndex + 1000, mixed)
        println("1000: $r1000")
        val r2000 = find(zeroIndex + 2000, mixed)
        println("2000: $r2000")
        val r3000 = find(zeroIndex + 3000, mixed)
        println("3000: $r3000")

        return r1000 + r2000 + r3000
    }

    fun part1(input: List<String>): String {
        val list = parse(input).mapIndexed { index, value -> Cell(value, index + 1) }
        val mixed = mix(list)
        val result = calc(mixed)
        return result.toString()
    }

    fun part2(input: List<String>): String {
        val list = parse(input)
        var mixed = list.mapIndexed { index, value -> Cell(value * 811589153, index + 1) }
        for (i in 1..10) {
            mixed = mix(mixed)
        }

        val result = calc(mixed)
        return result.toString()
    }
}

@OptIn(ExperimentalTime::class)
@Suppress("DuplicatedCode")
fun main() {
    val solution = Day20()
    val name = solution.javaClass.name

    val execution = setOf(
        ExecutionMode.Test1,
        ExecutionMode.Test2,
        ExecutionMode.Exec1,
        ExecutionMode.Exec2
    )

    fun test() {
        val expected1 = "3"
        val expected2 = "1623178306"

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
