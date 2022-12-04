class SectionRange(val start: Int, val end: Int)
class SectionPair(val left: SectionRange, val right: SectionRange) {
    fun hasFullOverlap(): Boolean {
        return hasFullOverlap(left, right) || hasFullOverlap(right, left)
    }

    fun hasOverlap(): Boolean {
        return hasOverlap(left, right) || hasOverlap(right, left)
    }

    private fun hasFullOverlap(x: SectionRange, y: SectionRange): Boolean {
        return x.start >= y.start && x.end <= y.end
    }

    private fun hasOverlap(x: SectionRange, y: SectionRange): Boolean {
        return x.start >= y.start && x.start <= y.end
    }
}

fun main() {
    fun parse(lines: List<String>): List<SectionPair> {
        return lines.map { line ->
            val ranges = line
                .split(',')
                .map { range ->
                    val rangeParts = range.split('-')
                    SectionRange(rangeParts[0].toInt(), rangeParts[1].toInt())
                }
            SectionPair(ranges[0], ranges[1])
        }
    }

    fun part1(input: List<String>): Int {
        return parse(input)
            .filter { it.hasFullOverlap() }
            .size
    }

    fun part2(input: List<String>): Int {
        return parse(input)
            .filter { it.hasOverlap() }
            .size
    }

    val testInput = readInput("Day04_test")
    assert(part1(testInput), 2)
    assert(part2(testInput), 4)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
