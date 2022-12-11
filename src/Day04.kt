

fun main() {
    fun parse(lines: List<String>): List<Pair<Interval, Interval>> {
        return lines.map { line ->
            val intervals = line
                .split(',')
                .map { range ->
                    val rangeParts = range.split('-')
                    Interval(rangeParts[0].toInt(), rangeParts[1].toInt())
                }
            intervals[0]to intervals[1]
        }
    }

    fun part1(input: List<String>): Int {
        return parse(input)
            .filter { (left, right) -> left.hasFullOverlap(right) }
            .size
    }

    fun part2(input: List<String>): Int {
        return parse(input)
            .filter { (left, right) -> left.hasOverlap(right) }
            .size
    }

    val testInput = readInput("Day04_test")
    assert(part1(testInput), 2)
    assert(part2(testInput), 4)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
