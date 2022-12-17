@Suppress("DuplicatedCode")
fun main() {
    fun part1(input: List<String>): Long {
        return 0
    }

    fun part2(input: List<String>): Long {
        return 0
    }

// ==================================================================== //

    val day = "N"

    fun test() {
        val testInput = readInput("Day${day}_test")
        assert(part1(testInput), 0L)
        assert(part2(testInput), 0L)
    }

    fun run() {
        val input = readInput("Day$day")
        println(part1(input))
        println(part2(input))
    }

    test()
    run()
}
