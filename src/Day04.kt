fun main() {
    fun getWinningNumbers(s: String) = s.split("|", ":")[1]
            .split(" ")
            .filter(String::isNotEmpty)
            .map(String::toInt)

    fun getOwnedNumbers(s: String) = s.split("|", ":")[2]
        .split(" ")
        .filter(String::isNotEmpty)
        .map(String::toInt)

    fun part1(input: List<String>): Int {
        return input.map { cards -> getWinningNumbers(cards).count { it in getOwnedNumbers(cards) } }
            .sumOf { if (it == 0) 0 else 1.shl(it - 1) }
    }

    fun part2(input: List<String>): Int {
        val matchingNumbers = input.map { cards -> getWinningNumbers(cards).count { it in getOwnedNumbers(cards) } }
        val cardNumbers = MutableList(matchingNumbers.size) { 1 }
        for (i in 0..<cardNumbers.size) {
            for (j in 0..<matchingNumbers[i]) {
                cardNumbers[i + j + 1] += cardNumbers[i]
            }
        }
//        print(cardNumbers)
        return cardNumbers.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 30)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}
