import java.math.BigInteger
import kotlin.math.abs

fun main() {
    fun fallIn(r: Int, bound1: Int, bound2: Int) = (r in (bound1 + 1)..<bound2) || (r in (bound2 + 1)..<bound1)
    fun part1(input: List<String>): Int {
        val expandedRows = input.withIndex().filter { it.value.all { it == '.' } }.map { it.index }
        val expandedCols = (0..<input[0].length).filter { ind -> input.all { it[ind] == '.' } }
        val galaxies = input.withIndex().map { row -> row.value.withIndex().filter { it.value == '#' }.map { Pair(row.index, it.index) } }
            .flatten()
//        println(galaxies)
//        println(expandedCols)
//        println(expandedCols.count { fallIn(it, galaxies[0].second, galaxies[1].second) })
        return galaxies.map { src -> galaxies.map { dst -> abs(src.first - dst.first) + abs(src.second - dst.second) +
                expandedCols.count { fallIn(it, src.second, dst.second) } + expandedRows.count { fallIn(it, src.first, dst.first) }
        } }
            .flatten().sum() / 2
    }

    fun part2(input: List<String>, ratio: Int = 1000000): BigInteger {
        val expandRatio = (ratio - 1).toBigInteger()
        val expandedRows = input.withIndex().filter { it.value.all { it == '.' } }.map { it.index }
        val expandedCols = (0..<input[0].length).filter { ind -> input.all { it[ind] == '.' } }
        val galaxies = input.withIndex().map { row -> row.value.withIndex().filter { it.value == '#' }.map { Pair(row.index, it.index) } }
            .flatten()
        return galaxies.map { src -> galaxies.map { dst -> abs(src.first - dst.first).toBigInteger() + abs(src.second - dst.second).toBigInteger() +
                expandedCols.count { fallIn(it, src.second, dst.second) }.toBigInteger() * expandRatio + expandedRows.count { fallIn(it, src.first, dst.first) }.toBigInteger() * expandRatio
        } }
            .flatten().fold(0.toBigInteger()) { acc, integer -> acc + integer } / 2.toBigInteger()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 374)
    check(part2(testInput, 10) == 1030.toBigInteger())
    check(part2(testInput, 100) == 8410.toBigInteger())

    val input = readInput("Day11")
    part1(input).println()
    part2(input).println()
}
