class Operation(val cycles: Int, val add: Int)

fun main() {
    fun parse(input: List<String>): List<Operation> {
        return input.map { line ->
            if (line == "noop") {
                Operation(1, 0)
            } else {
                val modifier = line.split(' ')[1].toInt()
                Operation(2, modifier)
            }
        }
    }

    fun part1(input: List<String>): Long {
        val ops = parse(input)

        var x = 1
        var result = 0L
        val maxIndex = 220
        var seekIndex = 20
        val hop = 40
        var cycleCounter = 0
        for (op in ops) {
            for (cycle in 1..op.cycles) {
                ++cycleCounter
                if (cycleCounter == seekIndex) {
                    result += x * seekIndex
                    if (seekIndex == maxIndex) {
                        return result
                    }
                    seekIndex += hop
                }
            }
            x += op.add
        }

        return result
    }

    fun isSprite(cycleCounter: Int, x: Int, lineIndex: Int, hop: Int): Boolean {
        val lineValue = cycleCounter - (hop * lineIndex)
        return lineValue >= x - 1 && lineValue <= x + 1
    }

    fun part2(input: List<String>): Long {
        val ops = parse(input)

        var x = 1
        var lineIndex = 0
        val maxIndex = 240
        var seekIndex = 40
        val hop = 40
        var cycleCounter = 0
        for (op in ops) {
            for (cycle in 1..op.cycles) {
                if (isSprite(cycleCounter, x, lineIndex, hop)) {
                    print("#")
                } else {
                    print(".")
                }

                ++cycleCounter
                if (cycleCounter == seekIndex) {
                    println()

                    if (seekIndex == maxIndex) {
                        return 0
                    }
                    seekIndex += hop
                    ++lineIndex
                }
            }
            x += op.add
        }

        return 0
    }

    val testInput = readInput("Day10_test")
    assert(part1(testInput), 13140L)

    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}
