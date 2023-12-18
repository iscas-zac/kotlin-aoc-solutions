import java.lang.Math.abs
import java.lang.Math.max
import java.math.BigInteger
import kotlin.math.absoluteValue

@OptIn(ExperimentalStdlibApi::class)
fun main() {
    fun part1(input: List<String>): BigInteger {
        val plan = input.map { it.split(" ") }
        val coords = mutableListOf(Pair(0, 0))
//        val rays = mutableListOf<Pair<Pair<Int, Int>, Pair<Int, Int>>>()
        for (turn in plan) {
            val tail = coords.last()
            val offset0 = turn[1].toInt() * if (turn[0].contains('R')) 1 else if (turn[0].contains('L')) -1 else 0
            val offset1 = turn[1].toInt() * if (turn[0].contains('D')) 1 else if (turn[0].contains('U')) -1 else 0
            coords += Pair(tail.first + offset0, tail.second + offset1)
//            rays += Pair(tail, Pair(tail.first + offset0 + if (turn[0].contains('R')) -1 else if (turn[0].contains('L')) 1 else 0,
//                tail.second + offset1 + if (turn[0].contains('D')) -1 else if (turn[0].contains('U')) 1 else 0))
        }
        val rangeXmax = coords.maxOfOrNull { it.second }!!
        val rangeXmin = coords.minOfOrNull { it.second }!!
        val rangeYmax = coords.maxOfOrNull { it.first }!!
        val rangeYmin = coords.minOfOrNull { it.first }!!
//        println(coords)
//        println(rays)
        fun onX(x: Int, y: Int, b1: Pair<Int, Int>, b2: Pair<Int, Int>) = (b1.first - y) * (b2.first - y) <= 0
                && (x - b1.second) * (x - b2.second) <= 0
        fun crossX(x1: Int, x2: Int, y: Int, b1: Pair<Int, Int>, b2: Pair<Int, Int>) = if ((b1.first - y) * (b2.first - y) <= 0
                && (x1 - b1.second) * (x2 - b1.second) <= 0) {
            if (b1.first == y && b2.first == y) 0
            else if ((b1.first - y) * (b2.first - y) < 0) 1
            else if (b1.first + b2.first < 2 * y) 1
            else 0 // if (b1.first + b2.first > 2 * y)
        } else 0
//        println((rangeYmin..rangeYmax).map { y ->
//            (rangeXmin..rangeXmax).map { x ->
////                Pair(x, y)
//                coords.zipWithNext().any { (b1, b2) ->  onX(x, y, b1, b2) } ||
//                        coords.zipWithNext().sumOf { (b1, b2) -> crossX(x, rangeXmax, y, b1, b2) } % 2 == 1
//            }
//        }.joinToString("\n"))
        return (rangeYmin..rangeYmax).sumOf { y ->
            (rangeXmin..rangeXmax).count { x ->
                coords.zipWithNext().any { (b1, b2) ->  onX(x, y, b1, b2) } ||
                        coords.zipWithNext().sumOf { (b1, b2) -> crossX(x, rangeXmax, y, b1, b2) } % 2 == 1
            }
        }.toBigInteger()
    }

    fun part2(input: List<String>): BigInteger {
        val plan = input.map { line ->
            line.split(" ")[2].trim('(').trimEnd(')')
            .let { listOf(it.last().toString(), it.slice(1..it.length-2)) } }
        val coords = mutableListOf(Pair(0, 0))
        for (turn in plan) {
            val tail = coords.last()
            val offset0 = turn[1].hexToInt() * if (turn[0].contains('0')) 1 else if (turn[0].contains('2')) -1 else 0
            val offset1 = turn[1].hexToInt() * if (turn[0].contains('1')) 1 else if (turn[0].contains('3')) -1 else 0
            coords += Pair(tail.first + offset0, tail.second + offset1)
        }
        val rangeXmax = coords.maxOfOrNull { it.second }!!
        fun crossX(x1: Int, x2: Int, y: Int, b1: Pair<Int, Int>, b2: Pair<Int, Int>) = if ((b1.first - y).toBigInteger() * (b2.first - y).toBigInteger() <= 0.toBigInteger()
            && (x1 - b1.second).toBigInteger() * (x2 - b1.second).toBigInteger() <= 0.toBigInteger()) {
            if (b1.first == y && b2.first == y) 0
            else if ((b1.first - y).toBigInteger() * (b2.first - y).toBigInteger() < 0.toBigInteger()) 1
            else if ((b1.first + b2.first).toBigInteger() < (2).toBigInteger() * y.toBigInteger()) 1
            else 0 // if (b1.first + b2.first > 2 * y)
        } else 0
        val borders = coords.zip(coords.drop(1))
        val verts =
            borders.filter { (p1, p2) -> p1.first == p2.first }.map { (p, _) -> p.first }.toSet().sorted()
                .toList()
        val horizs =
            borders.filter { (p1, p2) -> p1.second == p2.second }.map { (p, _) -> p.second }.toSet().sorted()
                .toList()
//        val borderLen = borders.sumOf { (p1, p2) -> ((kotlin.math.abs(p1.second - p2.second) + 1).toBigInteger() *
//                (kotlin.math.abs(p2.first - p1.first) + 1).toBigInteger()) - (1).toBigInteger() }
        val rectangles = mutableListOf<List<Int>>()
//        println(verts.zip(verts.drop(1)).map { (vStart, vEnd) ->
//            horizs.zip(horizs.drop(1)).map { (hStart, hEnd) ->
//                val x = hStart + (hEnd - hStart) / 2
//                val y = vStart + (vEnd - vStart) / 2
//                if (//borders.any { (b1, b2) ->  onX(x, y, b1, b2) } ||
//                    borders.sumOf { (b1, b2) -> crossX(x, rangeXmax, y, b1, b2) } % 2 == 1) {
//                    Pair((vEnd - vStart - 1).toBigInteger(), (hEnd - hStart - 1).toBigInteger())
//                } else null
//            }
//        })
        val innerArea = verts.zip(verts.drop(1)).sumOf { (vStart, vEnd) ->
            horizs.zip(horizs.drop(1)).sumOf { (hStart, hEnd) ->
                val x = hStart + (hEnd - hStart) / 2
                val y = vStart + (vEnd - vStart) / 2
                if (borders.sumOf { (b1, b2) -> crossX(x, rangeXmax, y, b1, b2) } % 2 == 1) {
                    rectangles.add(listOf(vStart, hStart, vEnd, hEnd))
                    (vEnd - vStart - 1).toBigInteger() * (hEnd - hStart - 1).toBigInteger()
                } else (0).toBigInteger()
            }
        }
        println(rectangles.size)
//        println(Int.MAX_VALUE)
        val vertices = rectangles.flatMap { listOf(
            Pair(it[1], it[0]),
            Pair(it[3], it[0]),
            Pair(it[1], it[2]),
            Pair(it[3], it[2]),
            ) }.toSet()
        val connectionLength = horizs.zip(horizs.drop(1)).sumOf { (hStart, hEnd) ->
            verts.count { v -> rectangles.any { rect -> (rect[0] == v || rect[2] == v) && rect[1] == hStart && rect[3] == hEnd } }.toBigInteger() *
                    (hEnd - hStart - 1).toBigInteger()
        } + verts.zip(verts.drop(1)).sumOf { (vStart, vEnd) ->
            horizs.count { h -> rectangles.any { rect -> (rect[1] == h || rect[3] == h) && rect[0] == vStart && rect[2] == vEnd } }
                .toBigInteger() *
                    (vEnd - vStart - 1).toBigInteger()
        }

//        println(innerArea + connectionLength + vertices.size.toBigInteger())
//        fun onX(x: Int, y: Int, b1: Pair<Int, Int>, b2: Pair<Int, Int>) =
//            (b1.first - y).toBigInteger() * (b2.second - x).toBigInteger() == (b1.second - x).toBigInteger() * (b2.first - y).toBigInteger()
//                    && (b1.second - x).toBigInteger() * (b2.second - x).toBigInteger() + (b1.first - y).toBigInteger() * (b2.first - y).toBigInteger() <= (0).toBigInteger()
//        val innerBorderLen = (verts.flatMap { y -> (horizs.first()..horizs.last()).map { x -> Pair(y, x) } }.toSet() +
//                horizs.flatMap { x -> (verts.first()..verts.last()).map { y -> Pair(y, x) } }.toSet())
//            .count {(y, x) -> borders.any { (b1, b2) -> onX(x, y, b1, b2) } ||
//                    borders.sumOf { (b1, b2) -> crossX(x, rangeXmax, y, b1, b2) } % 2 == 1 }.toBigInteger()
//        println(innerBorderLen)
//        println((verts.flatMap { y -> (horizs.first()..horizs.last()).map { x -> Pair(y, x) } }.toSet() +
//                horizs.flatMap { x -> (verts.first()..verts.last()).map { y -> Pair(y, x) } }.toSet()).size)
//        println(innerBorderLen + innerArea)

        return innerArea + connectionLength + vertices.size.toBigInteger()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    check(part1(testInput) == 62.toBigInteger())
    check(part2(testInput) == 952408144115.toBigInteger())

    val input = readInput("Day18")
    part1(input).println()
    part2(input).println()
}
