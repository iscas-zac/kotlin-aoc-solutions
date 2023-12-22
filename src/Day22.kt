import java.math.BigInteger
import kotlin.math.absoluteValue

fun main() {
    fun occupy(brick: Triple<IntRange, IntRange, IntRange>, coords: Triple<Int, Int, Int>) = coords.first in brick.first && coords.second in brick.second && coords.third in brick.third
    fun brickContent(brick: Triple<IntRange, IntRange, IntRange>) = brick.first.flatMap { x -> brick.second.flatMap { y -> brick.third.map { Triple(x, y, it) } } }
    fun supporter(brick: Triple<IntRange, IntRange, IntRange>, bricks: List<Triple<IntRange, IntRange, IntRange>>) = brickContent(brick).flatMap { (x, y, z) ->
        bricks.filter { occupy(it, Triple(x, y, z - 1)) && it != brick } }.toSet()
    fun shouldFall(brick: Triple<IntRange, IntRange, IntRange>, bricks: List<Triple<IntRange, IntRange, IntRange>>) = supporter(brick, bricks).isEmpty() &&
            brick.third.first != 1
    fun part1(input: List<String>): BigInteger {
        var bricks = input.map { it.split('~').let { it[0].split(',').zip(it[1].split(','))
            .map { (start, end) -> start.toInt()..end.toInt() }.let { Triple(it[0], it[1], it[2]) } } }
        while (bricks.any { shouldFall(it, bricks) }) {
            val (fallen, stab) = bricks.partition { shouldFall(it, bricks) }
//            println(bricks)
//            println(bricks.map { supporter(it, bricks) })
//            println(bricks.map { shouldFall(it, bricks) })
//            println(stab)
//            println(fallen)
            bricks = stab + fallen.map { (xs, ys, zs) -> Triple(xs, ys, zs.first - 1..<zs.last) }
//            println(fallen.map { (xs, ys, zs) -> Triple(xs, ys, zs.first - 1..<zs.last) })
        }
        println(bricks)
        val supportMap = bricks.map { supporter(it, bricks) }
        val disintegratables = bricks.filter { brick -> supportMap.all { brick !in it } }.toSet() + supportMap.filter { it.size > 1 }
            .flatten()
            .filter { candidate -> !supportMap.any { it.size == 1 && candidate in it } }
            .toSet()
        println(disintegratables)
        return disintegratables.size.toBigInteger()
    }

    fun part2(input: List<String>): BigInteger {
        var bricks = input.map { it.split('~').let { it[0].split(',').zip(it[1].split(','))
            .map { (start, end) -> start.toInt()..end.toInt() }.let { Triple(it[0], it[1], it[2]) } } }
        while (bricks.any { shouldFall(it, bricks) }) {
            val (fallen, stab) = bricks.partition { shouldFall(it, bricks) }
            bricks = stab + fallen.map { (xs, ys, zs) -> Triple(xs, ys, zs.first - 1..<zs.last) }
        }
        println(bricks)
        val supportMap = bricks.map { supporter(it, bricks) }
        val wholeSupportMap = bricks.associateWith { setOf<Triple<IntRange, IntRange, IntRange>>() }.toMutableMap()
//        bricks.filter { supporter(it, bricks).isEmpty() }
        val dependencyDict = bricks.zip(supportMap).toMap()
        var workList = bricks
        while (workList.isNotEmpty()) {
            val head = workList.first()
            workList = workList.drop(1)
            val tempDependency = wholeSupportMap[head]
            wholeSupportMap[head] = wholeSupportMap[head]!! + //dependencyDict[head]!! +
                    if (dependencyDict[head]!!.isNotEmpty())
                        dependencyDict[head]!!.fold(bricks.toSet()) { acc, mid -> acc.intersect(wholeSupportMap[mid]!! + mid) }
                    else setOf()
            if (tempDependency != wholeSupportMap[head]) {
                workList = workList + dependencyDict.filter { (_, deps) -> head in deps }
                    .map { it.key }
            }
        }
//        println(wholeSupportMap)
        println(wholeSupportMap.map { (_, deps) -> deps.size }.sum())

        return wholeSupportMap.map { (_, deps) -> deps.size }.sum().toBigInteger()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day22_test")
    check(part1(testInput) == 5.toBigInteger())
    check(part2(testInput) == 7.toBigInteger())

    val input = readInput("Day22")
    part1(input).println()
    part2(input).println()
}
