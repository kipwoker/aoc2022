import kotlin.math.max
import kotlin.math.min

enum class CaveCell {
    Air,
    Rock,
    Sand
}

enum class SandFallResult {
    Stuck,
    Fall
}

val caveSize = 1000

fun main() {
    class Cave(val map: Array<Array<CaveCell>>) {
        fun getBounds(): Pair<Point, Point> {
            var minX = caveSize
            var minY = caveSize
            var maxX = 0
            var maxY = 0
            for (y in 0 until caveSize) {
                for (x in 0 until caveSize) {
                    if (map[x][y] != CaveCell.Air) {
                        minX = min(minX, x)
                        minY = min(minY, y)
                        maxX = max(maxX, x)
                        maxY = max(maxY, y)
                    }
                }
            }

            return Point(minX, minY) to Point(maxX, maxY)
        }
    }

    fun parse(input: List<String>): Cave {
        val cave = Cave(Array(caveSize) { _ -> Array(caveSize) { _ -> CaveCell.Air } })

        val pointBlocks = input.map { line ->
            line
                .split('-')
                .map { pair ->
                    val parts = pair.split(',')
                    Point(parts[0].toInt(), parts[1].toInt())
                }
        }

        for (pointBlock in pointBlocks) {
            for (i in 0..pointBlock.size - 2) {
                val left = pointBlock[i]
                val right = pointBlock[i + 1]
                if (left.x != right.x && left.y != right.y) {
                    println("Unexpected $left -> $right")
                    throw RuntimeException()
                }

                if (left.x == right.x) {
                    val x = left.x
                    for (dy in min(left.y, right.y)..max(left.y, right.y)) {
                        cave.map[x][dy] = CaveCell.Rock
                    }
                }

                if (left.y == right.y) {
                    val y = left.y
                    for (dx in min(left.x, right.x)..max(left.x, right.x)) {
                        cave.map[dx][y] = CaveCell.Rock
                    }
                }
            }
        }

        return cave
    }

    fun print(cave: Cave) {
        val window = 0
        val (minPoint, maxPoint) = cave.getBounds()
        for (y in (minPoint.y - window)..(maxPoint.y + window)) {
            for (x in (minPoint.x - window)..(maxPoint.x + window)) {
                val sign = when (cave.map[x][y]) {
                    CaveCell.Air -> '.'
                    CaveCell.Rock -> '#'
                    CaveCell.Sand -> 'o'
                }
                print(sign)
            }
            println()
        }
    }

    fun playSand(cave: Cave): SandFallResult {
        val (_, maxPoint) = cave.getBounds()
        var sx = 500
        var sy = 0

        while (sy < maxPoint.y) {
            if (cave.map[sx][sy + 1] == CaveCell.Air) {
                ++sy
            } else if (cave.map[sx - 1][sy + 1] == CaveCell.Air) {
                --sx
                ++sy
            } else if (cave.map[sx + 1][sy + 1] == CaveCell.Air) {
                ++sx
                ++sy
            } else {
                cave.map[sx][sy] = CaveCell.Sand
                return SandFallResult.Stuck
            }
        }

        return SandFallResult.Fall
    }

    fun play1(cave: Cave): Int {
        var counter = 0
        while (playSand(cave) != SandFallResult.Fall) {
            // print(cave)
            ++counter
        }

        return counter
    }

    fun play2(cave: Cave): Int {
        val (_, maxPoint) = cave.getBounds()
        for (x in 0 until caveSize) {
            cave.map[x][maxPoint.y + 2] = CaveCell.Rock
        }

        var counter = 0
        while (playSand(cave) != SandFallResult.Fall && cave.map[500][0] != CaveCell.Sand) {
            ++counter
        }

        return counter + 1
    }

    fun part1(input: List<String>): Int {
        val cave = parse(input)
        return play1(cave)
    }

    fun part2(input: List<String>): Int {
        val cave = parse(input)
        return play2(cave)
    }

    val testInput = readInput("Day14_test")
    assert(part1(testInput), 24)
    assert(part2(testInput), 93)

    val input = readInput("Day14")
    println(part1(input))
    println(part2(input))
}
