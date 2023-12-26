import java.math.BigInteger

fun main() {
    fun part1(input: List<String>): BigInteger {
        val data = input.associate { it.split(": ").let { Pair(it[0], it[1].split(" ")) } }
        println(data)
        val connections = data.flatMap { (k, vs) -> vs.map { Pair(k, it) } }
        val items = data.keys + data.values.flatten()
        var flag = 0
        val size = (connections.size * connections.size * connections.size).toDouble()

        fun getPossibleFriends(item: String) = connections.filter { it.first == item }.map { it.second } +
                connections.filter { it.second == item }.map { it.first }
        val possibleFriends = items.associateWith { getPossibleFriends(it).toSet() }
        val edgeFlows = connections.associateWith { 0 }.toMutableMap()
        val nodeDist = items.associateWith { Int.MAX_VALUE }.toMutableMap()
        val src = items.first()
        var dst = items.last()
        nodeDist[src] = 0

//        // hierarchy graph
//        var workList = possibleFriends[src]!!.toMutableSet()
//        while (workList.isNotEmpty()) {
//            val item = workList.first()
//            workList -= item
//            if (nodeDist[item] == Int.MAX_VALUE) {
//                nodeDist[item] = possibleFriends[item]!!.minOf { nodeDist[it]!! } + 1
//                for (friend in possibleFriends[item]!!) {
//                    if (nodeDist[friend] == Int.MAX_VALUE) workList += friend
//                }
//            }
//        }
//
//        workList = possibleFriends[src]!!.toMutableSet()
//        for (edge in (connections.filter { it.first == src } +
//                connections.filter { it.second == src }).take(4)) {
//            if (edge.first == items.first()) edgeFlows[edge] = 1
//            else edgeFlows[edge] = -1
//        }
//
//        fun getInEdges(item: String) = connections.filter { it.first == item }
//        fun getOutEdges(item: String) = connections.filter { it.second == item }
//        fun getFlow(item: String) = getInEdges(item).sumOf { edgeFlows[it]!! } - getOutEdges(item).sumOf { edgeFlows[it]!! }
//
//        while (workList.isNotEmpty()) {
//            val item = workList.first()
//            workList -= item
//            if (getFlow(item) != 0) {
//                val further = possibleFriends[item]!!.filter { nodeDist[it]!! > nodeDist[item]!! }
//                further
//            }
//        }
//
//        while (workList.isNotEmpty()) {
//            val item = workList.first()
//            workList -= item
//            if (nodeDist[item] == Int.MAX_VALUE) {
//                nodeDist[item] = possibleFriends[item]!!.minOf { nodeDist[it]!! } + 1
//                for (friend in possibleFriends[item]!!) {
//                    if (nodeDist[friend] == Int.MAX_VALUE) workList += friend
//                }
//            }
//        }

        var cut = 10
        var edges: Set<Pair<String, String>>
        var groups: Set<Set<String>> = setOf()
        while (cut != 3) {
            edges = connections.toSet()
            groups = items.map { setOf(it) }.toSet()
            // Karger’s algorithm https://web.stanford.edu/~rezab/discrete/Notes/4.pdf
            while (groups.size > 2) {
                val choosed = edges.random()
                val group1 = groups.find { choosed.first in it }!!
                val group2 = groups.find { choosed.second in it }!!
                val merged = group1 + group2
                groups = groups - setOf(group1, group2) + setOf(merged)
                edges = edges.filterNot { it.first in merged && it.second in merged }.toSet()
            }
            cut = edges.size
            println(cut)
        }


        return groups.fold(1) { acc, group -> acc * group.size }.toBigInteger()
    }

    fun part2(input: List<String>): BigInteger {
        return 0
            .toBigInteger()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day25_test")
    check(part1(testInput) == 54.toBigInteger())
//    check(part2(testInput) == 0.toBigInteger())

    val input = readInput("Day25")
    part1(input).println()
    part2(input).println()
}
