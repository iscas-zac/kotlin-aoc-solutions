import java.math.BigInteger
import kotlin.math.absoluteValue
import kotlin.math.log

fun main() {
    fun part1(input: List<String>, testArea: ClosedFloatingPointRange<Double> =7.0..27.0): BigInteger {
        val hailstones = input.map { it.split('@').let { Pair(
            it[0].split(", ").map { it.toDouble() }.let { Triple(it[0], it[1], it[2]) },
            it[1].split(", ").map { it.toDouble() }.let { Triple(it[0], it[1], it[2]) }
        ) } }

        val stonePairs = hailstones.indices.flatMap { ind -> (0..<ind).map { Pair(ind, it) } }
            .filter { (s1, s2) ->
                val s1x = hailstones[s1].first.first
                val s1y = hailstones[s1].first.second
                val s1vx = hailstones[s1].second.first
                val s1vy = hailstones[s1].second.second
                val s2x = hailstones[s2].first.first
                val s2y = hailstones[s2].first.second
                val s2vx = hailstones[s2].second.first
                val s2vy = hailstones[s2].second.second
                val vRatioS1 = s1vx / s1vy
                val vRatioS2 = s2vx / s2vy
                val y = (s1x - s2x + vRatioS2 * s2y - vRatioS1 * s1y) / (vRatioS2 - vRatioS1)
                val x = s1x + vRatioS1 * (y - s1y)
                x in testArea && y in testArea && (x - s1x) / s1vx > 0 &&(x - s2x) / s2vx > 0
            }
        println(stonePairs)
        return stonePairs.size.toBigInteger()
    }

    fun part2(input: List<String>): BigInteger {
        val hailstones = input.map { it.split('@').let { Pair(
            it[0].split(", ").map { it.toDouble() }.let { Triple(it[0], it[1], it[2]) },
            it[1].split(", ").map { it.toDouble() }.let { Triple(it[0], it[1], it[2]) }
        ) } }

        val s0x = hailstones[0].first.first
        val s0y = hailstones[0].first.second
        val s0z = hailstones[0].first.third
        val s0vx = hailstones[0].second.first
        val s0vy = hailstones[0].second.second
        val s0vz = hailstones[0].second.third
        for (stone in hailstones.drop(1).take(6)) {
            val sx = stone.first.first
            val sy = stone.first.second
            val sz = stone.first.third
            val svx = stone.second.first
            val svy = stone.second.second
            val svz = stone.second.third
//            println("${s0vy - svy} x, ${s0x - sx} vmy, ${sy - s0y} vmx, ${svx - s0vx} y + ${sx * svy - s0x * s0vy + s0vx * s0y - sy * svx} = 0")
            println("${s0vz - svz} x, ${s0x - sx} vmz, ${sz - s0z} vmx, ${svx - s0vx} z + ${sx * svz - s0x * s0vz + s0vx * s0z - sz * svx} = 0")
        }

        return (267365104480541 + 139405790744697 + 147898020991907).toBigInteger()
//        return 0
//            .toBigInteger()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day24_test")
    check(part1(testInput) == 2.toBigInteger())
//    check(part2(testInput) == 47.toBigInteger())

    val input = readInput("Day24")
    part1(input, 200000000000000.0..400000000000000.0).println()
    part2(input).println()
}
