import java.math.BigInteger
import kotlin.math.absoluteValue

fun main() {
    fun part1(input: List<String>): BigInteger {
        val records = input.map { it.map { mutableMapOf<String, Int>() } }
        records[0][1]["e1"] = input[0][1].digitToInt()
        records[1][0]["s1"] = input[1][0].digitToInt()
        val width = input[0].length
        val height = input.size

        fun goInDirection(xLoc: Int, yLoc: Int, dir: String, state: String): Boolean {
            if (yLoc == 0 && dir == "w") return false
            if (yLoc == width - 1 && dir == "e") return false
            if (xLoc == 0 && dir == "n") return false
            if (xLoc == height - 1 && dir == "s") return false
            if (state[0] == dir[0] && state[1] == '3') return false
            if (state[0] == 'n' && dir == "s") return false
            if (state[0] == 's' && dir == "n") return false
            if (state[0] == 'w' && dir == "e") return false
            if (state[0] == 'e' && dir == "w") return false

            return true
        }
        val workList = mutableSetOf(Pair(0, 1), Pair(1, 0))
        fun update(xLoc: Int, yLoc: Int, dir: String, state: String) {
            val dirVec = when (dir) {
                "w" -> Pair(-1, 0)
                "e" -> Pair(1, 0)
                "n" -> Pair(0, -1)
                "s" -> Pair(0, 1)
                else -> Pair(0, 0)
            }
            val newX = xLoc + dirVec.second
            val newY = yLoc + dirVec.first
            val targetState = if (state[0] == dir[0]) dir + (state[1].digitToInt() + 1).digitToChar()
            else dir + "1"
            val attempt = records[xLoc][yLoc][state]!! + input[newX][newY].digitToInt()
            val check = records[newX][newY].keys.filter { it.startsWith(targetState[0]) && it[1].digitToInt() <= targetState[1].digitToInt() }
                .map { records[newX][newY][it]!! }
                .any { it <= attempt }
            if (!check) {
                records[newX][newY][targetState] = attempt
                workList += Pair(newX, newY)
            }
        }
        var i = 0
        while (workList.isNotEmpty()) {
            val head = workList.first()
            workList.remove(head)
            for ((state, _) in records[head.first][head.second]) {
                for (dir in listOf("w", "e", "n", "s"))
                    if (goInDirection(head.first, head.second, dir, state))
                        update(head.first, head.second, dir, state)
            }
//            for (dir in listOf("w", "e", "n", "s")) {
//                val minLossInOtherDirections = records[head.first][head.second].keys.filter { it[0] != dir[0] }
//                    .minOfOrNull { records[head.first][head.second][it]!! }
//                if (goInDirection(head.first, head.second, dir, state))
//                    update(head.first, head.second, dir, state)
//            }
            println("$i, ${workList.size}")
            println(records.map { it.map { it.values.minOrNull() ?: -1 } }.joinToString("\n") + "\n")
            i++
        }
        return records[height-1][width-1].values.min().toBigInteger()
    }

    fun part2(input: List<String>): BigInteger {
        val records = input.map { it.map { mutableMapOf<Pair<Int, Int>, Int>() } }
        records[0][1][Pair(0, 1)] = input[0][1].digitToInt()
        records[1][0][Pair(1, 0)] = input[1][0].digitToInt()
        val width = input[0].length
        val height = input.size

        val workList = mutableSetOf(Pair(0, 1), Pair(1, 0))
        var i = 0
        while (workList.isNotEmpty()) {
            val head = workList.first()
            workList.remove(head)
            for (dir in listOf(Pair(1, 0),
                Pair(0, 1),
                Pair(-1, 0),
                Pair(0, -1))) {
                if (head.first + dir.first < 0) continue
                if (head.first + dir.first >= height) continue
                if (head.second + dir.second < 0) continue
                if (head.second + dir.second >= width) continue
                val thisRecord = records[head.first][head.second]
                val targetRecord = records[head.first + dir.first][head.second + dir.second]
                val eligibleStraightKeys = thisRecord.keys
                    .filter {
                        val prod = it.first * dir.first + it.second * dir.second
                        val trace = it.first.absoluteValue + it.second.absoluteValue
                        (prod > 0 && trace < 10)
                    }
                    for (key in eligibleStraightKeys) {
                        val newKey = Pair(key.first + dir.first, key.second + dir.second)
                        val attempt = thisRecord[key]!! + input[head.first + dir.first][head.second + dir.second].digitToInt()
                        if (!targetRecord.containsKey(newKey) || targetRecord[newKey]!! > attempt) {
                            targetRecord[newKey] = attempt
                            workList.add(Pair(head.first + dir.first, head.second + dir.second))
                        }
                    }

                val eligibleTurnMin = thisRecord.keys
                    .filter {
                        val prod = it.first * dir.first + it.second * dir.second
                        val trace = it.first.absoluteValue + it.second.absoluteValue
                        (prod == 0 && trace > 3)
                    }.minOfOrNull { thisRecord[it]!! }
                if (eligibleTurnMin == null) continue
                val attempt = eligibleTurnMin + input[head.first + dir.first][head.second + dir.second].digitToInt()
                if (!targetRecord.containsKey(dir) || targetRecord[dir]!! > attempt) {
                    targetRecord[dir] = attempt
                    workList.add(Pair(head.first + dir.first, head.second + dir.second))
                }
            }
            println("$i, ${workList.size}")
            println(records.map { it.map { it.values.minOrNull() ?: -1 } }.joinToString("\n") + "\n")
            i++
        }

        return records[height-1][width-1].values.min().toBigInteger()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day17_test")
    check(part1(testInput) == 102.toBigInteger())
    check(part2(testInput) == 94.toBigInteger())

    val input = readInput("Day17")
//    part1(input).println()
    part2(input).println()
}
