import java.math.BigInteger

fun main() {
    fun match(shape: String, counts: List<Int>): Boolean {
        return shape.split(".").filter { it.isNotEmpty() }
            .map { it.length } == counts
    }

    fun String.rep(loc: Int, c: Char) = this.slice(0..<loc) + c + this.slice(loc+1..<this.length)
    fun part1(input: List<String>): Long {
        val springs = input.map { s -> s.split(" ").let { Pair(it[0], it[1].split(",").map(String::toInt)) } }
        val empties = springs.map { it.first.withIndex().filter { c -> c.value == '?' }.map { c -> c.index } }
        val arrangements = springs.map { spring ->
            var possibilities = mutableListOf(spring.first)
            while (possibilities[0].contains('?')) {
                possibilities = possibilities.flatMap { s -> s.indexOfFirst { it == '?' }
                    .let { listOf(s.rep(it, '.'), s.rep(it, '#')) } }
                    .toMutableList()
            }
            possibilities.filter { match(it, spring.second) }.size
//            possibilities.map { it.split(".").filter { it.isNotEmpty() }
//                .map { it.length }  == spring.second }
//            possibilities
        }
        return arrangements.sum().toLong()
    }

//    fun parti(len: Int, groups: Int): List<List<Int>> {
//        return if (groups == 0 && len == 0) listOf(listOf())
//        else if (groups == 0) listOf()
//        else (0..len).flatMap { first -> parti(len - first, groups - 1).map { arr -> arr + first } }
//    }
//    fun rightPossibles(len: Int, counts: List<Int>): List<String> {
//        val res = len - counts.sum() - counts.size + 1
//        print(parti(len, counts.size + 1))
//        return parti(res, counts.size + 1).map { arr ->
//            val blanks = arr.map { "".padStart(it, '1') }
//            blanks.slice(0..blanks.size - 2).zip(counts.map { "".padStart(it, '2') })
//                .joinToString("") { (ones, twos) -> "$ones$twos" } + blanks.takeLast(1)
//        }
//    }

    fun rightRegex(right: List<Int>) = "([.?]*)" + right.joinToString("[.?]+") { "[#?]".repeat(it) } + "([.?]*)"

    fun longestSpringForEveryIndex(conf: String) = conf.indices.map { ind -> Pair(ind, conf.drop(ind).indexOfFirst { it == '.' }
        .let { if (it == -1) conf.length - ind else it }) }
    fun chooseLoc(conf: String, len: Int) = longestSpringForEveryIndex(conf).filter { it.second >= len &&
            (it.first + len >= conf.length || conf[it.first + len] != '#') &&
            !conf.slice(0..<it.first).contains('#') }
        .map { it.first }
    fun getByDP(conf: String, right: List<Int>): BigInteger {
//        println(conf)
        val possibilities = conf.indices.flatMap { ind -> right.toSet().map {  // [(substr of conf, pipeLen), [choose loc])]
            Pair(Pair(conf.slice(ind..<conf.length), it),
                chooseLoc(conf.slice(ind..<conf.length), it)) } }.toMap().toMutableMap()
        var nexts = chooseLoc(conf, right[0]).map { Pair(it, 1.toBigInteger()) } // [(ind1, 1), (ind2, 1), ...]
//        nexts.println()
        for ((ind, pipeLen) in right.withIndex().drop(1)) {
//            nexts.asSequence()
//                .map { Pair(it.first + right[ind - 1] + 1, it.second) } // [(substr begin loc, occur), ...]
//                .mapNotNull { trans -> possibilities[Pair(conf.slice(trans.first..<conf.length), pipeLen)]?.map { Pair(it + trans.first, trans.second) } }
//                .flatten() // [(choose loc, occ]
//                .toList()
//                .println()
            nexts = nexts.asSequence()
                .map { Pair(it.first + right[ind - 1] + 1, it.second) } // [(substr begin loc, occur), ...]
                .mapNotNull { trans -> possibilities[Pair(conf.slice(trans.first..<conf.length), pipeLen)]?.map { Pair(it + trans.first, trans.second) } }
                .flatten() // [(choose loc, occ]
                .groupBy { it.first }
                .map { group -> Pair(group.key, group.value.sumOf { it.second }) }
                .toList()
//            if (ind > 2) break
        }
//        println(nexts)
//        println(possibilities)
        return nexts.sumOf { it.second }
    }

    fun part2(input: List<String>): BigInteger {
        val springs = input.map { s -> s.split(" ").let { line ->
            Pair("${line[0]}?${line[0]}?${line[0]}?${line[0]}?${line[0]}",
            line[1].let { listOf(it, it, it, it, it).joinToString(",") }.split(",").map(String::toInt)) } }

//        println(springs[1].first)
//        println(rightRegex(springs[1].second).toRegex().find(springs[1].first)?.groups)
//        println(springs.map { it.first.matches(rightRegex(it.second).toRegex()) })
//        val lists = springs.map { springConf ->
//            var worklist = mutableListOf(springConf.first)
//            val rightPattern = rightRegex(springConf.second).toRegex()
//            val finishList = mutableListOf<String>()
//            while (worklist.isNotEmpty()) {
//                val head = worklist.first()
//                worklist = worklist.drop(1).toMutableList()
//                val firstUnknown = head.indexOfFirst { it == '?' }
//                if (firstUnknown >= 0) {
//                    val br1 = head.rep(firstUnknown, '.')
//                    if (br1.matches(rightPattern)) worklist += br1
//                    val br2 = head.rep(firstUnknown, '#')
//                    if (br2.matches(rightPattern)) worklist += br2
//                } else finishList += head
//            }
//            finishList
//        }

        getByDP(".#?.#?.#?.#?#", listOf(1,1,1,1,1)).println()
        fun chooseLocAtWholeString(conf: String, len: Int) = longestSpringForEveryIndex(conf).filter { it.second >= len &&
                (it.first + len >= conf.length || conf[it.first + len] != '#') &&
                (it.first - 1 < 0 || conf[it.first - 1] != '#')
        }.map { it.first }
        fun bipart(conf: String, proof: List<Int>): BigInteger {
            val poss = proof.toSet().associateWith {  // [pipeLen, [choose loc])]
                chooseLocAtWholeString(conf, it)
            }
            val memoizeRanges = mutableMapOf<Pair<IntRange, IntRange>, BigInteger>()

            fun rangedNums(stringSlice: IntRange, intSlice: IntRange): BigInteger {
                return if (memoizeRanges.containsKey(Pair(stringSlice, intSlice)))
                    memoizeRanges[Pair(stringSlice, intSlice)]!!
                else {
                    val res: BigInteger
                    if (intSlice.isEmpty()) {
                        res = if (conf.slice(stringSlice).contains('#'))
                            0.toBigInteger()
                        else 1.toBigInteger()
                    } else {
                        val mid = (intSlice.first + intSlice.last) / 2
                        res = poss[proof[mid]]!!.filter { it in stringSlice && it + proof[mid] - 1 in stringSlice }
                            .sumOf {  ind ->
                                rangedNums(stringSlice.first..<ind-1, intSlice.first..<mid) *
                                        rangedNums(ind+proof[mid]+1..stringSlice.last, mid+1..intSlice.last)
                            }
                    }
                    memoizeRanges[Pair(stringSlice, intSlice)] = res
                    res
                }
            }
//            println(proof[mid])
//            println("$mid ${chooseLocAtWholeString(conf, proof[mid])}")
            return rangedNums(conf.indices, proof.indices)
        }
//        "1234".slice(3..1)
        bipart("?###????????", listOf(3,2,1)).println()
//        return springs.sumOf { getByDP(it.first, it.second) }
        println(springs.map { bipart(it.first, it.second) })
        return springs.sumOf { bipart(it.first, it.second) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 21.toLong())
    check(part2(testInput) == 525152.toBigInteger())

    val input = readInput("Day12")
    part1(input).println()
    part2(input).println()
}
