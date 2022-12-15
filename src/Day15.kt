import kotlin.math.abs

fun main() {
    data class Signal(val sensor: Point, val beacon: Point) {
        fun getDistance(): Int {
            return abs(sensor.x - beacon.x) + abs(sensor.y - beacon.y)
        }
    }

    fun parse(input: List<String>): List<Signal> {
        val regex = """Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)""".toRegex()

        return input.map { line ->
            val (sensorX, sensorY, beaconX, beaconY) = regex
                .matchEntire(line)
                ?.destructured
                ?: throw IllegalArgumentException("Incorrect input line $line")

            Signal(Point(sensorX.toInt(), sensorY.toInt()), Point(beaconX.toInt(), beaconY.toInt()))
        }
    }

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

    fun getIntervals(
        signals: List<Signal>,
        row: Int
    ): List<Interval> {
        val intervals = mutableListOf<Interval>()
        for (signal in signals) {
            val distance = signal.getDistance()
            val diff = abs(signal.sensor.y - row)
            if (diff <= distance) {
                val delta = distance - diff
                val interval = Interval(signal.sensor.x - delta, signal.sensor.x + delta)
                // println("$signal diff: $diff delta: $delta distance: $distance interval: $interval")
                intervals.add(interval)
            }
        }

        val merged = merge(intervals)
        // println("Merged: $merged")
        return merged
    }

    fun part1(signals: List<Signal>, row: Int): Int {
        val merged = getIntervals(signals, row)

        val points = signals
            .flatMap { s -> listOf(s.beacon, s.sensor) }
            .filter { p -> p.y == row }
            .map { p -> p.x }
            .toSet()

        return merged.sumOf { i -> i.countPoints(points) }
    }

    fun part2(signals: List<Signal>, limit: Int): Long {
        for (row in 0..limit) {
            val intervals = getIntervals(signals, row)
            if (intervals.size == 2) {
                val left = intervals[0]
                val right = intervals[1]

                if (left.end + 1 == right.start - 1) {
                    return (left.end + 1) * 4000000L + row
                } else {
                    println("Error: $left $right $row")
                    throw RuntimeException()
                }
            }

            if (intervals.size != 1) {
                println("Error: ${intervals.size} $row")
                throw RuntimeException()
            }
        }

        return -1
    }

    val testInput = readInput("Day15_test")
    assert(part1(parse(testInput), 10), 26)
    assert(part2(parse(testInput), 20), 56000011L)

    val input = readInput("Day15")
    println(part1(parse(input), 2000000))
    println(part2(parse(input), 4000000))
}
