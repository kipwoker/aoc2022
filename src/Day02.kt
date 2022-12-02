enum class Figure(val score: Int) {
    Rock(1),
    Paper(2),
    Scissors(3)
}

fun main() {
    fun createFigure(value: String): Figure {
        return when (value) {
            "A" -> Figure.Rock
            "B" -> Figure.Paper
            "C" -> Figure.Scissors
            "X" -> Figure.Rock
            "Y" -> Figure.Paper
            "Z" -> Figure.Scissors
            else -> throw Exception("Value unsupported $value")
        }
    }

    fun compare(f1: Figure, f2: Figure): Int {
        if (f1 == f2) {
            return 3
        }

        if (
            f1 == Figure.Paper && f2 == Figure.Scissors ||
            f1 == Figure.Rock && f2 == Figure.Paper ||
            f1 == Figure.Scissors && f2 == Figure.Rock
        ) {
            return 6
        }

        return 0
    }

    fun readTurns(lines: List<String>): List<Pair<Figure, Figure>> {
        return lines.map {
            val parts = it.split(' ')
            Pair(createFigure(parts[0]), createFigure(parts[1]))
        }
    }

    fun createFigure2(first: Figure, value: String): Figure {
        if (value == "Y") {
            return first
        }

        if (value == "X") {
            return when (first) {
                Figure.Rock -> Figure.Scissors
                Figure.Paper -> Figure.Rock
                Figure.Scissors -> Figure.Paper
            }
        }

        return when (first) {
            Figure.Rock -> Figure.Paper
            Figure.Paper -> Figure.Scissors
            Figure.Scissors -> Figure.Rock
        }
    }

    fun readTurns2(lines: List<String>): List<Pair<Figure, Figure>> {
        return lines.map {
            val parts = it.split(' ')
            val first = createFigure(parts[0])
            Pair(first, createFigure2(first, parts[1]))
        }
    }

    fun play(turns: List<Pair<Figure, Figure>>): Long {
        var result = 0L
        turns.map {
            compare(it.first, it.second) + it.second.score
        }.forEach {
            result += it
        }

        return result
    }

    fun part1(input: List<String>): Long {
        return play(readTurns(input))
    }

    fun part2(input: List<String>): Long {
        return play(readTurns2(input))
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 15L)
    check(part2(testInput) == 12L)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
