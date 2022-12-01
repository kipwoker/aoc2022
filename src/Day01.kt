fun main() {
    fun getStructure(input: List<String>): MutableList<List<Int>> {
        val result = mutableListOf<List<Int>>()
        var bucket = mutableListOf<Int>()
        result.add(bucket)

        input.forEach { line ->
            run {
                if (line.isBlank()) {
                    bucket = mutableListOf()
                    result.add(bucket)
                } else {
                    bucket.add(line.toInt())
                }
            }
        }
        return result
    }

    fun part1(input: List<String>): Int {
        val structure = getStructure(input)

        return structure.maxOf { it.sum() }
    }

    fun part2(input: List<String>): Int {
        val structure = getStructure(input)

        return structure
            .map { it.sum() }
            .sortedByDescending { it }
            .take(3)
            .sumOf { it }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 24000)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
