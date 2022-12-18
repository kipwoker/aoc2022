
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class Day18 {
    data class Point(val xs: Array<Int>) {
        fun isBetween(minPoint: Point, maxPoint: Point): Boolean {
            return xs
                .zip(minPoint.xs)
                .zip(maxPoint.xs)
                .all { p ->
                    val x = p.first.first
                    val min = p.first.second
                    val max = p.second

                    x in min..max
                }
        }

        fun add(index: Int, value: Int): Point {
            val new = xs.copyOf()
            new[index] += value
            return Point(new)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Point

            if (!xs.contentEquals(other.xs)) return false

            return true
        }

        override fun hashCode(): Int {
            return xs.contentHashCode()
        }
    }

    fun parse(input: List<String>): Set<Point> {
        return input.map { line ->
            val xs = line.split(',').map { it.toInt() }.toTypedArray()
            Point(xs)
        }.toSet()
    }

    fun getNeighbors(point: Point): Set<Point> {
        val range = listOf(-1, 1)

        return point.xs.flatMapIndexed { index, _ -> range.map { dx -> point.add(index, dx) } }.toSet()
    }

    fun part1(input: List<String>): String {
        val points = parse(input)
        var result = 0L
        for (point in points) {
            var coverage = 6
            val neighbors = getNeighbors(point)
            coverage -= neighbors.count { points.contains(it) }
            result += coverage
        }

        return result.toString()
    }

    fun part2(input: List<String>): String {
        val points = parse(input)
        var result = 0L

        val dimension = points.first().xs.size
        val max = (0 until dimension).map { d -> points.maxOf { p -> p.xs[d] } }
        val min = (0 until dimension).map { d -> points.minOf { p -> p.xs[d] } }

        val maxPoint = Point(max.map { it + 1 }.toTypedArray())
        val minPoint = Point(min.map { it - 1 }.toTypedArray())

        val visited = mutableSetOf<Point>()
        val q = ArrayDeque(listOf(minPoint))

        while (q.isNotEmpty()) {
            val point = q.removeFirst()

            val neighbors = getNeighbors(point).filter { n -> n.isBetween(minPoint, maxPoint) }
            for (neighbor in neighbors) {
                if (neighbor in points) {
                    ++result
                } else if (neighbor !in visited) {
                    visited.add(neighbor)
                    q.addLast(neighbor)
                }
            }
        }

        return result.toString()
    }
}

@OptIn(ExperimentalTime::class)
@Suppress("DuplicatedCode")
fun main() {
    val solution = Day18()
    val name = solution.javaClass.name

    fun test() {
        val expected1 = "64"
        val expected2 = "58"

        val testInput = readInput("${name}_test")
        println("Test part 1")
        assert(solution.part1(testInput), expected1)
        println("> Passed")
        println("Test part 2")
        assert(solution.part2(testInput), expected2)
        println("> Passed")
        println()
        println("=================================")
        println()
    }

    fun run() {
        val input = readInput(name)
        val elapsed1 = measureTime {
            println("Part 1: " + solution.part1(input))
        }
        println("Elapsed: $elapsed1")
        println()

        val elapsed2 = measureTime {
            println("Part 2: " + solution.part2(input))
        }
        println("Elapsed: $elapsed2")
        println()
    }

    test()
    run()
}
