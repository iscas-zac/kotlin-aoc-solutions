import java.math.BigInteger

fun main() {
    fun getSeeds(input: String) = input.split(": ")[1]
        .split(" ")
        .map(String::toBigInteger)

    fun getSeedRanges(input: String) = getSeeds(input).withIndex()
        .partition { (ind, _) -> ind and 1 != 1 }
        .let { it ->
            it.first.map{ indPair -> indPair.value }.zip(it.second.map { indPair -> indPair.value })
        }

    fun getMaps(input: List<String>): List<List<List<BigInteger>>> {
        val seed2SoilStart = input.indexOfFirst { it.startsWith("seed-to-soil") }
        val soil2FertStart = input.indexOfFirst { it.startsWith("soil-to-fertilizer") }
        val fert2WaterStart = input.indexOfFirst { it.startsWith("fertilizer-to-water") }
        val water2LightStart = input.indexOfFirst { it.startsWith("water-to-light") }
        val light2TempStart = input.indexOfFirst { it.startsWith("light-to-temperature") }
        val temp2HumidStart = input.indexOfFirst { it.startsWith("temperature-to-humidity") }
        val humid2LocStart = input.indexOfFirst { it.startsWith("humidity-to-location") }

        val maps = listOf(
            input.slice(seed2SoilStart+1..soil2FertStart-2)
                .map { it.split(" ").map(String::toBigInteger) },
            input.slice(soil2FertStart+1..fert2WaterStart-2)
                .map { it.split(" ").map(String::toBigInteger) },
            input.slice(fert2WaterStart+1..water2LightStart-2)
                .map { it.split(" ").map(String::toBigInteger) },
            input.slice(water2LightStart+1..light2TempStart-2)
                .map { it.split(" ").map(String::toBigInteger) },
            input.slice(light2TempStart+1..temp2HumidStart-2)
                .map { it.split(" ").map(String::toBigInteger) },
            input.slice(temp2HumidStart+1..humid2LocStart-2)
                .map { it.split(" ").map(String::toBigInteger) },
            input.slice(humid2LocStart+1..<input.size)
                .map { it.split(" ").map(String::toBigInteger) },

        )

        return maps
    }


    fun part1(input: List<String>): BigInteger {
        val seeds = getSeeds(input[0])
        val maps = getMaps(input)
        var locs = seeds
        for (mapping in maps) {
            locs = locs.map { loc ->
                val range = mapping.find { loc < it[1] + it[2] && loc >= it[1] }
                if (range == null) loc
                else loc - range[1] + range[0]
            }
        }
//        print(locs)
        return locs.min()
    }

    fun locThruMapping(locRange: Pair<BigInteger, BigInteger>, mapRange: Triple<BigInteger, BigInteger, BigInteger>): Pair<List<Pair<BigInteger, BigInteger>>, List<Pair<BigInteger, BigInteger>>> {
        if (locRange.first >= mapRange.second + mapRange.third ||
                locRange.first + locRange.second <= mapRange.second)
            return Pair(listOf(locRange), listOf())
        val remains = mutableListOf<Pair<BigInteger, BigInteger>>()
        var low = locRange.first
        if (locRange.first < mapRange.second) {
            remains.add(Pair(locRange.first, mapRange.second - locRange.first))
            low = mapRange.second
        }
        var high = locRange.first + locRange.second
        if (locRange.first + locRange.second > mapRange.second + mapRange.third) {
            remains.add(Pair(mapRange.second + mapRange.third, locRange.first + locRange.second - (mapRange.second + mapRange.third)))
            high = mapRange.second + mapRange.third
        }
//        print(remains)
        return Pair(remains, listOf(Pair(low - mapRange.second + mapRange.first, high - low)))
    }

    fun part2(input: List<String>): BigInteger {
        val seedRanges = getSeedRanges(input[0])
        val maps = getMaps(input)
        var locRanges = seedRanges
        for (mapping in maps) {
            locRanges = locRanges.flatMap { locRange ->
                var worklist = listOf(locRange)
                val result = mutableListOf<Pair<BigInteger, BigInteger>>()
                for (mapRange in mapping) {
                    val pairs = worklist.map { locThruMapping(it, Triple(mapRange[0], mapRange[1], mapRange[2])) }
                    worklist = pairs.flatMap { it.first }
                    result.addAll(pairs.flatMap { it.second })
                }
                result.addAll(worklist)
                result
            }
        }

        return locRanges.minOf { it.first }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == (35).toBigInteger())
    check(part2(testInput) == (46).toBigInteger())

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}
