import java.util.Stack

class CrateStack(val crates: Stack<Char>)
class Move(val quantity: Int, val startIdx: Int, val endIdx: Int)
class State(val stacks: Array<CrateStack>, val moves: Array<Move>) {
    fun applyMoves() {
        for (move in moves) {
            for (i in 1..move.quantity) {
                val crate = stacks[move.startIdx].crates.pop()
                stacks[move.endIdx].crates.push(crate)
            }
        }
    }

    fun applyMoves2() {
        for (move in moves) {
            val batch = mutableListOf<Char>()
            for (i in 1..move.quantity) {
                val crate = stacks[move.startIdx].crates.pop()
                batch.add(crate)
            }

            batch.asReversed().forEach { crate ->
                stacks[move.endIdx].crates.push(crate)
            }
        }
    }

    fun getTops(): String {
        return String(stacks.map { it.crates.peek() }.toCharArray())
    }
}

fun main() {
    fun parseStacks(input: List<String>): Array<CrateStack> {
        val idxs = mutableListOf<Int>()

        val idxInput = input.last()
        for ((index, item) in idxInput.toCharArray().withIndex()) {
            if (item != ' ') {
                idxs.add(index)
            }
        }

        val result = Array(idxs.size) { _ -> CrateStack(Stack<Char>()) }
        val cratesInput = input.asReversed().slice(1 until input.size)
        for (line in cratesInput) {
            for ((localIdx, idx) in idxs.withIndex()) {
                if (line.length > idx && line[idx] != ' ') {
                    result[localIdx].crates.push(line[idx])
                }
            }
        }

        return result
    }

    fun parseMoves(input: List<String>): Array<Move> {
        return input
            .map { line ->
                val parts = line.split(' ')
                Move(parts[1].toInt(), parts[3].toInt() - 1, parts[5].toInt() - 1)
            }.toTypedArray()
    }

    fun parse(input: List<String>): State {
        val separatorIdx = input.lastIndexOf("")
        val stacksInput = input.slice(0 until separatorIdx)
        val movesInput = input.slice(separatorIdx + 1 until input.size)

        return State(parseStacks(stacksInput), parseMoves(movesInput))
    }

    fun part1(input: List<String>): String {
        val state = parse(input)
        state.applyMoves()
        return state.getTops()
    }

    fun part2(input: List<String>): String {
        val state = parse(input)
        state.applyMoves2()
        return state.getTops()
    }

    val testInput = readInput("Day05_test")
    assert(part1(testInput), "CMZ")
    assert(part2(testInput), "MCD")

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}
