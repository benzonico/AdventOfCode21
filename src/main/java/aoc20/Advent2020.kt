package aoc20

import utils.PuzzleInputWriter


fun main(args: Array<String>) {
//    val sessionId = args[0]
//    val puzzleInputWriter = PuzzleInputWriter(sessionId)
//    for(i in 1..25){
//        puzzleInputWriter.writeDayPuzzleToFile("2020", i)
//    }
    day3()
}


fun day4() {
    val lines = getResourceAsText("day4.txt").split("\r\n")
}

fun day3() {
    val lines = getResourceAsText("day3.txt").split("\r\n")
    println(countTrees(lines, 1, 1) * countTrees(lines, 3, 1) * countTrees(lines, 5, 1) * countTrees(lines, 7, 1) * countTrees(lines, 1, 2))
}


private fun countTrees(lines: List<String>, horiz: Int, vert: Int) =
        lines.filterIndexed { index, s -> index % vert == 0 }.mapIndexed { index, l -> l[(index * horiz) % l.length] }.count { c -> c == '#' }.toLong()

fun day2() {
    val passwords = getResourceAsText("day2.txt").split("\r\n")
    println(passwords.map { s -> Pwd.create(s) }.count { p -> p.isValid() })
}

class Pwd(val value: String, val letter: String, val pos1: Int, val pos2: Int) {

    fun isValid(): Boolean {
        return (pos1 < value.length && letter.contains(value[pos1])).xor(pos2 < value.length && letter.contains(value[pos2]));
    }

    companion object {
        fun create(s: String): Pwd {
            val split = s.split(" ")
            val rangeString = split[0].split("-")
            return Pwd(split[2], split[1].substring(0, 1), rangeString[0].toInt() - 1, rangeString[1].toInt() - 1)
        }
    }
}

fun day1() {
    val expenses = getResourceAsText("day1.txt").split("\r\n")
            .filter { s -> s.isNotBlank() }
            .map { s -> Integer.parseInt(s) }
    for (expense in expenses) {
        for (expense2 in expenses) {
            for (expense3 in expenses) {
                if (expense + expense2 + expense3 == 2020) {
                    println(expense * expense2 * expense3)
                }
            }
        }
    }
}

fun getResourceAsText(path: String): String {
    return {}::class.java.getResource(path).readText()
}
