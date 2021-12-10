package aoc21

import utils.PuzzleInputWriter
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet


fun main(args: Array<String>) {
    val sessionId = args[0]
    val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
//    val lines = File("src/main/resources/aoc21/day10_test.txt").readLines()
    val lines = PuzzleInputWriter(sessionId).writeDayPuzzleToFile("2021", day).readLines()
    day(lines)
}

fun day(lines: List<String>) {
    val scores = lines.map { l -> computeScore(l) }.filter { i -> i>0 }.sorted()
    println(scores[scores.size/2])
}

fun computeScore(l: String):Long {
    val open = listOf('(', '[', '{', '<')
    val close = listOf(')', ']', '}', '>')
    val score = listOf(3, 57, 1197, 25137)
    val stack = ArrayDeque<Int>()
    var tot = 0L
    for (c in l) {
        val opening = open.indexOf(c)
        if(opening != -1) {
            // open char
            stack.push(opening)
        } else {
            val closing = close.indexOf(c)
            val expected = stack.pop()
            if (closing != expected) {
                //corrupted
    //                println("found $c instead of "+ open[expected])
    //                tot+= score[closing]
                return -1
            }
        }

    }
    for (index in stack) {
        tot = tot*5 + (index+1)
    }
    return tot
}

fun day9(lines: List<String>) {
//    println( lines.flatMapIndexed{ j, l -> l.mapIndexed{ i, nb -> riskLevel(i, j, nb.toString().toInt(), lines) }}.sum())
    val lowestPoints = lines.flatMapIndexed { y, l -> l.mapIndexed { i, nb ->
        if(isLowest(i, y, nb.toString().toInt(), lines)) Point(i, y, nb.toString().toInt()) else null
     }
    }.filterNotNull()
    println(lowestPoints)
    val sortedBasinSize = lowestPoints.map { p -> p.basinSize(lines) }.sortedDescending()
    println(sortedBasinSize)
    println(sortedBasinSize[0]*sortedBasinSize[1]*sortedBasinSize[2])
}

class Point(val x: Int, val y: Int, val nb: Int) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Point

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }

    fun basinSize(lines: List<String>) :Int{
        println("Compute for $this")
        val explored = java.util.HashSet<Point>()
        var toVisit = ArrayDeque<Point>()
        toVisit.addAll(adjacentPoints(lines).filter { p -> p.nb != 9 })
        while (!toVisit.isEmpty()) {
            val current = toVisit.pop()
            explored.add(current)
            val filter = current.adjacentPoints(lines).filter { p -> p.nb != 9 }.filter { p -> !explored.contains(p) }
            println("visiting $current $filter")
            filter.forEach { p -> toVisit.push(p) }
        }
        println("$this  "+explored)
        return explored.size
    }
    fun adjacentPoints(lines: List<String>):List<Point> {
        val res = ArrayList<Point>()
        // left
        val line = lines[y]
        if(x != 0) res.add(Point(x-1 ,y, line[x-1].toString().toInt()))
        //right
        if(x < line.length-1) res.add(Point(x+1 ,y, line[x+1].toString().toInt()))
        //up
        if(y != 0) res.add(Point(x, y-1, lines[y-1][x].toString().toInt()))
        //down
        if(y < lines.size-1) res.add(Point(x, y+1, lines[y+1][x].toString().toInt()))
//        println("$this....$res")
        return res
    }

    override fun toString(): String {
        return "($x,$y, nb=$nb)"
    }
}

fun isLowest(index:Int, lineNb:Int, nb:Int, lines: List<String>):Boolean {
    val line = lines[lineNb]
    var isLowest = true
    // left
    if(isLowest && index != 0) {
        val left = line[index - 1].toString().toInt()
        isLowest = nb < left
    }
    //right
    if(isLowest && index < line.length-1) {
        val right = line[index + 1].toString().toInt()
        isLowest = nb <right
    }
    //up
    if(isLowest && lineNb != 0) {
        val up = lines[lineNb - 1][index].toString().toInt()
        isLowest = nb < up
    }
    //down
    if(isLowest && lineNb < lines.size-1) {
        val down = lines[lineNb + 1][index].toString().toInt()
        isLowest = nb < down
    }
    return isLowest
}
fun riskLevel(index:Int, lineNb:Int, nb:Int, lines: List<String>):Int {
    return if(isLowest(index, lineNb, nb, lines)) nb+1 else 0;
}
fun day8(lines: List<String>) {
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
