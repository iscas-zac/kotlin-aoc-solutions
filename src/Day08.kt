import java.math.BigInteger

fun main() {

    fun part1(input: List<String>): Int {
//        val times = input[0].substring(5).split(" ").filter(String::isNotEmpty).map(String::toInt)
        val insts = input[0]
        val branches = input.subList(2, input.size).map { it.split("=", "(", ",", ")").filter(String::isNotEmpty).filter(String::isNotBlank).map(String::trim) }
//        print(branches)
        var tup = branches.find { it[0] == "AAA" }!!
        var ind = 0
//        println(tup)
        while (tup[0] != "ZZZ") {
            val next = if (insts[ind % insts.length] == 'L') tup[1] else tup[2]
//            print(insts[ind % input.size])
//            print(next)
//            println(ind)
            tup = branches.find { it[0] == next }!!
            ind++
        }

        return ind
    }

    fun part2(input: List<String>): BigInteger {
        val insts = input[0]
        val branches = input.subList(2, input.size).associate {
            it.split("=", "(", ",", ")")
                .filter(String::isNotEmpty)
                .filter(String::isNotBlank)
                .map(String::trim)
                .let { lst -> Pair(lst[0], mapOf(Pair('L', lst[1]), Pair('R', lst[2]))) }
        }
        val starts = branches.filter { it.key.endsWith('A') }.keys

        fun fallInZ(start: String): List<Int> {
            val res = mutableListOf<Int>()
            var i = 0
            var cur = start
            while (i < input.size * branches.size * 2) {
                if (cur.endsWith('Z')) res.add(i)
                cur = branches[cur]!![insts[i % insts.length]]!!
                i++
            }
            return res
        }

//        insts.length.println()
        return starts.map { fallInZ(it).first().toBigInteger() }
            .fold(1.toBigInteger()) { acc, num -> acc * num / acc.gcd(num) }

//        return 6.toBigInteger()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
//    check(part1(testInput) == 2)
    check(part2(testInput) == 6.toBigInteger())

    val input = readInput("Day08")
    part1(input).println()
    part2(input).println()
}
