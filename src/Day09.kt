import kotlin.math.abs

enum class Direction {
    Up,
    Down,
    Left,
    Right
}

class Command(val direction: Direction, val count: Int)
data class Point(val x: Int, val y: Int)

fun main() {
    fun parse(input: List<String>): List<Command> {
        return input.map { line ->
            val parts = line.split(' ')
            val direction = when (parts[0]) {
                "U" -> Direction.Up
                "D" -> Direction.Down
                "L" -> Direction.Left
                "R" -> Direction.Right
                else -> throw RuntimeException()
            }

            Command(direction, parts[1].toInt())
        }
    }

    fun move(point: Point, direction: Direction): Point {
        return when (direction) {
            Direction.Up -> Point(point.x, point.y + 1)
            Direction.Down -> Point(point.x, point.y - 1)
            Direction.Left -> Point(point.x - 1, point.y)
            Direction.Right -> Point(point.x + 1, point.y)
        }
    }

    fun isTouching(head: Point, tail: Point): Boolean {
        return abs(head.x - tail.x) <= 1 && abs(head.y - tail.y) <= 1
    }

    fun getDelta(head: Int, tail: Int): Int {
        if (head == tail) {
            return 0
        }

        if (head > tail) {
            return 1
        }

        return -1
    }

    fun tryMoveTail(head: Point, tail: Point): Point {
        if (isTouching(head, tail)) {
            return tail
        }

        val dx = getDelta(head.x, tail.x)
        val dy = getDelta(head.y, tail.y)

        return Point(tail.x + dx, tail.y + dy)
    }

    fun part1(input: List<String>): Int {
        val commands = parse(input)
        val visited = mutableSetOf<Point>()
        var head = Point(0, 0)
        var tail = Point(0, 0)
        visited.add(tail)
        for (command in commands) {
            for (i in 1..command.count) {
                head = move(head, command.direction)
                tail = tryMoveTail(head, tail)
                visited.add(tail)
            }
        }

        return visited.count()
    }

    fun part2(input: List<String>): Int {
        val commands = parse(input)
        val visited = mutableSetOf<Point>()
        var head = Point(0, 0)
        val size = 8
        val middle = (1..size).map { Point(0, 0) }.toMutableList()
        var tail = Point(0, 0)
        visited.add(tail)
        for (command in commands) {
            for (i in 1..command.count) {
                head = move(head, command.direction)
                var pointer = head
                for (j in 0 until size) {
                    middle[j] = tryMoveTail(pointer, middle[j])
                    pointer = middle[j]
                }
                tail = tryMoveTail(pointer, tail)
                visited.add(tail)
            }
        }

        return visited.count()
    }

    val testInput = readInput("Day09_test")
    assert(part1(testInput), 13)
    assert(part2(testInput), 1)

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}
