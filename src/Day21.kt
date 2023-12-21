import java.lang.IndexOutOfBoundsException
import java.math.BigInteger

fun main() {
    fun part1(input: List<String>, steps: Int =6): BigInteger {
        val grid = input.flatMapIndexed { x, line -> line.mapIndexed { y, _ -> Pair(Pair(x, y), -2)} }.toMap().toMutableMap()
        val start = input.withIndex().firstNotNullOf { (ind, line) ->
            line.withIndex().firstOrNull { it.value == 'S' }
                ?.let { Pair(ind, it.index) }
        }
        println(start)
        val nexts = input.withIndex().map { (x, line) ->
            line.withIndex().map { (y, c) ->
                listOf(Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1))
                    .filter { try { input[x + it.first][y + it.second] != '#'} catch (e: IndexOutOfBoundsException) { false } }
            }
        }
        grid[start] = -1
        for (s in 0..<steps) {
            for ((inds, _) in grid.filter { (_, last) -> last == s - 1 }) {
                for (dir in nexts[inds.first][inds.second]) {
                    grid[Pair(inds.first + dir.first, inds.second + dir.second)] = s
                }
            }
//            println(grid)
        }
        println(grid)
        return grid.filter { it.value == steps - 1 }.size.toBigInteger()
    }

    fun part2(input: List<String>, steps: Int): BigInteger {
        val grid = input.flatMapIndexed { x, line -> line.mapIndexed { y, _ -> Pair(
            Pair(x, y), Pair(
                -2, setOf(
                    Pair(x, y))
            )) } }.toMap().toMutableMap()
        val start = input.withIndex().firstNotNullOf { (ind, line) ->
            line.withIndex().firstOrNull { it.value == 'S' }
                ?.let { Pair(ind, it.index) }
        }
        println(start)
        val xrange = input.size
        val yrange = input[0].length
        val nexts = input.withIndex().map { (x, line) ->
            line.withIndex().map { (y, c) ->
                listOf(Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1))
                    .filter { input[(x + it.first + xrange) % xrange][(y + it.second + yrange) % yrange] != '#' }
            }
        }
        grid[start] = Pair(-1, setOf(start))
        val a = mutableMapOf(Pair(-1, (-1).toBigInteger()))
        val b = mutableMapOf(Pair(1, mutableMapOf(Pair(1, (1).toBigInteger()))))
        var rule = 0
        for (s in 0..<steps) {
            for ((inds, record) in grid.filter { (_, record) -> record.first == s - 1 }) {
                for (dir in nexts[inds.first][inds.second]) {
                    val newInds = Pair((inds.first + dir.first + xrange) % xrange, (inds.second + dir.second + yrange) % yrange)
                    val (lastStep, list) = grid[newInds]!!
                    if (lastStep == s)
                        grid[newInds] = Pair(s, list +
                        record.second.map { Pair(it.first + dir.first, it.second + dir.second) })
                    else
                        grid[newInds] = Pair(s,
                                record.second.map { Pair(it.first + dir.first, it.second + dir.second) }.toSet())
                }
            }
//            println(grid)
            println("$s")
            a[s] = grid.filter { it.value.first == s }.values.sumOf { it.second.size.toBigInteger() }
            if (s == (3 * xrange) || (s > 10 * xrange && s % (3 * xrange) == 0)) {
                for (rule in 0..s / 2 - 2) {
                    b[rule] = mutableMapOf()
                    for (ind in (2 * rule)..s) {
                        b[rule]!![ind] = a[ind]!! - (2).toBigInteger() * a[ind - rule]!! + a[ind - 2 * rule]!!
                    }
                }
                val c = b.filter { gram -> gram.key != 0 && gram.value.values.let { graph -> graph.count { it == graph.last() } > graph.size * 0.7 } }
                if (c.isNotEmpty()) {
                    rule = c.keys.first()
                    break
                }
//            println(a[s]!! - (2).toBigInteger() * a[s - xrange - 1]!! + a[s - 2 * (xrange + 1)]!!)
            }
        }
//        println(grid)
        if (rule != 0) {
            val init = (steps - 1) % rule + (a.keys.last() - rule * 2) / rule * rule
            val coef0 = a[init]!!
            val coef2 = b[rule]!![b[rule]!!.keys.last()]!! / (2).toBigInteger()
            val coef1 = a[init + rule]!! - coef0 - coef2
            val param = ((steps - 1 - init) / rule).toBigInteger()
            return coef2 * param * param + coef1 * param + coef0
        }
        return grid.filter { it.value.first == steps - 1 }.values.sumOf { it.second.size.toBigInteger() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day21_test")
    check(part1(testInput) == 16.toBigInteger())
    check(part2(testInput, 6) == 16.toBigInteger())
    check(part2(testInput, 10) == 50.toBigInteger())
    check(part2(testInput, 50) == 1594.toBigInteger())
    check(part2(testInput, 100) == 6536.toBigInteger())
//    part2(readInput("Day21"), 800)
    check(part2(testInput, 500) == 167004.toBigInteger())
    check(part2(testInput, 1000) == 668697.toBigInteger())
    check(part2(testInput, 5000) == 16733044.toBigInteger())


    val input = readInput("Day21")
    part1(input, 64).println()
    part2(input, 26501365).println()
}
