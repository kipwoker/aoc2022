import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class Day23 {
    fun parse(input: List<String>): Set<Point> {
        return input.flatMapIndexed { y, line ->
            line.mapIndexed { x, c ->
                if (c == '#') {
                    Point(x, y)
                } else {
                    null
                }
            }
        }.filterNotNull().toSet()
    }

    private val directions = arrayOf(Direction.Up, Direction.Down, Direction.Left, Direction.Right)

    fun getNext(direction: Direction): Direction {
        val index = (directions.indexOf(direction) + 1 + directions.size) % directions.size
        return directions[index]
    }

    fun getNeighbors(point: Point): List<Point> {
        return (-1..1)
            .flatMap { x ->
                (-1..1)
                    .map { y -> Point(point.x + x, point.y + y) }
            }
            .filter { p -> p != point }
    }

    fun canMove(point: Point, neighbors: List<Point>, direction: Direction): Point? {
        return when (direction) {
            Direction.Up -> if (neighbors.none { p -> p.y == point.y - 1 }) Point(point.x, point.y - 1) else null
            Direction.Down -> if (neighbors.none { p -> p.y == point.y + 1 }) Point(point.x, point.y + 1) else null
            Direction.Left -> if (neighbors.none { p -> p.x == point.x - 1 }) Point(point.x - 1, point.y) else null
            Direction.Right -> if (neighbors.none { p -> p.x == point.x + 1 }) Point(point.x + 1, point.y) else null
        }
    }

    fun move(points: Set<Point>, direction: Direction): Set<Point> {
        val proposals = mutableMapOf<Point, Point>()
        for (point in points) {
            val neighbors = getNeighbors(point).filter { it in points }
            if (neighbors.isEmpty()) {
                continue
            }

            var cursor = direction
            while (true) {
                val target = canMove(point, neighbors, cursor)
                if (target != null) {
                    proposals[point] = target
                    break
                }
                cursor = getNext(cursor)
                if (cursor == direction) {
                    break
                }
            }
        }

        val reverseMap = proposals.asIterable().groupBy({ pair -> pair.value }, { pair -> pair.key })
        val moves = reverseMap.filter { it.value.size == 1 }.map { it.value[0] to it.key }.toMap()

        return points
            .filter { p -> !moves.containsKey(p) }
            .union(moves.values)
            .toSet()
    }

    fun play1(points: Set<Point>): Set<Point> {
        var direction = Direction.Up
        var cursor = points

        for (i in 1..10) {
            cursor = move(cursor, direction)
            direction = getNext(direction)
        }

        return cursor
    }

    fun play2(points: Set<Point>): Int {
        var direction = Direction.Up
        var cursor = points

        var i = 0
        do {
            val newCursor = move(cursor, direction)
            direction = getNext(direction)
            val same = newCursor == cursor
            cursor = newCursor
            ++i
        } while (!same)

        return i
    }

    fun print(points: Set<Point>) {
        val maxX = points.maxOf { p -> p.x }
        val maxY = points.maxOf { p -> p.y }
        val minX = points.minOf { p -> p.x }
        val minY = points.minOf { p -> p.y }

        for (y in (minY - 1)..(maxY + 1)) {
            for (x in (minX - 1)..(maxX + 1)) {
                if (Point(x, y) in points) {
                    print('#')
                } else {
                    print('.')
                }
            }
            println()
        }
        println()
    }

    fun part1(input: List<String>): String {
        val points = parse(input)
        val result = play1(points)
        return calc(result)
    }

    private fun calc(result: Set<Point>): String {
        val maxX = result.maxOf { p -> p.x }
        val maxY = result.maxOf { p -> p.y }
        val minX = result.minOf { p -> p.x }
        val minY = result.minOf { p -> p.y }

        val dx = maxX - minX + 1
        val dy = maxY - minY + 1

        println("dx $dx dy $dy size ${result.size}")
        val answer = dx * dy - result.size
        return answer.toString()
    }

    fun part2(input: List<String>): String {
        val points = parse(input)
        val result = play2(points)
        return result.toString()
    }
}

@OptIn(ExperimentalTime::class)
@Suppress("DuplicatedCode")
fun main() {
    val solution = Day23()
    val name = solution.javaClass.name

    val execution = setOf(
        ExecutionMode.Test1,
        ExecutionMode.Test2,
        ExecutionMode.Exec1,
        ExecutionMode.Exec2
    )

    fun test() {
        val expected1 = "110"
        val expected2 = "20"

        val testInput = readInput("${name}_test")
        if (execution.contains(ExecutionMode.Test1)) {
            println("Test part 1")
            assert(solution.part1(testInput), expected1)
            println("> Passed")
        }
        if (execution.contains(ExecutionMode.Test2)) {
            println("Test part 2")
            assert(solution.part2(testInput), expected2)
            println("> Passed")
            println()
        }
        println("=================================")
        println()
    }

    fun run() {
        val input = readInput(name)
        if (execution.contains(ExecutionMode.Exec1)) {
            val elapsed1 = measureTime {
                println("Part 1: " + solution.part1(input))
            }
            println("Elapsed: $elapsed1")
            println()
        }

        if (execution.contains(ExecutionMode.Exec2)) {
            val elapsed2 = measureTime {
                println("Part 2: " + solution.part2(input))
            }
            println("Elapsed: $elapsed2")
            println()
        }
    }

    test()
    run()
}
