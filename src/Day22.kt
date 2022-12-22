import kotlin.math.max
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class Day22 {
    abstract class Instruction
    data class Move(val count: Int) : Instruction()
    data class Turn(val direction: Direction) : Instruction()

    data class PathItem(val id: Char, val point: Point, val direction: Direction)

    class Grove(
        val colBounds: Array<Interval>,
        val rowBounds: Array<Interval>,
        val walls: Set<Point>,
        val path: MutableList<PathItem>,
        val cubeSize: Int,
        var transitionType: Int
    )

    fun parseGrove(input: List<String>): Grove {
        val maxRowSize = input.size
        val maxColSize = input.maxOf { it.length }

        val rowBounds = Array(maxRowSize) { Interval(maxColSize + 1, -1) }
        val colBounds = Array(maxColSize) { Interval(maxRowSize + 1, -1) }
        val walls = mutableSetOf<Point>()

        for ((x, line) in input.withIndex()) {
            for ((y, cell) in line.withIndex()) {
                if (cell == '#' || cell == '.') {
                    rowBounds[x] = rowBounds[x].minStart(y).maxEnd(y)
                    colBounds[y] = colBounds[y].minStart(x).maxEnd(x)

                    if (cell == '#') {
                        walls.add(Point(x, y))
                    }
                }
            }
        }

        val cubeSize = max(maxRowSize, maxColSize) / 4
        return Grove(colBounds, rowBounds, walls, mutableListOf(), cubeSize, 1)
    }

    fun parseInstructions(input: String): List<Instruction> {
        val buffer = mutableListOf<Char>()
        val result = mutableListOf<Instruction>()
        for (c in input) {
            if (c == 'L' || c == 'R') {
                if (buffer.isNotEmpty()) {
                    val number = String(buffer.toCharArray())
                    result.add(Move(number.toInt()))
                    buffer.clear()
                }
                if (c == 'L') {
                    result.add(Turn(Direction.Left))
                } else {
                    result.add(Turn(Direction.Right))
                }
            } else {
                buffer.add(c)
            }
        }

        if (buffer.isNotEmpty()) {
            val number = String(buffer.toCharArray())
            result.add(Move(number.toInt()))
            buffer.clear()
        }

        return result
    }

    fun parse(input: List<String>): Pair<Grove, List<Instruction>> {
        val mapSource = input.take(input.size - 2)
        val instructionsSource = input.last()

        return parseGrove(mapSource) to parseInstructions(instructionsSource)
    }

    fun getDirectionChar(direction: Direction): Char {
        return when (direction) {
            Direction.Up -> '^'
            Direction.Down -> 'v'
            Direction.Left -> '<'
            Direction.Right -> '>'
        }
    }

    fun print(grove: Grove) {
        val maxRowIndex = grove.colBounds.maxOf { it.end }
        val maxColIndex = grove.rowBounds.maxOf { it.end }

        val pathMap = grove.path.associateBy { it.point }

        var i = 0
        for (x in 0..maxRowIndex) {
            for (y in 0..maxColIndex) {
                if (grove.colBounds[y].isInside(x) && grove.rowBounds[x].isInside(y)) {
                    val point = Point(x, y)
                    if (pathMap.containsKey(point)) {
                        print(pathMap[point]!!.id)
                        ++i
                    } else if (point in grove.walls) {
                        print('#')
                    } else {
                        print('.')
                    }
                } else {
                    print(' ')
                }
            }
            println()
        }
    }

    fun makeTransition1(grove: Grove, target: Point, d: Point): Point {
        if (d.x == 0) {
            val row = grove.rowBounds[target.x]
            if (!row.isInside(target.y)) {
                return if (row.end < target.y) {
                    Point(target.x, row.start)
                } else {
                    Point(target.x, row.end)
                }
            }
        }

        if (d.y == 0) {
            val col = grove.colBounds[target.y]
            if (!col.isInside(target.x)) {
                return if (col.end < target.x) {
                    Point(col.start, target.y)
                } else {
                    Point(col.end, target.y)
                }
            }
        }

        return target
    }

    fun makeTransition2(grove: Grove, current: Point, d: Point, direction: Direction): Pair<Point, Direction> {
        val target = current.sum(d)
        val s = grove.cubeSize

        if (d.x == 0) {
            val row = grove.rowBounds[current.x]
            if (target.y < row.start) {
                // left
                when (val x = current.x) {
                    in 0 until s -> {
                        // 1 -> 3
                        return Point(s, s + x) to Direction.Down
                    }

                    in s until (2 * s) -> {
                        // 2 -> 6
                        return Point(3 * s - 1, 5 * s - x - 1) to Direction.Up
                    }

                    in (2 * s) until (3 * s) -> {
                        // 5 -> 3
                        return Point(2 * s - 1, 4 * s - x - 1) to Direction.Up
                    }

                    else -> {
                        throw RuntimeException("Unexpected $x")
                    }
                }
            } else if (target.y > row.end) {
                // right
                when (val x = current.x) {
                    in 0 until s -> {
                        // 1 -> 6
                        return Point(3 * s - x - 1, 4 * s - 1) to Direction.Left
                    }

                    in s until (2 * s) -> {
                        // 4 -> 6
                        return Point(2 * s, 5 * s - x - 1) to Direction.Down
                    }

                    in (2 * s) until (3 * s) -> {
                        // 6 -> 1
                        return Point(3 * s - x - 1, 3 * s - 1) to Direction.Left
                    }

                    else -> {
                        throw RuntimeException("Unexpected $x")
                    }
                }
            }
        }

        if (d.y == 0) {
            val col = grove.colBounds[current.y]
            if (target.x < col.start) {
                // up
                when (val y = current.y) {
                    in 0 until s -> {
                        // 2 -> 1
                        return Point(0, 3 * s - y - 1) to Direction.Down
                    }

                    in s until 2 * s -> {
                        // 3 -> 1
                        return Point(y - s, 2 * s) to Direction.Right
                    }

                    in 2 * s until 3 * s -> {
                        // 1 -> 2
                        return Point(s, 3 * s - y - 1) to Direction.Down
                    }

                    in 3 * s until 4 * s -> {
                        // 6 -> 4
                        return Point(5 * s - 1 - y, 3 * s - 1) to Direction.Left
                    }

                    else -> {
                        throw RuntimeException("Unexpected $y")
                    }
                }
            } else if (target.x > col.end) {
                // down
                when (val y = current.y) {
                    in 0 until s -> {
                        // 2 -> 5
                        return Point(3 * s - 1, 3 * s - 1 - y) to Direction.Up
                    }

                    in s until 2 * s -> {
                        // 3 -> 5
                        return Point(y + s, 2 * s) to Direction.Right
                    }

                    in 2 * s until 3 * s -> {
                        // 5 -> 2
                        return Point(2 * s - 1, 3 * s - y - 1) to Direction.Up
                    }

                    in 3 * s until 4 * s -> {
                        // 6 -> 2
                        return Point(5 * s - 1 - y, 0) to Direction.Right
                    }

                    else -> {
                        throw RuntimeException("Unexpected $y")
                    }
                }
            }
        }

        return target to direction
    }

    fun makeTransition3(grove: Grove, current: Point, d: Point, direction: Direction): Pair<Point, Direction> {
        val target = current.sum(d)
        val s = grove.cubeSize

        if (d.x == 0) {
            val row = grove.rowBounds[current.x]
            if (target.y < row.start) {
                // left
                when (val x = current.x) {
                    in 0 until s -> {
                        // 3 -> 1
                        return Point(s - 1 - x + 2 * s, 0) to Direction.Right
                    }

                    in s until (2 * s) -> {
                        // 4 -> 1
                        return Point(2 * s, x - s) to Direction.Down
                    }

                    in (2 * s) until (3 * s) -> {
                        // 1 -> 3
                        return Point(s - 1 - x + 2 * s, s) to Direction.Right
                    }

                    in (3 * s) until (4 * s) -> {
                        // 2 -> 3
                        return Point(0, x - 2 * s) to Direction.Down
                    }

                    else -> {
                        throw RuntimeException("Unexpected $x")
                    }
                }
            } else if (target.y > row.end) {
                // right
                when (val x = current.x) {
                    in 0 until s -> {
                        // 6 -> 5
                        return Point(s - x - 1 + 2 * s, 2 * s - 1) to Direction.Left
                    }

                    in s until (2 * s) -> {
                        // 4 -> 6
                        return Point(s - 1, x + s) to Direction.Up
                    }

                    in (2 * s) until (3 * s) -> {
                        // 5 -> 6
                        return Point(s - 1 - x + 2 * s, 3 * s - 1) to Direction.Left
                    }

                    in (3 * s) until (4 * s) -> {
                        // 2 -> 5
                        return Point(3 * s - 1, x - 2 * s) to Direction.Up
                    }

                    else -> {
                        throw RuntimeException("Unexpected $x")
                    }
                }
            }
        }

        if (d.y == 0) {
            val col = grove.colBounds[current.y]
            if (target.x < col.start) {
                // up
                when (val y = current.y) {
                    in 0 until s -> {
                        // 1 -> 4
                        return Point(y + s, s) to Direction.Right
                    }

                    in s until (2 * s) -> {
                        // 3 -> 2
                        return Point(y + 2 * s, 0) to Direction.Right
                    }

                    in (2 * s) until (3 * s) -> {
                        // 6 -> 2
                        return Point(y - 2 * s, 4 * s - 1) to Direction.Up
                    }

                    else -> {
                        throw RuntimeException("Unexpected $y")
                    }
                }
            } else if (target.x > col.end) {
                // down
                when (val y = current.y) {
                    in 0 until s -> {
                        // 2 -> 6
                        return Point(0, y + 2 * s) to Direction.Down
                    }

                    in s until (2 * s) -> {
                        // 5 -> 2
                        return Point(y + 2 * s, s - 1) to Direction.Left
                    }

                    in (2 * s) until (3 * s) -> {
                        // 6 -> 4
                        return Point(y - s, 2 * s - 1) to Direction.Left
                    }

                    else -> {
                        throw RuntimeException("Unexpected $y")
                    }
                }
            }
        }

        return target to direction
    }

    fun move(grove: Grove, initPoint: Point, count: Int, initDirection: Direction): Pair<Point, Direction> {
        var currentPoint = initPoint
        var currentDirection = initDirection
        for (i in 1..count) {
            val d = when (currentDirection) {
                Direction.Right -> Point(0, 1)
                Direction.Up -> Point(-1, 0)
                Direction.Down -> Point(1, 0)
                Direction.Left -> Point(0, -1)
            }
            var new = currentPoint.sum(d)
            if (new in grove.walls) {
                return currentPoint to currentDirection
            }

            var newDirection: Direction? = null
            if (grove.transitionType == 1) {
                new = makeTransition1(grove, new, d)
            } else {
                val result = makeTransition3(grove, currentPoint, d, currentDirection)
                new = result.first
                newDirection = result.second
            }

            if (new in grove.walls) {
                return currentPoint to currentDirection
            }
            if (newDirection != null) {
                currentDirection = newDirection
            }

            grove.path.add(PathItem('a' + grove.path.size, new, currentDirection))
//            if (new != currentPoint.sum(d)) {
//                print(grove)
//                println()
//            }

            currentPoint = new
        }

        return currentPoint to currentDirection
    }

    fun play(grove: Grove, instructions: List<Instruction>): Pair<Point, Direction> {
        var position = Point(0, grove.rowBounds[0].start)
        var direction = Direction.Right

        grove.path.add(PathItem('a' + grove.path.size, position, direction))

        for ((index, instruction) in instructions.withIndex()) {
            println("Instruction $index: $instruction $position $direction")
            when (instruction) {
                is Move -> {
                    val result = move(grove, position, instruction.count, direction)
                    position = result.first
                    direction = result.second
                }

                is Turn -> direction = DirectionManager.turn(direction, instruction.direction)
            }
        }

        return position to direction
    }

    private fun solve(grove: Grove, instructions: List<Instruction>): String {
        val (point, direction) = play(grove, instructions)
        println("Point: $point | Direction: $direction")
        print(grove)
        val directionValue = when (direction) {
            Direction.Up -> 3
            Direction.Down -> 1
            Direction.Left -> 2
            Direction.Right -> 0
        }

        return ((point.x + 1) * 1000 + (point.y + 1) * 4 + directionValue).toString()
    }

    fun part1(input: List<String>): String {
        val (grove, instructions) = parse(input)
        return solve(grove, instructions)
    }

    fun part2(input: List<String>): String {
        val (grove, instructions) = parse(input)
        grove.transitionType = 2
        return solve(grove, instructions)
    }
}

@OptIn(ExperimentalTime::class)
@Suppress("DuplicatedCode")
fun main() {
    val solution = Day22()
    val name = solution.javaClass.name

    val execution = setOf(
        // ExecutionMode.Test1,
        // ExecutionMode.Test2,
        // ExecutionMode.Exec1,
        ExecutionMode.Exec2
    )

    fun test() {
        val expected1 = "6032"
        val expected2 = "5031"

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
