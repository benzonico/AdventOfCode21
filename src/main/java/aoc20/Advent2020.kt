package aoc20

import utils.PuzzleInputWriter
import java.io.File


fun main(args: Array<String>) {
    val sessionId = args[0]
    val puzzleInputWriter = PuzzleInputWriter(sessionId)
    val day = 6
//    val lines = listOf("FBFBBFFRLR")
//    val lines = File("src/main/resources/aoc20/day"+day+"_test.txt").readLines()
    val lines = puzzleInputWriter.linesOfDay("2020", day)
    day6(lines)
}

fun day6(lines: List<String>) {
    var currentAnswer:Iterable<Char>? = null
    var res = 0
    for (line in lines) {
        if (line.isBlank()) {
            val size = currentAnswer!!.toList().size
            res += size
            currentAnswer = null
        } else {
            currentAnswer = currentAnswer?.intersect(line.toSet()) ?: line.toList()
        }
    }
    res+=currentAnswer!!.toList().size
    println(res)
}

fun day5(lines: List<String>) {
    println(lines.maxOf { l -> seatId(l) })
    val seatIds = lines.map { l -> seatId(l) }
    println(seatIds.filter { i -> !seatIds.contains(i - 1) })
    println(seatIds.filter { i -> !seatIds.contains(i + 1) })


}

fun seatId(s: String): Int {
    val seat = seat(s)
    return seat.first * 8 + seat.second
}

fun seat(s: String): Pair<Int, Int> {
    var range = 0..127
    s.substring(0, 7).forEach { c -> range = reduceRange(range, c, 'F') }
    val row = range.first
    range = 0..7
    s.substring(7, s.length).forEach { c -> range = reduceRange(range, c, 'L') }
    return Pair(row, range.first)
}

fun reduceRange(intRange: IntRange, c: Char, rowOrSeat: Char): IntRange {
    val half = intRange.first + (intRange.last - intRange.first) / 2
    return if (intRange.first == intRange.last) intRange else if (c == rowOrSeat) IntRange(intRange.first, half) else IntRange(half + 1, intRange.last)
}

fun day4(lines: List<String>) {
    var currentPassport = Passport()
    val passports = ArrayList<Passport>()
    passports.add(currentPassport)
    for (line in lines) {
        if (line.isBlank()) {
            currentPassport = Passport()
            passports.add(currentPassport)
        } else {
            currentPassport.addFields(line)
        }
    }
    println(passports.count { p -> p.hasValidNumberOfField() && p.validateFields() })
}

class Passport {
    var myMap = HashMap<String, String>()
    fun addFields(s: String) {
        myMap.putAll(s.split(" ").map { e -> e.split(":") }.associateBy({ e -> e[0] }, { e -> e[1] }))
    }

    fun hasValidNumberOfField(): Boolean {
        return myMap.size == 8 || (myMap.size == 7 && !myMap.keys.contains("cid"))
    }

    fun validateFields(): Boolean {
        return myMap.all(::validateField)
    }

    private fun validateField(entry: Map.Entry<String, String>): Boolean {
        val value = entry.value
        val res = when (entry.key) {
            "byr" -> value.matches(Regex("\\d{4}")) && (1920..2002).contains(value.toInt())
            "iyr" -> value.matches(Regex("\\d{4}")) && (2010..2020).contains(value.toInt())
            "eyr" -> value.matches(Regex("\\d{4}")) && (2020..2030).contains(value.toInt())
            "hgt" -> value.matches(Regex("\\d+(cm|in)")) && run {
                val toInt = value.substring(0, value.length - 2).toInt()
                if (value.endsWith("cm")) (150..193).contains(toInt) else (59..76).contains(toInt)
            }
            "hcl" -> value.startsWith("#") && value.substring(1, value.length).matches(Regex("[0-9a-f]{6}"))
            "ecl" -> listOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth").contains(value)
            "pid" -> value.matches(Regex("\\d{9}"))
            "cid" -> true
            else -> false
        }
        if (!res) println(entry.key + "  " + entry.value)
        return res
    }


}

fun day3(day3: File) {
    val lines = day3.readLines()
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
