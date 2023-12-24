import java.math.BigInteger
import kotlin.math.absoluteValue
import kotlin.math.log

fun main() {
    fun part1(input: List<String>): BigInteger {
        val xRange = input.size
        val yRange = input[0].length
        var paths = listOf(listOf(Pair(0, 1)))
        fun reachEnd(path: List<Pair<Int, Int>>) = path.last() == Pair(xRange - 1, yRange - 2)
        fun getNext(path: List<Pair<Int, Int>>) = path.last().let { loc ->
            when (input[loc.first][loc.second]) {
                '>' -> listOf(Pair(loc.first, loc.second + 1))
                '<' -> listOf(Pair(loc.first, loc.second - 1))
                '^' -> listOf(Pair(loc.first - 1, loc.second))
                'v' -> listOf(Pair(loc.first + 1, loc.second))
                else -> listOf(
                    Pair(loc.first, loc.second + 1),
                    Pair(loc.first, loc.second - 1),
                    Pair(loc.first - 1, loc.second),
                    Pair(loc.first + 1, loc.second)
                )
                    .filter { try { input[it.first][it.second] != '#' } catch (_: IndexOutOfBoundsException) { false } }
            }.filter { it !in path }
        }
        val finished = mutableSetOf<List<Pair<Int, Int>>>()
        while (paths.isNotEmpty()) {
            val (newFinished, notFinished) = paths.flatMap { path -> getNext(path).map { path + it } }
                .partition { reachEnd(it) }
//            println(newFinished)
//            println(notFinished)
            finished += newFinished
            paths = notFinished
        }
//        println()
        return finished.maxOfOrNull { it.size - 1 }!!.toBigInteger()
    }

    fun part2(input: List<String>): BigInteger {
        val xRange = input.size
        val yRange = input[0].length
        val enter = Pair(0, 1)
        val exit = Pair(xRange - 1, yRange - 2)
        val longestDistanceBetweenNodes = mutableMapOf<Pair<Pair<Int, Int>, Pair<Int, Int>>, Int>()
        fun getNext(path: List<Pair<Int, Int>>) = path.last().let { loc ->
            listOf(
                    Pair(loc.first, loc.second + 1),
                    Pair(loc.first, loc.second - 1),
                    Pair(loc.first - 1, loc.second),
                    Pair(loc.first + 1, loc.second)
                )
                    .filter { try { input[it.first][it.second] != '#' } catch (_: IndexOutOfBoundsException) { false } }
                    .filter { it !in path }
        }

        fun getLongestDistanceFrom(node: Pair<Int, Int>): MutableSet<Pair<Int, Int>> {
            var workList = getNext(listOf(node)).map { listOf(node, it) }.toSet()
            val newNodes = mutableSetOf<Pair<Int, Int>>()
            while (workList.isNotEmpty()) {
                for (pathToExit in workList.filter { it.last() == exit }) {
                    val distance = pathToExit.size - 1
                    if (longestDistanceBetweenNodes[Pair(node, exit)] == null ||
                        longestDistanceBetweenNodes[Pair(node, exit)]!! < distance
                    )
                        longestDistanceBetweenNodes[Pair(node, exit)] = distance
                    longestDistanceBetweenNodes[Pair(exit, node)] = distance
                    longestDistanceBetweenNodes[Pair(node, exit)] = distance
                }
                workList = workList.filter { (it.last() != enter || it.last() != exit) }
                    .mapNotNull { path ->
                    val nexts = getNext(path)
                    if (nexts.size == 1) path + nexts[0] else {
                        val newNode = path.last()
                        val distance = path.size - 1
                        if (longestDistanceBetweenNodes[Pair(node, newNode)] == null ||
                            longestDistanceBetweenNodes[Pair(node, newNode)]!! < distance
                        ) {
                            longestDistanceBetweenNodes[Pair(node, newNode)] = distance
                            longestDistanceBetweenNodes[Pair(newNode, node)] = distance
                            newNodes += newNode
                        }
                        null
                    }
                }.toSet()
            }
            return newNodes
        }

        var newNodes = getLongestDistanceFrom(enter).toSet()
        while (newNodes.isNotEmpty())
            newNodes = newNodes.flatMap { getLongestDistanceFrom(it) }.toSet()
        val reachables = longestDistanceBetweenNodes.map { (k, _) -> Pair(k.first, k.second) }
            .groupBy { it.first }
            .mapValues { listOfPointPair -> listOfPointPair.value.map { it.second } }

        println(longestDistanceBetweenNodes)
        var paths = listOf(listOf(Pair(0, 1)))
        val finished = mutableSetOf<List<Pair<Int, Int>>>()
        while (paths.isNotEmpty()) {
            val (newFinished, notFinished) = paths.flatMap { path -> reachables[path.last()]!!
                .mapNotNull { if (it !in path) path + it else null } }
                .partition { it.last() == exit }
            finished += newFinished
            paths = notFinished

        }
            println(finished.filter { it.last() == exit }
                .map { path -> path.zip(path.drop(1)).sumOf { longestDistanceBetweenNodes[it]!! } })
        return finished.filter { it.last() == exit }
            .map { path -> path.zip(path.drop(1)).sumOf { longestDistanceBetweenNodes[it]!! } }
            .max()
            .toBigInteger()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day23_test")
    check(part1(testInput) == 94.toBigInteger())
    check(part2(testInput) == 154.toBigInteger())

    val input = readInput("Day23")
    part1(input).println()
    part2(input).println()
}
