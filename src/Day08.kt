import kotlin.math.max

class Tree(val value: Int, var visible: Boolean)

fun main() {
    fun parse(input: List<String>): List<List<Tree>> {
        return input.map { line ->
            line.toCharArray().map {
                Tree(it.digitToInt(), false)
            }
        }
    }

    fun findVisible(trees: List<List<Tree>>): Int {
        val size = trees.size
        val lastIdx = size - 1
        for (i in 0..lastIdx) {
            var maxLeft = -1
            var maxRight = -1
            for (j in 0..lastIdx) {
                val cellLeft = trees[i][j]
                if (cellLeft.value > maxLeft) {
                    cellLeft.visible = true
                    maxLeft = cellLeft.value
                }

                val cellRight = trees[i][lastIdx - j]
                if (cellRight.value > maxRight) {
                    cellRight.visible = true
                    maxRight = cellRight.value
                }
            }
        }

        for (i in 0..lastIdx) {
            var maxTop = -1
            var maxBottom = -1
            for (j in 0..lastIdx) {
                val cellTop = trees[j][i]
                if (cellTop.value > maxTop) {
                    cellTop.visible = true
                    maxTop = cellTop.value
                }

                val cellBottom = trees[lastIdx - j][i]
                if (cellBottom.value > maxBottom) {
                    cellBottom.visible = true
                    maxBottom = cellBottom.value
                }
            }
        }

        return trees.sumOf { x -> x.count { y -> y.visible } }
    }

    fun findMax(center: Int, range: IntProgression, getter: (x: Int) -> Tree): Int {
        var count = 0
        for (i in range) {
            val cell = getter(i)
            if (cell.value >= center) {
                return count + 1
            }

            ++count
        }
        return count
    }

    fun findScenic(trees: List<List<Tree>>): Int {
        val size = trees.size
        val lastIdx = size - 1
        var maxScenic = -1
        for (i in 0..lastIdx) {
            for (j in 0..lastIdx) {
                val center = trees[i][j].value

                val left = findMax(center, j - 1 downTo 0) { x -> trees[i][x] }
                val right = findMax(center, j + 1..lastIdx) { x -> trees[i][x] }
                val top = findMax(center, i - 1 downTo 0) { x -> trees[x][j] }
                val bottom = findMax(center, i + 1..lastIdx) { x -> trees[x][j] }

                val scenic = top * left * bottom * right
                maxScenic = max(scenic, maxScenic)
            }
        }

        return maxScenic
    }

    fun part1(input: List<String>): Int {
        val trees = parse(input)
        return findVisible(trees)
    }

    fun part2(input: List<String>): Int {
        val trees = parse(input)
        return findScenic(trees)
    }

    val testInput = readInput("Day08_test")
    assert(part1(testInput), 21)
    assert(part2(testInput), 8)

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}
