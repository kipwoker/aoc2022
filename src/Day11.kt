import kotlin.math.pow

data class Modifier(val op: Char, val value: Int)
data class Condition(val divisibleBy: Int, val ifTrue: Int, val ifFalse: Int)
data class Monkey(
    val index: Int,
    val queue: MutableList<Long>,
    val modifier: Modifier,
    val condition: Condition,
    var inspectionCount: Long
)

fun main() {
    fun parse(input: List<String>): List<Monkey> {
        val chunks = input.chunked(7)
        return chunks.map { it ->
            val index = it[0].toInt()
            val items = it[1].split(',').map { x -> x.toLong() }.toMutableList()
            val modifierParts = it[2].split(' ')
            val modifier = Modifier(modifierParts[0][0], modifierParts[1].toInt())
            val divisibleBy = it[3].toInt()
            val ifTrue = it[4].toInt()
            val ifFalse = it[5].toInt()
            val condition = Condition(divisibleBy, ifTrue, ifFalse)

            Monkey(index, items, modifier, condition, 0)
        }
    }

    fun applyModifier(value: Long, modifier: Modifier): Long {
        return when (modifier.op) {
            '^' -> value.toDouble().pow(modifier.value.toDouble()).toLong()
            '+' -> value + modifier.value
            '*' -> value * modifier.value
            else -> throw RuntimeException("Unknown modifier $modifier")
        }
    }

    fun applyCondition(value: Long, condition: Condition): Int {
        return if (value % condition.divisibleBy == 0L) condition.ifTrue else condition.ifFalse
    }

    fun printState(monkeys: List<Monkey>) {
        for (monkey in monkeys) {
            println("Monkey ${monkey.index}: ${monkey.queue}")
        }
    }

    fun printCounts(monkeys: List<Monkey>) {
        for (monkey in monkeys) {
            println("Monkey ${monkey.index}: ${monkey.inspectionCount}")
        }
    }

    fun play(monkeys: List<Monkey>, iterationsCount: Int, worryReducer: (v: Long) -> Long) {
        val overflowBreaker = monkeys.map { it.condition.divisibleBy }.reduce { acc, i -> acc * i }
        for (i in 1..iterationsCount) {
            for (monkey in monkeys) {
                for (item in monkey.queue) {
                    val worryLevel = applyModifier(item, monkey.modifier)
                    val newLevel = worryReducer(worryLevel) % overflowBreaker
                    val newIndex = applyCondition(newLevel, monkey.condition)
                    monkeys[newIndex].queue.add(newLevel)
                    ++monkey.inspectionCount
                }
                monkey.queue.clear()
            }

            if (i % 20 == 0){
                println("Iteration $i")
                printCounts(monkeys)
                println()
            }
        }
    }

    fun calculateResult(monkeys: List<Monkey>): Long {
        val ordered = monkeys.sortedBy { -it.inspectionCount }
        return ordered[0].inspectionCount * ordered[1].inspectionCount
    }

    fun part1(input: List<String>): Long {
        val monkeys = parse(input)
        play(monkeys, 20) { x -> x / 3 }
        return calculateResult(monkeys)
    }

    fun part2(input: List<String>): Long {
        val monkeys = parse(input)
        play(monkeys, 10000) { x -> x }
        return calculateResult(monkeys)
    }

    val testInput = readInput("Day11_test")
    assert(part1(testInput), 10605L)
    assert(part2(testInput), 2713310158L)

    val input = readInput("Day11")
    println(part1(input))
    println(part2(input))
}
