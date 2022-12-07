import java.math.BigInteger

enum class NodeType { File, Dir }
class Node(val parent: Node?, val children: MutableMap<String, Node>, val alias: String, val size: BigInteger?, val type: NodeType) {
    var totalSize = BigInteger.ZERO

    fun getRoot(): Node {
        var currentNode = this
        while (currentNode.parent != null) {
            currentNode = currentNode.parent!!
        }

        return currentNode
    }

    fun calculateTotalSize(): BigInteger {
        if (type == NodeType.File) {
            totalSize = size!!
            return totalSize
        }

        totalSize = children.values.sumOf { it.calculateTotalSize() }
        return totalSize
    }

    fun sumBy(threshold: BigInteger): BigInteger {
        var result = BigInteger.ZERO

        if (type == NodeType.Dir && totalSize <= threshold) {
            result += totalSize
        }

        return result + children.values.sumOf { it.sumBy(threshold) }
    }

    fun printNode() {
        printNode(0)
    }

    fun printNode(indentLevel: Int) {
        val indent = (0..indentLevel).map { ' ' }.joinToString("")
        val typeMsg = if (type == NodeType.Dir) "dir" else "file, size=$size"
        println("$indent- $alias ($typeMsg)")
        children.values.forEach { it.printNode(indentLevel + 2) }
    }

    fun getDirSizes(): List<BigInteger> {
        val result = mutableListOf(totalSize)
        result.addAll(children.values.flatMap { it.getDirSizes() })
        return result
    }
}

fun main() {
    fun ensureDirExists(node: Node, alias: String) {
        if (node.children[alias] == null) {
            node.children[alias] = Node(node, mutableMapOf(), alias, null, NodeType.Dir)
        }
    }

    fun ensureFileExists(node: Node, alias: String, size: BigInteger) {
        if (node.children[alias] == null) {
            node.children[alias] = Node(node, mutableMapOf(), alias, size, NodeType.File)
        }
    }

    fun parse(input: List<String>): Node {
        val root = Node(null, mutableMapOf(), "/", null, NodeType.Dir)
        var pointer = root
        var lineIdx = 0
        val totalSize = input.size
        while (lineIdx < totalSize) {
            val line = input[lineIdx]

            println("Command: $line")
            if (line == "$ cd /") {
                pointer = pointer.getRoot()
                ++lineIdx
            } else if (line == "$ cd ..") {
                pointer = pointer.parent!!
                ++lineIdx
            } else if (line.startsWith("$ cd")) {
                val dirAlias = line.split(' ')[2]
                ensureDirExists(pointer, dirAlias)
                pointer = pointer.children[dirAlias]!!
                ++lineIdx
            } else if (line == "$ ls") {
                var listIdx = lineIdx + 1
                while (listIdx < totalSize && !input[listIdx].startsWith("$")) {
                    val node = input[listIdx]
                    if (node.startsWith("dir")) {
                        val dirAlias = node.split(' ')[1]
                        ensureDirExists(pointer, dirAlias)
                    } else {
                        val parts = node.split(' ')
                        val size = parts[0].toBigInteger()
                        val fileAlias = parts[1]
                        ensureFileExists(pointer, fileAlias, size)
                    }

                    ++listIdx
                }

                lineIdx = listIdx
            } else {
                println("Unexpected line: $line")
                return root
            }

            println("Root")
            root.printNode()
            println()
        }

        return root
    }

    fun part1(input: List<String>): BigInteger {
        val root = parse(input)
        root.calculateTotalSize()
        return root.sumBy(BigInteger.valueOf(100000))
    }

    fun part2(input: List<String>): BigInteger {
        val root = parse(input)
        root.calculateTotalSize()
        val threshold = root.totalSize - BigInteger.valueOf(40000000)

        return root.getDirSizes().filter { it >= threshold }.min()
    }

    val testInput = readInput("Day07_test")
    assert(part1(testInput), BigInteger.valueOf(95437L))

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}
