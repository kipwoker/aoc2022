import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

enum class MathOperator {
    Plus,
    Minus,
    Multiply,
    Divide
}

class Day21 {
    data class Monkey(val name: String, var value: Long?, val left: String?, val right: String?, val op: MathOperator?)

    fun parseOperator(value: String): MathOperator {
        return when (value) {
            "*" -> MathOperator.Multiply
            "+" -> MathOperator.Plus
            "-" -> MathOperator.Minus
            "/" -> MathOperator.Divide
            else -> throw RuntimeException("Unknown op $value")
        }
    }

    fun parse(input: List<String>): List<Monkey> {
        return input.map { line ->
            val parts = line.split(' ')
            if (parts.size == 4) {
                Monkey(parts[0], null, parts[1], parts[3], parseOperator(parts[2]))
            } else {
                Monkey(parts[0], parts[1].toLong(), null, null, null)
            }
        }
    }

    fun calc1(name: String, map: Map<String, Monkey>): Long {
        val monkey = map.getValue(name)
        if (monkey.value != null) {
            return monkey.value!!
        }

        val left = calc1(monkey.left!!, map)
        val right = calc1(monkey.right!!, map)
        val result = when (monkey.op!!) {
            MathOperator.Plus -> left + right
            MathOperator.Minus -> left - right
            MathOperator.Multiply -> left * right
            MathOperator.Divide -> left / right
        }

        monkey.value = result

        return result
    }

    fun calc2(name: String, map: Map<String, Monkey>): Long {
        val monkey = map.getValue(name)
        if (monkey.value != null) {
            return monkey.value!!
        }

        val left = calc2(monkey.left!!, map)
        val right = calc2(monkey.right!!, map)
        if (monkey.name == "root") {
            return right - left
        }

        val result = when (monkey.op!!) {
            MathOperator.Plus -> left + right
            MathOperator.Minus -> left - right
            MathOperator.Multiply -> left * right
            MathOperator.Divide -> left / right
        }

        monkey.value = result

        return result
    }

    fun part1(input: List<String>): String {
        val monkeys = parse(input)
        val monkeyMap = monkeys.associateBy { it.name }
        val result = calc1("root", monkeyMap)

        return result.toString()
    }

    fun part2(input: List<String>): String {
        var r = 5000000000000L
        var l = 0L
        while (r - l > 1) {
            val h = (r + l) / 2
            val monkeys = parse(input)
            val monkeyMap = monkeys.associateBy { it.name }
            monkeyMap["humn"]!!.value = h
            val result = calc2("root", monkeyMap)
            if (result != 0L) {
                if (result < 0) {
                    l = h
                } else {
                    r = h
                }

                continue
            }

            return h.toString()
        }

        return "Not Found"
    }
}

@OptIn(ExperimentalTime::class)
@Suppress("DuplicatedCode")
fun main() {
    val solution = Day21()
    val name = solution.javaClass.name

    val execution = setOf(
        ExecutionMode.Test1,
        ExecutionMode.Test2,
        ExecutionMode.Exec1,
        ExecutionMode.Exec2
    )

    fun test() {
        val expected1 = "152"
        val expected2 = "301"

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
