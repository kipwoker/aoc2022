import java.util.Comparator

fun main() {
    class TreeNode(
        val value: Int?,
        val parent: TreeNode?,
        val str: String?,
        var children: MutableList<TreeNode> = mutableListOf()
    )

    class TreeNodeComparator(val f: (o1: TreeNode?, o2: TreeNode?) -> Int) : Comparator<TreeNode> {
        override fun compare(o1: TreeNode?, o2: TreeNode?): Int {
            return f(o1, o2)
        }
    }

    fun parseTree(input: String): TreeNode {
        val root = TreeNode(null, null, input)

        var currentNode = root
        var level = 0

        val size = input.length
        var i = 0
        while (i < size) {
            var c = input[i]
            when (c) {
                '[' -> {
                    val child = TreeNode(null, currentNode, null)
                    currentNode.children.add(child)
                    currentNode = child
                    level++
                }
                ']' -> {
                    currentNode = currentNode.parent!!
                    level--
                }
                ',' -> {
                }
                in '0'..'9' -> {
                    var number = ""
                    while (c in '0'..'9') {
                        number += c
                        ++i
                        c = input[i]
                    }
                    --i
                    val child = TreeNode(number.toInt(), currentNode, null)
                    currentNode.children.add(child)
                }
            }
            ++i
        }

        return root
    }

    fun parse(input: List<String>): List<Pair<TreeNode, TreeNode>> {
        return input
            .chunked(3)
            .map { chunk ->
                parseTree(chunk[0]) to parseTree(chunk[1])
            }
    }

    fun compare(left: Int, right: Int): Boolean? {
        if (left == right) {
            return null
        }

        return left < right
    }

    fun compare(left: TreeNode, right: TreeNode): Boolean? {
        if (left.value != null && right.value != null) {
            return compare(left.value, right.value)
        }

        if (left.value == null && right.value == null) {
            var i = 0
            while (i < left.children.size || i < right.children.size) {
                if (left.children.size < right.children.size && i >= left.children.size) {
                    return true
                }

                if (left.children.size > right.children.size && i >= right.children.size) {
                    return false
                }

                val leftChild = left.children[i]
                val rightChild = right.children[i]
                val result = compare(leftChild, rightChild)
                if (result != null) {
                    return result
                }

                ++i
            }
        }

        if (left.value != null) {
            val subNode = TreeNode(null, left.parent, null)
            val subChild = TreeNode(left.value, subNode, null)
            subNode.children.add(subChild)
            return compare(subNode, right)
        }

        if (right.value != null) {
            val subNode = TreeNode(null, right.parent, null)
            val subChild = TreeNode(right.value, subNode, null)
            subNode.children.add(subChild)
            return compare(left, subNode)
        }

        return null
    }

    fun part1(input: List<String>): Int {
        val pairs = parse(input)
        return pairs
            .map { item -> compare(item.first, item.second) }
            .mapIndexed { index, r -> if (r == true) index + 1 else 0 }
            .sum()
    }

    fun part2(input: List<String>): Int {
        val input1 = input.toMutableList()
        input1.add("\n")
        input1.add("[[2]]")
        input1.add("[[6]]")
        input1.add("\n")
        val pairs = parse(input1)
        val comparator = TreeNodeComparator { o1, o2 ->
            when (compare(o1!!, o2!!)) {
                true -> -1
                false -> 1
                else -> 0
            }
        }

        return pairs
            .flatMap { item -> listOf(item.first, item.second) }
            .sortedWith(comparator)
            .mapIndexed { index, treeNode ->
                if (treeNode.str == "[[2]]" || treeNode.str == "[[6]]") {
                    index + 1
                } else {
                    1
                }
            }
            .reduce { acc, i -> acc * i }
    }

    val testInput = readInput("Day13_test")
    assert(part1(testInput), 13)
    assert(part2(testInput), 140)

    val input = readInput("Day13")
    println(part1(input))
    println(part2(input))
}
