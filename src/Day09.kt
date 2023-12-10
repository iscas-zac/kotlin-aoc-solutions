fun main() {
    fun interpolatable(series: List<Int>): Int {
        val diffs = series.zipWithNext().map { (a0, a1) -> a1 - a0 }
        if (diffs.all { it == 0 }) return series[0]
        else return interpolatable(diffs) + series[series.size - 1]
    }

    fun part1(input: List<String>): Int {
        val histories = input.map { it.split(" ").filter(String::isNotEmpty).map(String::toInt) }

        return histories.sumOf { interpolatable(it) }
    }

    fun part2(input: List<String>): Int {
        return input.map { it.split(" ").filter(String::isNotEmpty).map(String::toInt).asReversed() }
            .sumOf { interpolatable(it) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 114)
    check(part2(testInput) == 2)

    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}
