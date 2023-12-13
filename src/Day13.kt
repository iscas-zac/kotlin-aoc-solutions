import java.math.BigInteger

fun main() {
    fun getNumVertical(grid: List<String>): Int {
        fun isMirror(sentence: String, loc: Int): Boolean {
            return sentence.slice(0..loc)
                .withIndex()
                .all { (ind, value) -> loc * 2 + 1 - ind >= sentence.length || sentence[loc*2+1-ind] == value }
        }
        return grid[0].indices.toList().dropLast(1).find { loc -> grid.all { isMirror(it, loc) } } ?: -1
    }

    fun getNumHorizontal(grid: List<String>): Int {
        return grid.indices.toList().dropLast(1).find { line ->
            grid
                .slice(0..line)
                .withIndex()
                .all { (ind, value) -> line * 2 + 1 - ind >= grid.size || grid[line * 2 + 1 - ind] == value }
        }
            ?: -1
    }

    fun part1(input: List<String>): BigInteger {
        val empties = listOf(-1) + input.withIndex().filter { it.value.isEmpty() }.map { it.index } + input.size
        val grids = empties.zipWithNext { begin, end -> begin+1..<end }.map { input.slice(it) }

        val test = """
.##.#.#.#..
.##.#.#.#..
#.####.#.#.
#.##.....##
...#.##.#..
.##.##.#..#
...##.##.#.
#.....#.#.#
#.....###.#
...##.##.#.
.##.##.#..#""".lines().drop(1)
//        getNumVertical()
//        println(getNumHorizontal(test))
//        println(grids.map { getNumVertical(it) + 1 + 100 * (getNumHorizontal(it) + 1) })
        return grids.sumOf { getNumVertical(it) + 1 + 100 * (getNumHorizontal(it) + 1) }.toBigInteger()
    }

    fun getNumVerticalDiffBy1(grid: List<String>): Int {
        fun getMirrorDiff(sentence: String, loc: Int): Int {
            return sentence.slice(0..loc)
                .withIndex()
                .count { (ind, value) -> !(loc * 2 + 1 - ind >= sentence.length || sentence[loc*2+1-ind] == value) }
        }
        return grid[0].indices.toList().dropLast(1).find { loc -> grid.sumOf { getMirrorDiff(it, loc) } == 1 } ?: -1
    }

    fun getStringDiff(str1: String, str2: String) = str1.zip(str2).count { (c1, c2) -> c1 != c2 }
    fun getNumHorizontalDiffBy1(grid: List<String>): Int {
        return grid.indices.toList().dropLast(1).find { line ->
            grid
                .slice(0..line)
                .withIndex()
                .sumOf { (ind, value) -> if (line * 2 + 1 - ind >= grid.size) 0
                        else getStringDiff(grid[line * 2 + 1 - ind], value)
                } == 1
        }
            ?: -1
    }

    fun part2(input: List<String>): BigInteger {
        val empties = listOf(-1) + input.withIndex().filter { it.value.isEmpty() }.map { it.index } + input.size
        val grids = empties.zipWithNext { begin, end -> begin+1..<end }.map { input.slice(it) }
        println(getNumHorizontalDiffBy1(grids[1]))

        return grids.sumOf { getNumVerticalDiffBy1(it) + 1 + 100 * (getNumHorizontalDiffBy1(it) + 1) }.toBigInteger()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 405.toBigInteger())
    check(part2(testInput) == 400.toBigInteger())

    val input = readInput("Day13")
    part1(input).println()
    part2(input).println()
}
