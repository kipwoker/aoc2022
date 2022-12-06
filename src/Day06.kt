
fun main() {
    fun getDiffIndex(input: List<String>, diffCount: Int): Int {
        val line = input[0].toCharArray()

        for (i in 0..line.size - diffCount) {
            if (line.slice(i until i + diffCount).toSet().size == diffCount) {
                return i + diffCount
            }
        }

        return -1
    }

    fun part1(input: List<String>): Int {
        return getDiffIndex(input, 4)
    }

    fun part2(input: List<String>): Int {
        return getDiffIndex(input, 14)
    }

    assert(part1(readInput("Day06_test_1")), 7)
    assert(part1(readInput("Day06_test_2")), 5)
    assert(part1(readInput("Day06_test_3")), 11)

    assert(part2(readInput("Day06_test_1")), 19)
    assert(part2(readInput("Day06_test_2")), 23)
    assert(part2(readInput("Day06_test_3")), 26)

    val input = readInput("Day06")
    println(part1(input))
    println(part2(input))
}
