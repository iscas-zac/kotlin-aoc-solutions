fun main() {

    fun getPaths(input: List<String>): MutableList<List<Pair<Int, Int>>> {
        val sloc = input.withIndex().find { line -> line.value.chars().anyMatch { it == 'S'.code } }!!
            .let { loc -> Pair(loc.index, loc.value.withIndex().find { it.value == 'S' }!!.index) }
        //        print(sloc)

        val width = input.first().length
        val height = input.size

        fun getConnections(end: Pair<Int, Int>): List<Pair<Int, Int>> {
            return when (input[end.first][end.second]) {
                '|' -> listOf(
                    Pair(end.first - 1, end.second),
                    Pair(end.first + 1, end.second)
                ) // is a vertical pipe connecting north and south.
                '-' -> listOf(
                    Pair(end.first, end.second - 1),
                    Pair(end.first, end.second + 1)
                ) // is a horizontal pipe connecting east and west.
                'L' -> listOf(
                    Pair(end.first - 1, end.second),
                    Pair(end.first, end.second + 1)
                ) // is a 90-degree bend connecting north and east.
                'J' -> listOf(
                    Pair(end.first - 1, end.second),
                    Pair(end.first, end.second - 1)
                ) // is a 90-degree bend connecting north and west.
                '7' -> listOf(
                    Pair(end.first + 1, end.second),
                    Pair(end.first, end.second - 1)
                ) // is a 90-degree bend connecting south and west.
                'F' -> listOf(
                    Pair(end.first + 1, end.second),
                    Pair(end.first, end.second + 1)
                ) // is a 90-degree bend connecting south and east.
                '.' -> listOf() // is ground; there is no pipe in this tile.
                'S' -> listOf(
                    Pair(end.first - 1, end.second),
                    Pair(end.first + 1, end.second),
                    Pair(end.first, end.second - 1),
                    Pair(end.first, end.second + 1)
                )

                else -> listOf()
            }
        }

        fun findNextNode(path: List<Pair<Int, Int>>): List<List<Pair<Int, Int>>> {
            val end = path[path.size - 1]

            return getConnections(end).filter { next ->
                !(next in path || next.first < 0 || next.second < 0 || next.first >= height || next.second >= width)
                        && end in getConnections(next)
            }.map { path + it }
        }

        var worklist = listOf(listOf(sloc))
        val finishlist = mutableListOf(listOf(sloc))
        while (worklist.isNotEmpty()) {
            val item = worklist[0]
            val paths = findNextNode(item)
    //            println("path:" + paths)
    //            println("neighbor:" + getConnections(item.last()))
            if (paths.isNotEmpty())
                worklist = worklist.drop(1) + paths
            else
                worklist = worklist.drop(1)
            if (item.size > 2 && sloc in getConnections(item.last()))
                finishlist += item
    //            println(worklist)
        }

        //        print(finishlist)
        return finishlist
    }

    fun part1(input: List<String>): Int {
        val finishlist = getPaths(input)

        return finishlist.maxOf { it.size / 2 }
    }

    fun part2(input: List<String>): Int {
        val path = getPaths(input)[1]

        val colorMap = (0..input.size).map { if (it == 0 || it == input.size)
            (0..input[0].length).map { 0 }.toMutableList()
        else (0..input[0].length).map { if (it == 0 || it == input[0].length) 0 else 1 }.toMutableList() }

        fun findNexts(node: Pair<Int, Int>): List<Pair<Int, Int>> {
            fun isConnected(p1: Pair<Int, Int>, p2: Pair<Int, Int>): Boolean {
                if (p1.first < 0 || p1.first >= input.size || p1.second < 0 || p1.second > input[0].length ||
                    p2.first < 0 || p2.first >= input.size || p2.second < 0 || p2.second > input[0].length)
                    return false
                if (p1 !in path || p2 !in path) return false
                if (p1.first == p2.first && p1.second + 1 == p2.second)
                    return input[p1.first][p1.second] in listOf('-', 'F', 'L', 'S') && input[p2.first][p2.second] in listOf('-', '7', 'J', 'S')
                if (p1.first + 1 == p2.first && p1.second == p2.second)
                    return input[p1.first][p1.second] in listOf('|', 'F', '7', 'S') && input[p2.first][p2.second] in listOf('|', 'J', 'L', 'S')
                assert(false)
                return true
            }

            val nexts = mutableListOf<Pair<Int, Int>>()

            if (!isConnected(Pair(node.first - 1, node.second - 1), Pair(node.first - 1, node.second)))
                nexts.add(Pair(node.first - 1, node.second))
            if (!isConnected(Pair(node.first, node.second - 1), Pair(node.first, node.second)))
                nexts.add(Pair(node.first + 1, node.second))
            if (!isConnected(Pair(node.first - 1, node.second - 1), Pair(node.first, node.second - 1)))
                nexts.add(Pair(node.first, node.second - 1))
            if (!isConnected(Pair(node.first - 1, node.second), Pair(node.first, node.second)))
                nexts.add(Pair(node.first, node.second + 1))
//            println(nexts)
            return nexts.filterNot { it.first < 0 || it.second < 0 || it.first > input.size || it.second > input[0].length }
        }

        var worklist = (0..input.size).map { Pair(it, 0) } + (0..input.size).map { Pair(it, input[0].length) } +
                (0..input[0].length).map { Pair(0, it) } + (0..input[0].length).map { Pair(input.size, it) }
        while (worklist.isNotEmpty()) {
            val item = worklist[0]
//            println(item)
            val nexts = findNexts(item)
            if (nexts.isNotEmpty())
                worklist = worklist.drop(1) + nexts.filter { colorMap[it.first][it.second] != 0 }
            else
                worklist = worklist.drop(1)
            nexts.forEach { colorMap[it.first][it.second] = 0 }
//            println(colorMap.joinToString("\n"))
//            println("")
        }

        return (1..<input.size-1).flatMap { row -> (1..<input[0].length-1).map { Pair(row, it) } }
            .filter { (r, c) -> colorMap[r][c] == 1 && colorMap[r + 1][c] == 1 && colorMap[r][c + 1] == 1 && colorMap[r + 1][c + 1] == 1 }
            .count()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 4)
    check(part2(readInput("Day10_test2")) == 10)

    val input = readInput("Day10")
    part1(input).println()
    part2(input).println()
}
