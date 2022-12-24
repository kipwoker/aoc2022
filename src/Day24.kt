import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class Day24 {
    data class Valley(val blizzards: Map<Point, List<Direction>>, val maxY: Int, val maxX: Int)
    data class Moment(val expedition: Point, val minutesSpent: Int)

    fun parse(input: List<String>): Valley {
        val blizzards = mutableMapOf<Point, List<Direction>>()
        val walls = mutableSetOf<Point>()
        for ((y, line) in input.withIndex()) {
            for ((x, cell) in line.withIndex()) {
                when (cell) {
                    '#' -> walls.add(Point(x, y))
                    '>' -> blizzards[Point(x, y)] = listOf(Direction.Right)
                    '<' -> blizzards[Point(x, y)] = listOf(Direction.Left)
                    '^' -> blizzards[Point(x, y)] = listOf(Direction.Up)
                    'v' -> blizzards[Point(x, y)] = listOf(Direction.Down)
                }
            }
        }

        return Valley(blizzards, input.size - 1, input[0].length - 1)
    }

    fun nextPosition(direction: Direction, position: Point, valley: Valley): Point {
        val d = when (direction) {
            Direction.Up -> Point(0, -1)
            Direction.Down -> Point(0, 1)
            Direction.Left -> Point(-1, 0)
            Direction.Right -> Point(1, 0)
        }

        val newPosition = position.sum(d)
        if (newPosition.x <= 0) {
            return Point(valley.maxX - 1, newPosition.y)
        }
        if (newPosition.x >= valley.maxX) {
            return Point(1, newPosition.y)
        }
        if (newPosition.y <= 0) {
            return Point(newPosition.x, valley.maxY - 1)
        }
        if (newPosition.y >= valley.maxY) {
            return Point(newPosition.x, 1)
        }

        return newPosition
    }

    fun print(valley: Valley) {
        for (y in 0..valley.maxY) {
            for (x in 0..valley.maxX) {
                val point = Point(x, y)
                if (x == 0 || x == valley.maxX || y == 0 || y == valley.maxY) {
                    print('#')
                } else if (point !in valley.blizzards.keys) {
                    print('.')
                } else {
                    val directions = valley.blizzards[point]!!
                    if (directions.size == 1) {
                        when (directions.first()) {
                            Direction.Up -> print('^')
                            Direction.Down -> print('v')
                            Direction.Left -> print('<')
                            Direction.Right -> print('>')
                        }
                    } else {
                        print(directions.size)
                    }
                }
            }
            println()
        }

        println()
    }

    fun next(valley: Valley): Valley {
        val blizzards = valley.blizzards.flatMap { blizzard ->
            blizzard.value.map { direction -> nextPosition(direction, blizzard.key, valley) to direction }
        }.groupBy({ x -> x.first }, { x -> x.second })

        val newValley = Valley(blizzards, valley.maxY, valley.maxX)

        return newValley
    }

    fun getAvailableMoves(current: Point, start: Point, target: Point, valley: Valley): Set<Point> {
        return listOf(
            Point(0, 0), // wait
            Point(0, -1),
            Point(0, 1),
            Point(-1, 0),
            Point(1, 0)
        )
            .map { p -> p.sum(current) }
            .filter { p ->
                (p.x > 0 && p.x < valley.maxX && p.y > 0 && p.y < valley.maxY) ||
                    p == target || p == start
            }
            .filter { p -> !valley.blizzards.containsKey(p) }
            .toSet()
    }

    fun search(start: Point, target: Point, initValleyState: Valley): Pair<Int, Valley> {
        val valleyStates = mutableListOf(initValleyState)

        print(initValleyState)

        val q = ArrayDeque<Moment>()
        val visited = mutableSetOf<Moment>()
        val initMoment = Moment(start, 0)
        q.addLast(initMoment)
        visited.add(initMoment)

        while (q.isNotEmpty()) {
            val moment = q.removeFirst()
            val nextMinute = moment.minutesSpent + 1
            val nextValleyState = if (valleyStates.size > nextMinute) {
                valleyStates[nextMinute]
            } else {
                valleyStates.add(next(valleyStates.last()))
                valleyStates.last()
            }
            val availableMoves = getAvailableMoves(moment.expedition, start, target, nextValleyState)
            if (target in availableMoves) {
                return nextMinute to nextValleyState
            }

            for (move in availableMoves) {
                val nextMoment = Moment(move, nextMinute)
                if (nextMoment !in visited) {
                    visited.add(nextMoment)
                    q.addLast(nextMoment)
                }
            }
        }

        return -1 to initValleyState
    }

    fun part1(input: List<String>): String {
        val valley = parse(input)
        val start = Point(1, 0)
        val target = Point(valley.maxX - 1, valley.maxY)
        val count = search(start, target, valley).first
        return count.toString()
    }

    fun part2(input: List<String>): String {
        val valley = parse(input)
        val start = Point(1, 0)
        val target = Point(valley.maxX - 1, valley.maxY)
        val forward = search(start, target, valley)
        val r1 = forward.first
        println("R1 $r1")
        val reward = search(target, start, forward.second)
        val r2 = reward.first
        println("R2 $r2")
        val again = search(start, target, reward.second)
        val r3 = again.first
        println("R3 $r3")

        return (r1 + r2 + r3).toString()
    }
}

@OptIn(ExperimentalTime::class)
@Suppress("DuplicatedCode")
fun main() {
    val solution = Day24()
    val name = solution.javaClass.name

    val execution = setOf(
        ExecutionMode.Test1,
        ExecutionMode.Test2,
        ExecutionMode.Exec1,
        ExecutionMode.Exec2
    )

    fun test() {
        val expected1 = "18"
        val expected2 = "54"

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
