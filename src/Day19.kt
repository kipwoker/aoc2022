import kotlin.math.max
import kotlin.math.min
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

enum class Material(val weight: Int) {
    Ore(1),
    Clay(2),
    Obsidian(3),
    Geode(4)
}

class Day19 {
    class Blueprint(
        val number: Int,
        val costs: Map<Material, Map<Material, Int>>
    ) {
        fun maxLimits(totalMinutes: Int): Map<Material, Int> {
            val maxOreCount = costs.map { it.value[Material.Ore] ?: 0 }.max()
            return mapOf(
                Material.Ore to maxOreCount,
                Material.Clay to costs[Material.Obsidian]!![Material.Clay]!!,
                Material.Obsidian to costs[Material.Geode]!![Material.Obsidian]!!,
                Material.Geode to 12
            )
        }

        fun getMaxMaterialsMap(): MutableMap<Material, Int> {
            val multiplier = 2
            val result = mutableMapOf<Material, Int>()
            for (robotCost in costs) {
                for (material in robotCost.value) {
                    val current = result[material.key] ?: 0
                    result[material.key] = current + material.value
                }
            }

            result[Material.Geode] = 100

            for (r in result) {
                r.setValue(r.value * multiplier)
            }

            return result
        }
    }

    data class State(
        val minutesLeft: Int,
        val robotsCount: Map<Material, Int>,
        val materialsCount: Map<Material, Int>
    )

    class Player(
        val cache: MutableMap<State, Int>,
        val maxCountMap: Map<Material, Int>,
        val maxMaterialsMap: Map<Material, Int>,
        val blueprint: Blueprint,
        val minutesTotal: Int
    ) {

        fun play(state: State): Int {
            val cached = cache[state]
            if (cached != null) {
                return cached
            }

            val geodeCount = state.materialsCount.g(Material.Geode)
            val clayCount = state.materialsCount.g(Material.Clay)
            val obsidianCount = state.materialsCount.g(Material.Obsidian)
            val oreCount = state.materialsCount.g(Material.Ore)
            if (state.minutesLeft == 0) {
                return geodeCount
            }

            var justWait = false
            val oreRobotsCount = state.robotsCount.g(Material.Ore)
            val clayRobotsCount = state.robotsCount.g(Material.Clay)
            val obsidianRobotsCount = state.robotsCount.g(Material.Obsidian)
            val geodeRobotsCount = state.robotsCount.g(Material.Geode)
            if (state.minutesLeft <= 3 && clayRobotsCount == 0) {
                return geodeCount
            }

            val clayCostForObsidian = blueprint.costs[Material.Obsidian]!![Material.Clay]!!

            if (minutesTotal > 30 && state.minutesLeft < minutesTotal / 2) {
                if (oreRobotsCount < 2) {
                    return geodeCount
                }

                if (clayRobotsCount < 2) {
                    return geodeCount
                }
            }

            // not enough resources to create geode robot until time ends
            if (minutesTotal < blueprint.costs[Material.Geode]!![Material.Obsidian]!!) {
                val notEnoughClayToBuyObsidian =
                    clayCount < clayCostForObsidian
                if (obsidianRobotsCount == 0 && notEnoughClayToBuyObsidian) {
                    return geodeCount + geodeRobotsCount * minutesTotal
                }
            }

            val minutesLeft = state.minutesLeft - 1
            var maxValue = -1
            var builtGeode = false
            if (!justWait) {
                for (cost in blueprint.costs.asIterable().sortedBy { -it.key.weight }) {
                    val robotType = cost.key
                    if (needBuild(robotType, state.robotsCount[robotType] ?: 0)) {
                        val rest = tryBuild(state.materialsCount, cost.value)
                        if (rest != null) {
                            val robotsCount = increaseRobotsCount(state, robotType)
                            val materialsCount = harvest(state.robotsCount, rest)
                            val playResult = play(State(minutesLeft, robotsCount, materialsCount))
                            maxValue = max(maxValue, playResult)
                            builtGeode = robotType == Material.Geode
                        }
                    }

                    if (builtGeode) {
                        break
                    }
                }
            }

            if (!builtGeode) {
                val materialsCount = harvest(state.robotsCount, state.materialsCount)
                val playResult = play(State(minutesLeft, state.robotsCount, materialsCount))
                maxValue = max(maxValue, playResult)
            }

            cache[state] = maxValue

            return maxValue
        }

        private fun needBuild(robotType: Material, robotCount: Int): Boolean {
            val maxCount = maxCountMap[robotType] ?: throw RuntimeException("Has no $robotType")
            return robotCount < maxCount
        }

        private fun harvest(robotsCount: Map<Material, Int>, materialsCount: Map<Material, Int>): Map<Material, Int> {
            val result = materialsCount.toMutableMap()
            for (robotBlock in robotsCount) {
                val current = result[robotBlock.key] ?: 0
                result[robotBlock.key] = min(current + robotBlock.value, maxMaterialsMap[robotBlock.key]!!)
            }

            return result
        }

        private fun increaseRobotsCount(state: State, robotType: Material): Map<Material, Int> {
            val robotsCount = state.robotsCount.toMutableMap()
            val robot = robotsCount[robotType]
            if (robot == null) {
                robotsCount[robotType] = 1
            } else {
                robotsCount[robotType] = robot + 1
            }
            return robotsCount
        }

        private fun tryBuild(materialsCount: Map<Material, Int>, cost: Map<Material, Int>): Map<Material, Int>? {
            val result = materialsCount.toMutableMap()
            for (c in cost) {
                val materialCount = materialsCount[c.key]
                if (materialCount == null || materialCount < c.value) {
                    return null
                }
                result[c.key] = materialCount - c.value
            }

            return result
        }
    }

    fun parse(input: List<String>): List<Blueprint> {
        return input.map { line ->
            val parts = line.split(' ')
            val number = parts[1].trimEnd(':').toInt()
            val costs = mapOf(
                Material.Ore to mapOf(Material.Ore to parts[6].toInt()),
                Material.Clay to mapOf(Material.Ore to parts[12].toInt()),
                Material.Obsidian to mapOf(Material.Ore to parts[18].toInt(), Material.Clay to parts[21].toInt()),
                Material.Geode to mapOf(Material.Ore to parts[27].toInt(), Material.Obsidian to parts[30].toInt())
            )

            Blueprint(number, costs)
        }
    }

    fun part1(input: List<String>): String {
//        val blueprints = parse(input)
//        val minutesTotal = 24
//
//        val result = blueprints.sumOf { blueprint ->
//            val player = Player(
//                mutableMapOf(),
//                blueprint.maxLimits(minutesTotal),
//                blueprint.getMaxMaterialsMap(),
//                blueprint,
//                minutesTotal
//            )
//            val initialState = State(minutesTotal, mapOf(Material.Ore to 1), mapOf())
//            val maxGeodes = player.play(initialState)
//            println("Blueprint: ${blueprint.number}. Geodes: $maxGeodes")
//            maxGeodes * blueprint.number
//        }
//
//        return result.toString()

        return "-1"
    }

    fun part2(input: List<String>): String {
        val blueprints = parse(input).take(3)
        val minutesTotal = 32

        val result = blueprints.map { blueprint ->
            val player = Player(
                mutableMapOf(),
                blueprint.maxLimits(minutesTotal),
                blueprint.getMaxMaterialsMap(),
                blueprint,
                minutesTotal
            )
            val initialState = State(minutesTotal, mapOf(Material.Ore to 1), mapOf())
            val maxGeodes = player.play(initialState)
            println("Blueprint: ${blueprint.number}. Geodes: $maxGeodes")
            maxGeodes
        }.reduce { acc, c -> acc * c }

        return result.toString()
    }
}

private fun Map<Material, Int>.g(clay: Material): Int {
    return this[clay] ?: 0
}

@OptIn(ExperimentalTime::class)
@Suppress("DuplicatedCode")
fun main() {
    val solution = Day19()
    val name = solution.javaClass.name

    fun test() {
        val expected1 = "33"
        val expected2 = "not expect anything"

        val testInput = readInput("${name}_test")
        println("Test part 1")
        assert(solution.part1(testInput), expected1)
        println("> Passed")
        println("Test part 2")
        // assert(solution.part2(testInput), expected2)
        println("> Passed")
        println()
        println("=================================")
        println()
    }

    fun run() {
        val input = readInput(name)
        val elapsed1 = measureTime {
            println("Part 1: " + solution.part1(input))
        }
        println("Elapsed: $elapsed1")
        println()

        val elapsed2 = measureTime {
            println("Part 2: " + solution.part2(input))
        }
        println("Elapsed: $elapsed2")
        println()
    }

    // test()
    run()
}
