import kotlin.math.max

data class LongPoint(val x: Long, val y: Long) {
    fun sum(other: LongPoint): LongPoint {
        return LongPoint(this.x + other.x, this.y + other.y)
    }
}

class Rock(val cells: List<LongPoint>, var shift: LongPoint) {
    fun getCoords(): List<LongPoint> {
        return cells.map { x -> x.sum(shift) }
    }

    fun hasCollision(points: Set<LongPoint>, move: LongPoint): Boolean {
        val newPosition = this.getCoords()
            .map { it.sum(move) }

        val hasBoundsCollision = newPosition.any { p -> p.x == -1L || p.y == -1L || p.x == 7L }
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
        fun create(index: Int, init: LongPoint): Rock {
            return when (val number = index % 5) {
                // ####
                0 -> Rock(
                    listOf(
                        LongPoint(0, 0),
                        LongPoint(1, 0),
                        LongPoint(2, 0),
                        LongPoint(3, 0)
                    ),
                    init
                )
                // +
                1 -> Rock(
                    listOf(
                        LongPoint(1, 0),
                        LongPoint(0, 1),
                        LongPoint(1, 1),
                        LongPoint(2, 1),
                        LongPoint(1, 2)
                    ),
                    init
                )
                // _|
                2 -> Rock(
                    listOf(
                        LongPoint(0, 0),
                        LongPoint(1, 0),
                        LongPoint(2, 0),
                        LongPoint(2, 1),
                        LongPoint(2, 2)
                    ),
                    init
                )
                // |
                3 -> Rock(
                    listOf(
                        LongPoint(0, 0),
                        LongPoint(0, 1),
                        LongPoint(0, 2),
                        LongPoint(0, 3)
                    ),
                    init
                )
                // square
                4 -> Rock(
                    listOf(
                        LongPoint(0, 0),
                        LongPoint(1, 0),
                        LongPoint(0, 1),
                        LongPoint(1, 1)
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
    fun parse(input: List<String>): List<Direction> {
        return input[0].toCharArray().map { c -> if (c == '<') Direction.Left else Direction.Right }
    }

    fun print(rocks: List<Rock>) {
        val points = rocks.flatMap { r -> r.getCoords() }.toSet()
        val maxY = points.maxOf { c -> c.y }
        for (y in maxY downTo 0) {
            for (x in 0L..6L) {
                if (points.contains(LongPoint(x, y))) {
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

    fun play(directions: List<Direction>, rocksLimit: Long): Long {
        var directionIterator = 0
        var rockIterator = 0
        var shift = LongPoint(2, 3)
        val stopped = mutableListOf<Rock>()
        val rockPoints = mutableSetOf<LongPoint>()
        var activeRock: Rock? = null
        var maxY = 0L

        val dy = LongPoint(0, -1)
        while (stopped.size < rocksLimit) {
            if (activeRock == null) {
                activeRock = Rock.create(rockIterator, shift)
                ++rockIterator
            }
            val direction = directions[directionIterator]
            val dx = if (direction == Direction.Left) LongPoint(-1, 0) else LongPoint(1, 0)
            if (!activeRock.hasCollision(rockPoints, dx)) {
                activeRock.shift = activeRock.shift.sum(dx)
            }

            if (!activeRock.hasCollision(rockPoints, dy)) {
                activeRock.shift = activeRock.shift.sum(dy)
            } else {
                stopped.add(activeRock)
                val coords = activeRock.getCoords()
                rockPoints.addAll(coords)
                maxY = max(maxY, coords.maxOf { c -> c.y })

                shift = LongPoint(shift.x, maxY + 3 + 1)

                activeRock = null

                // print(stopped)
            }

            directionIterator = (directionIterator + 1) % directions.size
        }

        return maxY + 1
    }

    fun part1(input: List<String>): Long {
        val directions = parse(input)
        return play(directions, 2022L)
    }

    fun part2(input: List<String>): Long {
        val directions = parse(input)
        return play(directions, 1000000000000L)
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

    test()
    // run()
}
