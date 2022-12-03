
fun main() {
    fun getPriority(ch: Char): Long {
        if (ch in 'A'..'Z') {
            return ch - 'A' + 27L
        }

        return ch - 'a' + 1L
    }

    fun part1(input: List<String>): Long {
        return input.sumOf { line ->
            line
                .chunkedSequence(line.length / 2)
                .map { it.toSet() }
                .reduce { acc, chars -> acc.intersect(chars) }
                .sumOf {
                    getPriority(it)
                }
        }
    }

    fun part2(input: List<String>): Long {
        return input
            .chunked(3)
            .sumOf { group ->
                group
                    .map { it.toSet() }
                    .reduce { acc, chars -> acc.intersect(chars) }
                    .sumOf {
                        getPriority(it)
                    }
            }
    }

    check(getPriority('a') == 1L)
    check(getPriority('z') == 26L)
    check(getPriority('A') == 27L)
    check(getPriority('Z') == 52L)

    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157L)
    check(part2(testInput) == 70L)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
