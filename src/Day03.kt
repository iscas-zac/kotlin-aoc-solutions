fun main() {
    fun part1(input: List<String>): Int {
        val width =  if (input.isNotEmpty()) input[0].length else 0
//        print(width)
        val pad = "${"".padStart(width + 2, '.')}${input.joinToString(".")}${"".padEnd(width + 2, '.')}" // 2 line ends
//        print(pad)
//        print(pad.length)
        val digits = pad.map { char -> char.isDigit() }
        val starts = digits.withIndex()
            .filter { (ind, isDigit) -> isDigit && !digits[ind - 1] }
            .map { it.index }
        val ends = digits.withIndex()
            .filter { (ind, isDigit) -> isDigit && !digits[ind + 1] }
            .map{ it.index }
        val intervals = starts.zip(ends)

        fun isAdjacentToSymbol(loc: Int) = arrayOf(
            pad[loc - (width + 1) - 1],
            pad[loc - (width + 1)],
            pad[loc - (width + 1) + 1],
            pad[loc - 1],
            pad[loc + 1],
            pad[loc + (width + 1) - 1],
            pad[loc + (width + 1)],
            pad[loc + (width + 1) + 1],
        ).any { char -> !char.isDigit() && char != '.' }
//print(intervals)
        val nums = intervals.filter { (start, end) -> (start..end).any {isAdjacentToSymbol(it) } }
            .map { (start, end) -> pad.slice(start..end).toInt() }
//print(nums)
        return nums.sum()
    }

    fun part2(input: List<String>): Int {
        val width =  if (input.isNotEmpty()) input[0].length else 0
        val pad = "${"".padStart(width + 2, '.')}${input.joinToString(".")}${"".padEnd(width + 2, '.')}" // 2 line ends
        val digits = pad.map { char -> char.isDigit() }
        val starts = digits.withIndex()
            .filter { (ind, isDigit) -> isDigit && !digits[ind - 1] }
            .map { it.index }
        val ends = digits.withIndex()
            .filter { (ind, isDigit) -> isDigit && !digits[ind + 1] }
            .map{ it.index }
        val intervals = starts.zip(ends).map { (start, end) -> start..end }

        val gearCandidates = pad.withIndex().filter { (_, symbol) -> symbol == '*' }.map { it.index }

        fun getAdjacentInterval(loc: Int) = intervals.filter { interval ->
            arrayOf(
                loc - (width + 1) - 1,
                loc - (width + 1),
                loc - (width + 1) + 1,
                loc - 1,
                loc + 1,
                loc + (width + 1) - 1,
                loc + (width + 1),
                loc + (width + 1) + 1,
            ).any { it in interval }
        }

        return gearCandidates.map { getAdjacentInterval(it) }
            .filter { it.size == 2 }
            .sumOf { pad.slice(it[0]).toInt() * pad.slice(it[1]).toInt() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 4361)
    check(part2(testInput) == 467835)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}
