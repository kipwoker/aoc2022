import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.math.max
import kotlin.math.min

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt")
    .readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

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
