import java.math.BigInteger

fun main() {
    fun hash(content: String): Int {
        var curr = 0
        for (c in content) {
            val ascii = c.code
            curr += ascii
            curr *= 17
            curr %= 256
        }
        return curr
    }
    fun part1(input: List<String>): BigInteger {
        val listOfCodes = input.joinToString("").split(',')
//        println(listOfCodes.map { hash(it).toBigInteger() })
        return listOfCodes.sumOf { hash(it).toBigInteger() }
    }

    fun part2(input: List<String>): BigInteger {
        val listOfCodes = input.joinToString("").split(',')

        return listOfCodes.groupBy { hash(it.split('=')[0].trimEnd('-')) }.map { (box, lens) ->
            (1 + box).toBigInteger() * lens.map { it.split('=')[0].trimEnd('-') }
                .toSet()
                .mapNotNull { pref ->
                    val lastMinus = lens.withIndex()
                        .findLast { it.value.startsWith("$pref-") }?.index ?: (-1)
                    try {
                        val firstEqualAfterLastMinus = lens.withIndex()
                            .first { it.value.startsWith("$pref=") && it.index > lastMinus }.index
                        Pair(pref, firstEqualAfterLastMinus)
                    } catch (_: NoSuchElementException) {
                        null
                    }
                }
                .sortedBy { it.second }
                .map { it.first }
                .withIndex()
                .sumOf { (ind, pref) ->
                    (ind + 1).toBigInteger() * lens.findLast { it.startsWith("$pref=") }!!
                        .split('=')[1]
                        .toBigInteger()
                }
        }.sumOf { it }
//        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    check(part1(testInput) == 1320.toBigInteger())
    check(part2(testInput) == 145.toBigInteger())

    val input = readInput("Day15")
    part1(input).println()
    part2(input).println()
}
