class Day12Input(
    val graph: Map<Point, Int>,
    val start: Point,
    val end: Point
)

data class Trace(val weight: Int, val current: Point, val prev: Point?, val distance: Int)

fun main() {
    fun getNeighbors(point: Point, graph: Map<Point, Int>): List<Point> {
        return listOf(
            Point(point.x - 1, point.y),
            Point(point.x + 1, point.y),
            Point(point.x, point.y - 1),
            Point(point.x, point.y + 1)
        ).filter { graph.contains(it) }
    }

    fun search(graph: Map<Point, Int>, startPoints: List<Point>, endPoint: Point): Int {
        val visited = startPoints.toMutableSet()
        val queue = ArrayDeque(
            startPoints.map { start -> Trace(graph[start]!!, start, null, 0) }
        )

        while (queue.isNotEmpty()) {
            val item = queue.removeFirst()
            if (item.current == endPoint) {
                return item.distance
            }

            val source = item.current
            val sourceWeight = graph[source]!!
            for (target in getNeighbors(source, graph)) {
                val targetWeight = graph[target]!!
                if (targetWeight - sourceWeight > 1 || target in visited) {
                    continue
                }
                visited.add(target)
                queue.addLast(Trace(targetWeight, target, source, item.distance + 1))
            }
        }

        return -1
    }

    fun parse(input: List<String>): Day12Input {
        var start = Point(0, 0)
        var end = Point(0, 0)
        val graph = input.mapIndexed { i, line ->
            line.mapIndexed { j, cell ->
                val value: Int = when (cell) {
                    'S' -> {
                        start = Point(i, j)
                        1
                    }
                    'E' -> {
                        end = Point(i, j)
                        'z' - 'a' + 1
                    }
                    else ->
                        (cell - 'a' + 1)
                }

                Point(i, j) to value
            }
        }.flatten().toMap()

        return Day12Input(graph, start, end)
    }

    fun part1(input: List<String>): Int {
        val day12Input = parse(input)
        return search(day12Input.graph, listOf(day12Input.start), day12Input.end)
    }

    fun part2(input: List<String>): Int {
        val day12Input = parse(input)
        val startPoints = day12Input.graph.filter { it.value == 1 }.map { it.key }
        return search(day12Input.graph, startPoints, day12Input.end)
    }

    val testInput = readInput("Day12_test")
    assert(part1(testInput), 31)
    assert(part2(testInput), 29)

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}
