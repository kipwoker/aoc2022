import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

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

class Interval(val start: Int, val end: Int) {
    fun hasFullOverlap(other: Interval): Boolean {
        return hasFullOverlap(this, other) || hasFullOverlap(other, this)
    }

    fun hasOverlap(other: Interval): Boolean {
        return hasOverlap(this, other) || hasOverlap(other, this)
    }

    private fun hasFullOverlap(x: Interval, y: Interval): Boolean {
        return x.start >= y.start && x.end <= y.end
    }

    private fun hasOverlap(x: Interval, y: Interval): Boolean {
        return x.start >= y.start && x.start <= y.end
    }
}

data class Point(val x: Int, val y: Int)