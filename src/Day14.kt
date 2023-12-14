import java.math.BigInteger

fun main() {
    fun findLoc(row: String, loc: Int): Int {
        val rowOfInterest = row.slice(0..loc)
        val cube = rowOfInterest.lastIndexOf('#')
        return if (cube == -1) rowOfInterest.count { it == 'O' }
        else cube + rowOfInterest.slice(cube..loc).count { it == 'O' } + 1
    }
    fun part1(input: List<String>): BigInteger {
        val verts = input[0].indices.map { ind -> input.map { it[ind] }.joinToString("") }

        println(verts[2].withIndex().filter { it.value == 'O' }.map { it.index }.map { input.size + 1 - findLoc(verts[2], it) })
        return verts.sumOf { row -> row.withIndex().filter { it.value == 'O' }.map { it.index }.sumOf { (input.size + 1 - findLoc(row, it)).toBigInteger() } }
    }
    
    fun findIntervals(row: String) = row.split('#').map { interval -> interval.count { it == 'O' } }

    fun rotateAndTilt(
        lines: List<String>
    ): List<String> {
        val rot = lines[0].indices.map { ind -> lines.map { it[ind] }.joinToString("") }
        return rot.map { row ->
            val newLoc =
                row.withIndex().filter { it.value == 'O' }.map { it.index }
                    .map { findLoc(row, it) }
            row.replace('O', '.')
                .mapIndexed { index, c -> if (index + 1 in newLoc) 'O' else c }.joinToString("")
        }
    }

    fun List<String>.rotate() = this[0].indices.map { ind -> this.asReversed().map { it[ind] }.joinToString("") }
    fun List<String>.rotate(times: Int): List<String> {
        var temp = this
        (0..<times).forEach { _ -> temp = temp.rotate() }
        return temp
    }

    fun part2(input: List<String>): BigInteger {
        var lines = input
//        lines = listOf(".N.", "W.E", ".S.")
//        println(lines[0].indices.joinToString("\n") { ind -> lines.map { it[ind] }.joinToString("") })
        var newLines = rotateAndTilt(lines)
        val transMaps = mutableListOf(mutableMapOf(Pair(lines.map { findIntervals(it) }, newLines.map { findIntervals(it) })))
        lines = newLines
//        println(lines[0].indices.joinToString("\n") { ind -> lines.map { it[ind] }.joinToString("") })
        newLines = rotateAndTilt(lines)
        transMaps += mutableMapOf(Pair(lines.map { findIntervals(it) }, newLines.map { findIntervals(it) }))
        lines = newLines
//        println(lines[0].indices.joinToString("\n") { ind -> lines.reversed().map { it[ind] }.joinToString("") })
        newLines = rotateAndTilt(lines.reversed())
        transMaps += mutableMapOf(Pair(lines.map { findIntervals(it) }, newLines.map { findIntervals(it) }))
        lines = newLines
//        println(lines[0].indices.joinToString("\n") { ind -> lines.reversed().map { it[ind] }.joinToString("") })
        newLines = rotateAndTilt(lines.reversed())
        transMaps += mutableMapOf(Pair(lines.map { findIntervals(it) }, newLines.map { findIntervals(it) }))
        var summary = lines.map { findIntervals(it) }
        var newSummary: List<List<Int>>

        var xx: BigInteger = 0.toBigInteger()
        var loop = 0.toBigInteger()
        var start = Triple(0, xx, listOf<List<Int>>())
        xx++
        var a = 0
        lines = newLines
        outer@ while(xx < 1000000000.toBigInteger()) {
            for (transMap in transMaps) {

                if (transMap.containsKey(summary)) {
                    summary = transMap[summary]!!
                }
                else {
                    newLines = rotateAndTilt(lines.reversed())
                    newSummary = newLines.map { findIntervals(it) }
                    transMap[summary] = newSummary
                    lines = newLines
                    summary = newSummary
                }
                println(lines.rotate(5 - a).joinToString("\n"))
                a = (a + 1) % 4
                println()
            }
            if (transMaps[0].containsKey(summary))
                if (summary == start.third) {
                    if (loop == 0.toBigInteger()) loop = xx - start.second
                } else if (start.third.isEmpty()) start = Triple(0, xx, summary)
//            println(lines.joinToString("\n"))
            if (loop != 0.toBigInteger() && xx.mod(loop) == (1000000000-1).toBigInteger().mod(loop)) break@outer
            print("\r$xx")
            xx++
        }
//        var a = 1
//        for (i in 0..1000000000) a += 1
        return summary.withIndex().sumOf { (ind, row) -> (ind + 1) * row.sum() }.toBigInteger()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 136.toBigInteger())
    check(part2(testInput) == 64.toBigInteger())

    val input = readInput("Day14")
    part1(input).println()
    part2(input).println()
}
