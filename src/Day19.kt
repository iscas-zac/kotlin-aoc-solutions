import java.math.BigInteger
import kotlin.math.max
import kotlin.math.min

fun main() {
    fun sampleAfterRule(sample: Map<Char, Int>, rule: String): String {
        val branches = rule.split(',')
        for (branch in branches) {
            if (!branch.contains(':')) return branch
            val cond = branch.split(':')[0]
            val symb = cond[1]
            val exec = branch.split(':')[1]
            if (symb == '<')
                if (sample[cond[0]] != null && sample[cond[0]]!! < cond.slice(2..<cond.length).toInt())
                    return exec
            if (symb == '>')
                if (sample[cond[0]] != null && sample[cond[0]]!! > cond.slice(2..<cond.length).toInt())
                    return exec
        }
        return ""
    }

    fun part1(input: List<String>): BigInteger {
        val rules = input.take(input.indexOfFirst { it.isEmpty() })
            .associate { rule -> rule.split("[{}]".toRegex()).let { Pair(it[0], it[1]) } }
        val samples = input.drop(input.indexOfFirst { it.isEmpty() } + 1)
            .map { group ->
                group.trim('{')
                    .trimEnd('}')
                    .split(',').associate { Pair(it[0], it.slice(2..<it.length).toInt()) }
            }

        fun processSample(sample: Map<Char, Int>): String {
            var res = "in"
            while (res != "A" && res != "R") {
                res = sampleAfterRule(sample, rules[res]!!)
            }
            return res
        }
//        println(samples.map { processSample(it) })
        return samples.filter { processSample(it) == "A" }.sumOf { it.values.sum() }.toBigInteger()
    }

    fun part2(input: List<String>): BigInteger {
        val rules = input.take(input.indexOfFirst { it.isEmpty() })
            .associate { rule -> rule.split("[{}]".toRegex()).let { line ->
                Pair(line[0],
                line[1].split(',')
                    .let { rules ->
                        val targets = rules.map { rule -> rule.split(':').last() }
                        val lastTarget = targets.last()
                        rules.zip(targets).dropLastWhile { (_, target) -> target == lastTarget }
                            .map { it.first } + lastTarget
                    }) } }
        println(input)
        println(rules)
        val allPaths = mutableListOf<List<String>>()
        var interestingSet = listOf(listOf("in"))
        while (interestingSet.isNotEmpty()) {
            val extended = interestingSet.flatMap { partialPath ->
                rules[partialPath.last()]!!.map { partialPath + it.split(':').last() }
            }
            interestingSet = extended.filter { it.last() != "A" && it.last() != "R" }
            allPaths += extended.filter { it.last() == "A" || it.last() == "R" }
        }
        val acceptedPaths = allPaths.filter { it.last() == "A" }
        val subscr = mapOf(Pair('x', 0), Pair('m', 1), Pair('a', 2), Pair('s', 3))

        val rulePairToRanges =
            acceptedPaths.flatMap { path -> path.zip(path.drop(1)) }.toSet().associate { (ruleName, target) ->
                Pair(Pair(ruleName, target),
                    listOf(
                        mutableListOf(
                            Pair(0, 4001),
                            Pair(0, 4001),
                            Pair(0, 4001),
                            Pair(0, 4001)
                        )
                    ).flatMap { oldRangeFor4 ->
                        rules[ruleName]!!.withIndex().filter { it.value.contains(target) }.map { (ind, entry) ->
                            val rangeFor4 = mutableListOf<Pair<Int, Int>>()
                            rangeFor4.addAll(oldRangeFor4)
                            if (entry.contains(":")) {
                                val cond = entry.split(':')[0]
                                val num = cond.slice(2..<cond.length).toInt()
                                val range = rangeFor4[subscr[cond[0]]!!]
                                if (cond[1] == '<') {
                                    rangeFor4[subscr[cond[0]]!!] = Pair(range.first, min(range.second, num))
                                } else if (cond[1] == '>') {
                                    rangeFor4[subscr[cond[0]]!!] = Pair(max(range.first, num), range.second)
                                }
                            }
                            for (otherEntry in rules[ruleName]!!.take(ind)) {
                                val cond = otherEntry.split(':')[0]
                                val num = cond.slice(2..<cond.length).toInt()
                                val range = rangeFor4[subscr[cond[0]]!!]
                                if (cond[1] == '>') {
                                    rangeFor4[subscr[cond[0]]!!] = Pair(range.first, min(range.second, num + 1))
                                } else if (cond[1] == '<') {
                                    rangeFor4[subscr[cond[0]]!!] = Pair(max(range.first, num - 1), range.second)
                                }
                            }
                            rangeFor4
                        }
                    }
                )
            }
        val rangeQuples = acceptedPaths.flatMap { path ->
            var rangesFor4 = listOf(mutableListOf(Pair(0, 4001), Pair(0, 4001), Pair(0, 4001), Pair(0, 4001)))
            for ((ruleName, target) in path.zip(path.drop(1))) {
                rangesFor4 = rangesFor4.flatMap { prev ->
                    rulePairToRanges[Pair(ruleName, target)]!!.map { trans ->
                        (0..3).map { Pair(max(trans[it].first, prev[it].first), min(trans[it].second, prev[it].second)) }
                            .toMutableList()
                    }
                }.filter { range -> range.all { it.first < it.second - 1 } }
            }
            rangesFor4
        }.toSet().toList()
//        val rangeQuples = acceptedPaths.flatMap { path ->
//            var rangesFor4 = listOf(mutableListOf(Pair(0, 4001), Pair(0, 4001), Pair(0, 4001), Pair(0, 4001)))
//            for ((ruleName, target) in path.zip(path.drop(1))) {
//                rangesFor4 = rangesFor4.flatMap { oldRangeFor4 ->
//                    rules[ruleName]!!.filter { it.contains(target) }.map { entry ->
//                        val rangeFor4 = mutableListOf<Pair<Int, Int>>()
//                        rangeFor4.addAll(oldRangeFor4)
//                        if (entry.contains(":")) {
//                            val cond = entry.split(':')[0]
//                            val num = cond.slice(2..<cond.length).toInt()
//                            val range = rangeFor4[subscr[cond[0]]!!]
//                            if (cond[1] == '<') {
//                                rangeFor4[subscr[cond[0]]!!] = Pair(range.first, min(range.second, num))
//                            } else if (cond[1] == '>') {
//                                rangeFor4[subscr[cond[0]]!!] = Pair(max(range.first, num), range.second)
//                            }
//                        }
//                        for (otherEntry in rules[ruleName]!!.takeWhile { !it.contains(target) }) {
//                            val cond = otherEntry.split(':')[0]
//                            val num = cond.slice(2..<cond.length).toInt()
//                            val range = rangeFor4[subscr[cond[0]]!!]
//                            if (cond[1] == '>') {
//                                rangeFor4[subscr[cond[0]]!!] = Pair(range.first, min(range.second, num + 1))
//                            } else if (cond[1] == '<') {
//                                rangeFor4[subscr[cond[0]]!!] = Pair(max(range.first, num - 1), range.second)
//                            }
//                        }
//                        rangeFor4
//                    }
//                }
//            }
//            rangesFor4
//        }
//        println(allPaths)
//        println(acceptedPaths)
        println(rangeQuples)
//        run {
//            val rules1 = input.take(input.indexOfFirst { it.isEmpty() })
//                .associate { rule -> rule.split("[{}]".toRegex()).let { Pair(it[0], it[1]) } }
//            val samples1 = input.drop(input.indexOfFirst { it.isEmpty() } + 1)
//                .map { group ->
//                    group.trim('{')
//                        .trimEnd('}')
//                        .split(',').associate { Pair(it[0], it.slice(2..<it.length).toInt()) }
//                }
//
//            fun processSample(sample: Map<Char, Int>): List<String> {
//                val res = mutableListOf("in")
//                while (res.last() != "A" && res.last() != "R") {
//                    res += sampleAfterRule(sample, rules1[res.last()]!!)
//                }
//                return res
//            }
//
//            val outliers = samples1.filter { processSample(it).last() == "A" }.filter { sample ->
//                rangeQuples
//                    .all { quple ->
//                        quple.zip(sample.toList())
//                            .any { (range, test) -> range.first >= test.second || range.second <= test.second }
//                    }
//            }
//
//            println(outliers)
//            val path = processSample(outliers[0])
//            var rangesFor4 = listOf(mutableListOf(Pair(0, 4001), Pair(0, 4001), Pair(0, 4001), Pair(0, 4001)))
//            for ((ruleName, target) in path.zip(path.drop(1))) {
//                rangesFor4 = rangesFor4.flatMap { oldRangeFor4 ->
//                    rules[ruleName]!!.filter { it.contains(target) }.map { entry ->
//                        val rangeFor4 = mutableListOf<Pair<Int, Int>>()
//                        rangeFor4.addAll(oldRangeFor4)
//                        if (entry.contains(":")) {
//                            val cond = entry.split(':')[0]
//                            val num = cond.slice(2..<cond.length).toInt()
//                            val range = rangeFor4[subscr[cond[0]]!!]
//                            if (cond[1] == '<') {
//                                rangeFor4[subscr[cond[0]]!!] = Pair(range.first, min(range.second, num))
//                            } else if (cond[1] == '>') {
//                                rangeFor4[subscr[cond[0]]!!] = Pair(max(range.first, num), range.second)
//                            }
//                        }
//                        for (otherEntry in rules[ruleName]!!.takeWhile { !it.contains(target) }) {
//                            val cond = otherEntry.split(':')[0]
//                            val num = cond.slice(2..<cond.length).toInt()
//                            val range = rangeFor4[subscr[cond[0]]!!]
//                            if (cond[1] == '>') {
//                                rangeFor4[subscr[cond[0]]!!] = Pair(range.first, min(range.second, num + 1))
//                            } else if (cond[1] == '<') {
//                                rangeFor4[subscr[cond[0]]!!] = Pair(max(range.first, num - 1), range.second)
//                            }
//                        }
//                        rangeFor4
//                    }
//                }
//            }
//
//            println(processSample(outliers[0]))
//            println(rangesFor4)
//            //        println(rangeQuples.filter { it[0].second > outliers[0]['x']!! && it[0].first < outliers[0]['x']!! } .joinToString("\n"))
//            //        return samples1.filter { processSample(it) == "A" }.sumOf { it.values.sum() }.toBigInteger()
//        }

        fun condIntersect(xs: List<Pair<Int, Int>>, ys: List<Pair<Int, Int>>): List<Pair<Int, Int>> {
            return xs.zip(ys).map { (x4, y4) -> Pair(max(x4.first, y4.first), min(x4.second, y4.second)) }
        }
        fun rangeNotEmpty(ranges: List<Pair<Int, Int>>) = ranges.all { p -> p.second > p.first }
        fun rangeContain(bigger: List<Pair<Int, Int>>, smaller: List<Pair<Int, Int>>) =
            bigger.zip(smaller).all { (bp, sp) -> bp.first < sp.first && bp.second >= sp.second }
        fun openRangeContain(bigger: List<Pair<Int, Int>>, smaller: List<Pair<Int, Int>>) =
            bigger.zip(smaller).all { (bp, sp) -> bp.first <= sp.first && bp.second >= sp.second }
        fun rangeContainSingle(bigger: Pair<Int, Int>, smaller: Pair<Int, Int>) =
            bigger.first < smaller.first && bigger.second >= smaller.second
        fun rangePartialContain(bigger: List<Pair<Int, Int>>, smaller: List<Pair<Int, Int>>) =
            bigger.zip(smaller).all { (bp, sp) -> bp.first < sp.second - 1 && bp.second > sp.first }
        fun isOpenRangeIntersect(r1: List<Pair<Int, Int>>, r2: List<Pair<Int, Int>>) =
            r1.zip(r2).all { (bp, sp) -> bp.first < sp.second - 1 && bp.second - 1 > sp.first }

        println("before filter")
        println(rangeQuples.size)
        println("after filter")
        val mergedRangeQuples = rangeQuples.filter { rangeUnderExamine -> rangeQuples.count { openRangeContain(it, rangeUnderExamine) } < 2 }
        println(mergedRangeQuples.size)
        println(mergedRangeQuples)
//        println("intersects: " + mergedRangeQuples.map { condIntersect(mergedRangeQuples[0], it) }
//            .filter { it.all { it.first < it.second - 1 } })
        println(mergedRangeQuples.filter { mrq -> mergedRangeQuples.count { isOpenRangeIntersect(it, mrq) } > 1 }
            .associateWith { mrq -> mergedRangeQuples.filter { it != mrq && isOpenRangeIntersect(it, mrq) } })

        val slices = (0..3).map { index -> mergedRangeQuples.map { it[index] } } // left closed right open
            .map { list -> (list.map { it.first + 1 } + list.map { it.second }).toSet().sorted() }
            .map { list -> list.zip(list.drop(1)) }

        println(slices.map { it.size })
        val z = slices.map { it.size }.fold((1).toDouble()) { acc, i -> acc * i.toDouble() }
        var i = 0
        var res = (0).toBigInteger()
//        val abc = mutableListOf<List<Pair<Int, Int>>>()
        for (ind in 0..3) {
            println((1..4000).filter { line -> mergedRangeQuples.all { it[ind].first >= line || it[ind].second <= line } }
                .map { line ->
                    Pair(
                        mergedRangeQuples.count { it[ind].first >= line },
                        mergedRangeQuples.count { it[ind].second <= line })
                })
        }

        println()

//        val eligibleSlicesTableForX =
//            slices[0].associateWith { x -> mergedRangeQuples.filter { rangeContainSingle(it[0], x) }.toSet() }
//        val eligibleSlicesTableForM =
//            slices[1].associateWith { m -> mergedRangeQuples.filter { rangeContainSingle(it[1], m) }.toSet() }
//        val eligibleSlicesTableForA =
//            slices[2].associateWith { a -> mergedRangeQuples.filter { rangeContainSingle(it[2], a) }.toSet() }
//        val eligibleSlicesTableForS =
//            slices[3].associateWith { s -> mergedRangeQuples.filter { rangeContainSingle(it[3], s) }.toSet() }
//        for (x in slices[0]) {
//            val eligibleSlicesForX = eligibleSlicesTableForX[x]!!
//            for (m in slices[1]) {
//                val eligibleSlicesForM = eligibleSlicesForX.intersect(eligibleSlicesTableForM[m]!!)
//                for (a in slices[2]) {
//                    val eligibleSlicesForA = eligibleSlicesForM.intersect(eligibleSlicesTableForA[a]!!)
//                    for (s in slices[3]) {
//                        print("\r" + i.toDouble() / z)
//                        i++
//
//                        val element = listOf(x, m, a, s)
//                        //if (eligibleSlicesForA.any { rangeContainSingle(it[3], s) }) {
//                        if (eligibleSlicesForA
//                                .intersect(eligibleSlicesTableForS[s]!!)
//                                .isNotEmpty()) {
//                            res += element.fold(1.toBigInteger()) { acc, pair -> acc * (pair.second - pair.first).toBigInteger() }
//                        }
//                    }
//                }
//            }
//        }
//        println()
//        println(slices)
//        println(abc.groupBy { it }.mapValues { it.value.size }.toList().sumOf { (l, w) -> l.fold(1.toBigInteger()) { acc, pair -> acc * (pair.second - pair.first).toBigInteger() }})
//        val cde = abc.groupBy { it }.mapValues { it.value.size }.toList().filter { it.second != 1 }.map { it.first }
//        println(rangePartialContain(cde[0], cde[1]))
//        println(rangeQuples[4].fold(1.toBigInteger()) { acc, pair -> acc * (pair.second - pair.first - 1).toBigInteger() })
//        println(rangeQuples[4])
//        println(slices.map { it.size })
        println(res)
        return mergedRangeQuples.sumOf { it.fold(1.toBigInteger()) { acc, pair -> acc * (pair.second - pair.first - 1).toBigInteger() } }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day19_test")
    check(part1(testInput) == 19114.toBigInteger())
    check(part2(testInput) == 167409079868000.toBigInteger())

    val input = readInput("Day19")
    part1(input).println()
    part2(input).println()
}
