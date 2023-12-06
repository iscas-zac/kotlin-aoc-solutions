fun main() {

    fun part1(input: List<String>): Int {
        val times = input[0].substring(5).split(" ").filter(String::isNotEmpty).map(String::toInt)
        val dists = input[1].substring(9).split(" ").filter(String::isNotEmpty).map(String::toInt)
//        print(times)
        return times.zip(dists).map { (time, dist) -> (0..time).map { it * (time - it) }.filter { it > dist }.size }
            .fold(1) { acc, i -> acc * i }
    }

    fun part2(input: List<String>): Int {
        val time = input[0].substring(5).split(" ").filter(String::isNotEmpty).joinToString("").toInt()
        val dist = input[1].substring(9).split(" ").filter(String::isNotEmpty).joinToString("").toBigInteger()
//        println("dist:" + dist)
//        println(time)
//        println((0..time).map { it * (time - it) }.filter { it.toBigInteger() > dist }.size)
        return (0..time).map { it.toBigInteger() * (time - it).toBigInteger() }.filter { it > dist }.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 288)
    check(part2(testInput) == 71503)

    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}
