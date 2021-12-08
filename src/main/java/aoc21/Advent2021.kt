package aoc21

import utils.PuzzleInputWriter
import java.io.File
import java.util.*


fun main(args: Array<String>) {
    val sessionId = args[0]
    val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
//    val file = File("src/main/resources/aoc21/day8_test.txt")
    val lines = PuzzleInputWriter(sessionId).writeDayPuzzleToFile("2021", day).readLines()
    day(lines)
}

fun day(lines: List<String>) {
    //                               1                        4                         7        8
    val numbers = listOf("abcefg", "cf", "acdeg", "acdfg", "bcdf", "abdfg", "abdefg", "acf", "abcdefg", "abcdfg")
    val parsedLines = lines.map { s -> OneInput.create(s)}
    println(parsedLines.map{i -> i.toNumber()}.sum())
//    val count = flatMap.count { l -> isUniqueDigit(l.length) }
//    println(count)
}
class OneInput(val digits:List<String>, val display:List<String>) {
//                               1                        4                         7        8
    val numbers = listOf("abcefg", "cf", "acdeg", "acdfg", "bcdf", "abdfg", "abdefg", "acf", "abcdefg", "abcdfg")
    companion object {
        fun create(l:String):OneInput {
            val split = l.split("|")
            return OneInput(split[0].split(" ").filter{s->s.isNotBlank()},split[1].split(" ").filter{s->s.isNotBlank()} )
        }
    }

    fun toNumber():Int{
        val map = digits.associateBy({d->d.toCharArray().sortedArray().joinToString("")},{ d->knownDigit(d, digits) }).toMap()
        return display.map { d -> map[d.toCharArray().sortedArray().joinToString("")] }.joinToString("").toInt()
    }

    private fun knownDigit(d: String, digits: List<String>):Int {
        if(isUniqueDigit(d.length)) {
            return numbers.indexOfFirst { l-> l.length==d.length }
        }
        val findOne = digits.first { nb -> nb.length == 2 }
        val findFour = digits.first { nb -> nb.length == 4 }
        if(d.length == 6) {
            // 9 6 0
            return if(containsEachChar(d, findOne)) (if(containsEachChar(d,findFour)) 9 else 0)  else 6

        } else {
            return if(containsEachChar(d, findOne)) 3 else if(commonWithFour(d, findFour)==2) 2 else 5
        }
        return -1
    }

    private fun commonWithFour(d: String, findFour: String): Int {
        return d.toCharArray().filter { c -> findFour.contains(c) }.count()
    }

    fun containsEachChar(s:String, sub:String):Boolean {
        return sub.toCharArray().all { c -> s.contains(c) }
    }

    override fun toString(): String {
        return "OneInput(digits=$digits, display=$display)"
    }

}

fun isUniqueDigit(length: Int):Boolean {
    return length == 2 ||length==3 ||length==4 ||length==7
}
