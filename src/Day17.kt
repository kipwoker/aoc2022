import kotlin.math.max

class Rock(val cells: List<Point>, var shift: Point) {
    fun getCoords(): List<Point> {
        return cells.map { x -> x.sum(shift) }
    }

    fun hasCollision(points: Set<Point>, move: Point): Boolean {
        val newPosition = this.getCoords()
            .map { it.sum(move) }

        val hasBoundsCollision = newPosition.any { p -> p.x == -1 || p.y == -1 || p.x == 7 }
        if (hasBoundsCollision) {
            return true
        }

        val hasCollision = newPosition
            .intersect(points)
            .isNotEmpty()
        if (hasCollision) {
            return true
        }

        return false
    }

    companion object {
        fun create(index: Int, init: Point): Rock {
            return when (val number = index % 5) {
                // ####
                0 -> Rock(
                    listOf(
                        Point(0, 0),
                        Point(1, 0),
                        Point(2, 0),
                        Point(3, 0)
                    ),
                    init
                )
                // +
                1 -> Rock(
                    listOf(
                        Point(1, 0),
                        Point(0, 1),
                        Point(1, 1),
                        Point(2, 1),
                        Point(1, 2)
                    ),
                    init
                )
                // _|
                2 -> Rock(
                    listOf(
                        Point(0, 0),
                        Point(1, 0),
                        Point(2, 0),
                        Point(2, 1),
                        Point(2, 2)
                    ),
                    init
                )
                // |
                3 -> Rock(
                    listOf(
                        Point(0, 0),
                        Point(0, 1),
                        Point(0, 2),
                        Point(0, 3)
                    ),
                    init
                )
                // square
                4 -> Rock(
                    listOf(
                        Point(0, 0),
                        Point(1, 0),
                        Point(0, 1),
                        Point(1, 1)
                    ),
                    init
                )
                else -> throw RuntimeException("Unexpected $number")
            }
        }
    }
}

@Suppress("DuplicatedCode")
fun main() {
    data class State(
        val stoppedRocksCount: Long,
        val ground: Set<Point>,
        val maxY: Int
    )

    fun parse(input: List<String>): List<Direction> {
        return input[0].toCharArray().map { c -> if (c == '<') Direction.Left else Direction.Right }
    }

    fun print(rocks: List<Rock>) {
        val points = rocks.flatMap { r -> r.getCoords() }.toSet()
        val maxY = points.maxOf { c -> c.y }
        for (y in maxY downTo 0) {
            for (x in 0..6) {
                if (points.contains(Point(x, y))) {
                    print('#')
                } else {
                    print('.')
                }
            }
            println()
        }
        println()
        println()
    }

    fun calculateState(
        topLevelMap: MutableMap<Int, Int>,
        stopped: Long,
        maxY: Int
    ): State {
        val minY = topLevelMap.minOf { t -> t.value }
        val newGround = topLevelMap.map { t -> Point(t.key, (t.value - minY)) }.toSet()
        return State(stopped, newGround, maxY)
    }

    fun removeUnreachable(
        topLevelMap: MutableMap<Int, Int>,
        ground: MutableSet<Point>
    ) {
        val minLevel = topLevelMap.values.min()
        ground.removeIf { it.y < minLevel }
    }

    fun actualizeTopLevels(
        coords: List<Point>,
        topLevelMap: MutableMap<Int, Int>
    ) {
        for (coord in coords) {
            val top = topLevelMap[coord.x]
            if (top == null || top < coord.y) {
                topLevelMap[coord.x] = coord.y
            }
        }
    }

    fun play(directions: List<Direction>, rocksLimit: Long, initGround: Set<Point>): List<State> {
        var directionIterator = 0
        var rockIterator = 0
        var shift = Point(2, 3)
        val ground = initGround.toMutableSet()
        var stopped = 0L
        val topLevelMap = mutableMapOf<Int, Int>()
        var activeRock: Rock? = null
        var maxY = 0

        val states = mutableListOf<State>()

        val dy = Point(0, -1)
        while (stopped < rocksLimit) {
            if (activeRock == null) {
                activeRock = Rock.create(rockIterator, shift)
                ++rockIterator
            }
            val direction = directions[directionIterator]
            val dx = if (direction == Direction.Left) Point(-1, 0) else Point(1, 0)

            if (!activeRock.hasCollision(ground, dx)) {
                activeRock.shift = activeRock.shift.sum(dx)
            }

            if (!activeRock.hasCollision(ground, dy)) {
                activeRock.shift = activeRock.shift.sum(dy)
            } else {
                ++stopped
                val coords = activeRock.getCoords()
                actualizeTopLevels(coords, topLevelMap)
                ground.addAll(coords)
                removeUnreachable(topLevelMap, ground)

                maxY = max(maxY, topLevelMap.values.max() + 1)
                shift = Point(shift.x, maxY + 3)

                activeRock = null
            }

            directionIterator = (directionIterator + 1) % directions.size

            if (directionIterator == 0) {
                states.add(calculateState(topLevelMap, stopped, maxY))
                if (states.size > 1) {
                    break
                }
            }
        }

        if (states.size <= 1) {
            states.add(calculateState(topLevelMap, stopped, maxY))
        }

        return states
    }

    fun findMax(directions: List<Direction>, totalRocks: Long): Long {
        val defaultGround = (0..6).map { Point(it, -1) }.toSet()

        val states = play(directions, totalRocks, defaultGround).reversed()
        val lastRocksCount = states[0].stoppedRocksCount
        val maxY = states[0].maxY
        val leftRocks = totalRocks - lastRocksCount
        if (leftRocks == 0L) {
            return maxY.toLong()
        }

        val deltaRocks = lastRocksCount - states[1].stoppedRocksCount
        val deltaY = states[0].maxY - states[1].maxY

        val mult = leftRocks / deltaRocks
        val sliceY = deltaY * mult
        val restRocks = leftRocks % deltaRocks

        val (_, _, maxY1) = play(
            directions,
            restRocks,
            states[0].ground
        )[0]

        return maxY + sliceY + maxY1 - 1
    }

    fun part1(input: List<String>): Long {
        return findMax(parse(input), 2022L)
    }

    fun part2(input: List<String>): Long {
        return findMax(parse(input), 1000000000000L)
    }

// ==================================================================== //

    val day = "17"

    fun test() {
        val testInput = readInput("Day${day}_test")
        assert(part1(testInput), 3068L)
        assert(part2(testInput), 1514285714288L)
    }

    fun run() {
        val input = readInput("Day$day")
        println(part1(input))
        println(part2(input))
    }

    // test()
    run()
}
