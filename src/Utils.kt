import java.io.File
import kotlin.math.max
import kotlin.math.min

fun readInput(name: String) = File("src", "$name.txt")
    .readLines()

fun <T> assert(actual: T, expected: T) {
    if (actual == expected) {
        return
    }

    throw Exception("Actual $actual Expected $expected")
}

data class Interval(val start: Int, val end: Int) {
    companion object {
        fun merge(intervals: List<Interval>): List<Interval> {
            var sorted = intervals.sortedBy { i -> i.start }
            var hasOverlap = true
            while (hasOverlap && sorted.size > 1) {
                hasOverlap = false
                val bucket = sorted.toMutableList<Interval?>()
                var i = 0
                while (i < bucket.size - 1) {
                    if (bucket[i] != null && bucket[i + 1] != null && bucket[i]!!.hasOverlap(bucket[i + 1]!!)) {
                        hasOverlap = true
                        val merged = bucket[i]!!.merge(bucket[i + 1]!!)
                        bucket[i] = null
                        bucket[i + 1] = merged
                    }

                    i += 1
                }

                sorted = bucket.filterNotNull()
            }

            return sorted
        }
    }

    fun hasFullOverlap(other: Interval): Boolean {
        return hasFullOverlap(this, other) || hasFullOverlap(other, this)
    }

    fun hasOverlap(other: Interval): Boolean {
        return hasOverlap(this, other) || hasOverlap(other, this)
    }

    fun merge(other: Interval): Interval {
        return Interval(min(this.start, other.start), max(this.end, other.end))
    }

    private fun hasFullOverlap(x: Interval, y: Interval): Boolean {
        return x.start >= y.start && x.end <= y.end
    }

    private fun hasOverlap(x: Interval, y: Interval): Boolean {
        return x.start >= y.start && x.start <= y.end
    }

    fun countPoints(excludePoints: Set<Int>? = null): Int {
        var count = end - start + 1
        if (excludePoints == null) {
            return count
        }

        for (p in excludePoints) {
            if (p in start..end) {
                --count
            }
        }

        return count
    }

    fun minStart(x: Int): Interval {
        if (x < start) {
            return Interval(x, end)
        }

        return this
    }

    fun maxEnd(x: Int): Interval {
        if (x > end) {
            return Interval(start, x)
        }

        return this
    }

    fun isInside(t: Int): Boolean {
        return t in start..end
    }
}

data class Point(val x: Int, val y: Int) {
    fun sum(other: Point): Point {
        return Point(this.x + other.x, this.y + other.y)
    }
}

enum class Direction {
    Up,
    Down,
    Left,
    Right
}

object DirectionManager {
    private val directions = arrayOf(Direction.Up, Direction.Right, Direction.Down, Direction.Left)

    fun turn(current: Direction, target: Direction): Direction {
        if (target == Direction.Down || target == Direction.Up) {
            throw RuntimeException("Illegal action $current -> $target")
        }

        val d = if (target == Direction.Left) -1 else 1
        val index = (directions.indexOf(current) + d + directions.size) % directions.size
        return directions[index]
    }
}

enum class Sign {
    Eq,
    Less,
    More
}

enum class ExecutionMode {
    Test1,
    Test2,
    Exec1,
    Exec2
}
