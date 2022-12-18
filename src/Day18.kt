
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class Day18 {
    data class Point(val x: Int, val y: Int, val z: Int) {
        fun isBetween(minPoint: Point, maxPoint: Point): Boolean {
            return (
                this.x in minPoint.x..maxPoint.x &&
                    this.y in minPoint.y..maxPoint.y &&
                    this.z in minPoint.z..maxPoint.z
                )
        }
    }

    val wildcard = Int.MAX_VALUE

    fun parse(input: List<String>): Set<Point> {
        return input.map { line ->
            val parts = line.split(',').map { it.toInt() }
            Point(parts[0], parts[1], parts[2])
        }.toSet()
    }

    fun getNeighbors(point: Point): Set<Point> {
        val range = listOf(-1, 1)
        val result = mutableSetOf<Point>()
        for (dx in range) {
            result.add(Point(point.x + dx, point.y, point.z))
        }
        for (dy in range) {
            result.add(Point(point.x, point.y + dy, point.z))
        }
        for (dz in range) {
            result.add(Point(point.x, point.y, point.z + dz))
        }

        return result
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

        val maxX = points.maxOf { p -> p.x }
        val maxY = points.maxOf { p -> p.y }
        val maxZ = points.maxOf { p -> p.z }

        val minX = points.minOf { p -> p.x }
        val minY = points.minOf { p -> p.y }
        val minZ = points.minOf { p -> p.z }

        val maxPoint = Point(maxX + 1, maxY + 1, maxZ + 1)
        val minPoint = Point(minX - 1, minY - 1, minZ - 1)

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
