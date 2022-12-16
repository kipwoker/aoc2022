import java.math.BigInteger
import kotlin.math.max
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    data class Vertex(val id: String, val rate: Int, val directions: Set<String>)

    fun switchOn(state: BigInteger, index: Int): BigInteger {
        return state or (BigInteger.ONE shl index)
    }

    fun isOpen(state: BigInteger, index: Int): Boolean {
        val bit = BigInteger.ONE shl index
        return (state and bit) == bit
    }

    data class State1(
        val vertex: String,
        val valveState: BigInteger,
        val minutesLeft: Int
    )

    data class State2(
        val vertex1: String,
        val vertex2: String,
        val valveState: BigInteger,
        val minutesLeft: Int
    )

    class Player1(
        val indexMap: Map<String, Int>,
        val cache: MutableMap<State1, Int>,
        val graph: Map<String, Vertex>,
        val allOpenedValveState: BigInteger
    ) {
        fun play(state: State1): Int {
            val cached = cache[state]
            if (cached != null) {
                return cached
            }

            if (state.valveState == allOpenedValveState) {
                return 0
            }

            if (state.minutesLeft <= 1) {
                return 0
            }

            var cost = -1
            val vertex = graph[state.vertex]!!
            val vertexIndex = indexMap[state.vertex]!!
            val minutesLeft = state.minutesLeft - 1
            if (vertex.rate != 0 && !isOpen(state.valveState, vertexIndex)) {
                val openCost = minutesLeft * vertex.rate + play(
                    State1(
                        state.vertex,
                        switchOn(state.valveState, vertexIndex),
                        minutesLeft
                    )
                )

                cost = max(cost, openCost)
            }

            for (target in vertex.directions) {
                val moveCost = play(
                    State1(
                        target,
                        state.valveState,
                        minutesLeft
                    )
                )

                cost = max(cost, moveCost)
            }

            cache[state] = cost
            return cost
        }
    }

    class Player2(
        val indexMap: Map<String, Int>,
        val cache: MutableMap<State2, Int>,
        val graph: Map<String, Vertex>,
        val allOpenedValveState: BigInteger
    ) {
        fun play(state: State2): Int {
            val cached = cache[state]
            if (cached != null) {
                return cached
            }

            if (state.valveState == allOpenedValveState) {
                return 0
            }

            if (state.minutesLeft <= 1) {
                return 0
            }

            var cost = -1
            val minutesLeft = state.minutesLeft - 1

            // open + open
            if (state.vertex1 != state.vertex2) {
                var valveState = state.valveState
                var subCost = 0

                val vertex1 = graph[state.vertex1]!!
                val vertexIndex1 = indexMap[state.vertex1]!!
                var openCount = 0
                if (vertex1.rate != 0 && !isOpen(valveState, vertexIndex1)) {
                    valveState = switchOn(valveState, vertexIndex1)
                    subCost += minutesLeft * vertex1.rate
                    ++openCount
                }

                val vertex2 = graph[state.vertex2]!!
                val vertexIndex2 = indexMap[state.vertex2]!!
                if (vertex2.rate != 0 && !isOpen(valveState, vertexIndex2)) {
                    valveState = switchOn(valveState, vertexIndex2)
                    subCost += minutesLeft * vertex2.rate
                    ++openCount
                }

                if (openCount == 2) {
                    val openCost = subCost + play(
                        State2(
                            state.vertex1,
                            state.vertex2,
                            valveState,
                            minutesLeft
                        )
                    )

                    cost = max(cost, openCost)
                }
            }

            // open + move
            run {
                var valveState = state.valveState
                var subCost = 0

                val vertex1 = graph[state.vertex1]!!
                val vertexIndex1 = indexMap[state.vertex1]!!
                var opened = false
                if (vertex1.rate != 0 && !isOpen(valveState, vertexIndex1)) {
                    valveState = switchOn(valveState, vertexIndex1)
                    subCost += minutesLeft * vertex1.rate
                    opened = true
                }

                if (opened) {
                    val vertex2 = graph[state.vertex2]!!
                    for (target in vertex2.directions) {
                        val moveCost = subCost + play(
                            State2(
                                state.vertex1,
                                target,
                                valveState,
                                minutesLeft
                            )
                        )

                        cost = max(cost, moveCost)
                    }
                }
            }

            // move + open
            run {
                var valveState = state.valveState
                var subCost = 0

                val vertex2 = graph[state.vertex2]!!
                val vertexIndex2 = indexMap[state.vertex2]!!
                var opened = false
                if (vertex2.rate != 0 && !isOpen(valveState, vertexIndex2)) {
                    valveState = switchOn(valveState, vertexIndex2)
                    subCost += minutesLeft * vertex2.rate
                    opened = true
                }

                if (opened) {
                    val vertex1 = graph[state.vertex1]!!
                    for (target in vertex1.directions) {
                        val moveCost = subCost + play(
                            State2(
                                target,
                                state.vertex2,
                                valveState,
                                minutesLeft
                            )
                        )

                        cost = max(cost, moveCost)
                    }
                }
            }

            // move + move
            run {
                val vertex1 = graph[state.vertex1]!!
                val vertex2 = graph[state.vertex2]!!
                for (target1 in vertex1.directions) {
                    for (target2 in vertex2.directions) {
                        if (target1 == target2) {
                            continue
                        }

                        val moveCost = play(
                            State2(
                                target1,
                                target2,
                                state.valveState,
                                minutesLeft
                            )
                        )

                        cost = max(cost, moveCost)
                    }
                }
            }

            cache[state] = cost
            return cost
        }
    }

    fun parse(input: List<String>): List<Vertex> {
        return input.map { line ->
            val parts = line.split(' ')
            val vertex = parts[1]
            val rate = parts[4].split('=')[1].split(';')[0].toInt()
            val directions = mutableSetOf<String>()
            for (i in 9 until parts.size) {
                directions.add(parts[i].split(',')[0])
            }

            Vertex(vertex, rate, directions)
        }
    }

    fun part1(input: List<String>): Int {
        val vertexes = parse(input)
        val indexMap = vertexes.mapIndexed { index, item -> item.id to index }.toMap()
        val graph = vertexes.associateBy { item -> item.id }

        val allOpened = BigInteger("1".repeat(indexMap.size))
        val allClosed = BigInteger.ZERO

        val player = Player1(indexMap, mutableMapOf(), graph, allOpened)
        return player.play(State1("AA", allClosed, 30))
    }

    fun part2(input: List<String>): Int {
        val vertexes = parse(input)
        val indexMap = vertexes.mapIndexed { index, item -> item.id to index }.toMap()
        val graph = vertexes.associateBy { item -> item.id }

        val allOpened = BigInteger("1".repeat(indexMap.size))
        val allClosed = BigInteger.ZERO

        val player = Player2(indexMap, mutableMapOf(), graph, allOpened)
        return player.play(State2("AA", "AA", allClosed, 20))
    }

    val testInput = readInput("Day16_test")
    assert(part1(testInput), 1651)
    assert(part2(testInput), 1707)
    println("=== tests passed ===")

    val input = readInput("Day16")
    val elapsed1 = measureTime { println(part1(input)) }
    println("Elapsed: $elapsed1")
    val elapsed2 = measureTime { println(part2(input)) }
    println("Elapsed: $elapsed2")
}
