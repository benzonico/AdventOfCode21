package aoc21

import utils.PuzzleInputWriter
import java.util.*
import com.github.ajalt.mordant.rendering.TextColors.*
import java.io.File

// https://github.com/ajalt/mordant text formatting
typealias Path = List<String>
typealias Point = Pair<Int, Int>

fun main(args: Array<String>) {
    val sessionId = args[0]
    val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    val puzzleInputWriter = PuzzleInputWriter(sessionId)
//    val lines = File("src/main/resources/aoc21/day13_test.txt").readLines()
    val lines = puzzleInputWriter.writeDayPuzzleToFile("2021", day).readLines()
    day(lines)
}

fun day(lines: List<String>) {
    var mut = emptyList<Point>().toMutableList()
    var instr = emptyList<String>().toMutableList()
    var instrLine= false
    for (l in lines) {
        if(l.isBlank()) {
            instrLine = true
            continue
        }
        if(!instrLine) mut.add(l.split(",").let { s -> Point(s[0].toInt(), s[1].toInt()) })
        else instr.add(l)
    }
    val instructions = instr.map { l -> l.split("=").let { s -> Pair(s[0].last()=='x', s[1].toInt()) } }
    println(instructions)
    //fold along x=655
    var points = mut.toList()
    for (i in instructions) {
       points = if(i.first) foldLeft(i.second, points) else foldUp(i.second, points)
    }
    val maxX = points.maxOf { p -> p.first }
    val maxY = points.maxOf { p-> p.second }
    val matrix:MutableList<MutableList<String>> = emptyList<MutableList<String>>().toMutableList()
    for(y in 0..maxY ) {
        val ligne = emptyList<String>().toMutableList()
        matrix.add(ligne)
        for (x in 0..maxX) {
            ligne.add(".")
        }
    }
    points.forEach { p -> matrix[p.second][p.first] = "#" }

    println(points)
    fun toStr(matrix: MutableList<MutableList<String>>): String {
        return matrix.joinToString("\n") { l ->
            l.map { i -> if (i == "#") red(i.toString())  else i }.joinToString("")
        }
    }
    //pghzbfjc
    println(toStr(matrix))
}
fun foldUp(fold:Int, points:List<Point>):List<Point>{
    return points.map { p -> if(p.second>fold) Point(p.first, 2*fold-p.second) else p }.toSet().toList()
}

fun foldLeft(fold:Int, points:List<Point>):List<Point>{
    return points.map { p -> if(p.first>fold) Point(2*fold-p.first, p.second) else p }.toSet().toList()
}
fun day12(lines: List<String>) {
    val immutGraph = lines.map { l -> l.split("-") }.groupBy({ a -> a[0] }, { a -> a[1] })
    val graph = immutGraph.toMutableMap()


    for (entry in immutGraph) {
        entry.value.forEach { v ->
            val keyLookup = graph.get(v)
            if (keyLookup == null) {
                graph[v] = listOf(entry.key)
            } else if (!keyLookup.contains(entry.key)) {
                val toMutableList = keyLookup.toMutableList()
                toMutableList.add(entry.key)
                graph[v] = toMutableList
            }
        }
    }
    println(graph)
    fun successors(name: String): List<String> {
        return graph.getOrDefault(name, emptyList())
    }
    fun canVisit(p: Path, name: String): Boolean {
        val filter = p.filter { s -> s.toCharArray().all { c -> c.isLowerCase() }}
        return if(filter.toSet().size == filter.size) true else !p.contains(name)
    }

    // path from start  = start + path from successors
    //
    //
    fun appendToPath(p:Path, s:String): List<String> {
        val toMutableList = p.toMutableList()
        toMutableList.add(s)
        return toMutableList
    }
    fun pathFrom(name: String, currentPaths:List<Path>): List<Path> {
        val paths = if(currentPaths.isEmpty()) listOf(listOf("start")) else currentPaths.map { p -> appendToPath(p, name) }
        println(paths)
        if(name == "end") {
            return paths
        }
        val successors = successors(name)
        return paths.flatMap { p ->
            successors.filter { succ -> succ!="start" && (succ.toCharArray().all { c-> c.isUpperCase()} ||(succ.toCharArray().all { c-> c.isLowerCase()} && canVisit(p, succ))) }
                .flatMap { succ -> pathFrom(succ, paths)} }
    }


/*    fun computePathFrom(name: String, paths: List<Path>): List<Path> {
        val somePaths = emptyList<Path>().toMutableList()
        val successors = successors(name)
        successors.forEach { s ->
            if (s == "start") {
                // do nothing, back to start.
            } else if (s == "end") {
                paths.forEach { l ->
                    val toMutableList = l.toMutableList()
                    toMutableList.add(s)
                    somePaths.add(toMutableList.toList())
                }
            } else {
                paths.forEach { p ->
                    val c = s.toCharArray()[0];
                    if (c.isUpperCase() || (c.isLowerCase() && canVisit(p, s))) {
                        val pathToExplore = emptyList<Path>().toMutableList()
                        val toMutableList = p.toMutableList()
                        toMutableList.add(s)
                        pathToExplore.add(toMutableList.toList())
                        somePaths.addAll(computePathFrom(s, pathToExplore.toList()))
                    }
                }
            }
        }
        return somePaths
    }*/

//    val paths = computePathFrom("start", listOf(listOf("start")))
    val paths = pathFrom("start", emptyList())
    println(paths)
    println(paths.size)
}


fun day11(lines: List<String>) {
    var parse = lines.map { l -> l.toCharArray().map { c -> c.toString().toInt() } }
    var matrix = parse.map { l -> l.toMutableList() }.toMutableList()
    fun sync(matrix: MutableList<MutableList<Int>>): Boolean {
        return matrix.all { l -> l.all { i -> i == 0 } }
    }
    fun step(matrix: MutableList<MutableList<Int>>): Pair<MutableList<MutableList<Int>>, Int> {
        val toUpdate = ArrayDeque<Pair<Int, Int>>()
        toUpdate.addAll((0..9).toList().flatMap { x -> (0..9).toList().map { y -> Pair(x, y) } })
        var tot = 0
        val flashed = HashSet<Pair<Int, Int>>()
        while (!toUpdate.isEmpty()) {
            val pop = toUpdate.pop()
            if (flashed.contains(pop)) continue
            val value = matrix[pop.first][pop.second]
            val newVal = if (value + 1 < 10) value + 1 else 0
            matrix[pop.first][pop.second] = newVal
            if (newVal == 0) {
                tot++
                flashed.add(pop)
                (pop.first - 1..pop.first + 1).forEach { x ->
                    (pop.second - 1..pop.second + 1).forEach { y ->
                        val element = Pair(x, y)
                        if ((0..9).contains(x) && (0..9).contains(y) && !flashed.contains(element)) toUpdate.push(element)
                    }
                }
            }
        }
        return Pair(matrix, tot)
    }
    var tot = 0
    var step = Pair(matrix, 0)
    for (i in 1..100) {
        step = step(matrix)
        matrix = step.first
        val message = toStr(matrix)
        println(message)
        println("------------")
        tot += step.second
//        println(tot)
    }
    println(tot)

//    var step = 0
//    while (!sync(matrix)) {
//        matrix = step(matrix).first
//        val message = toStr(matrix)
//        print(message+"\r")
//        step++
//    }
//    println(step)

}

fun toStr(matrix: MutableList<MutableList<Int>>): String {
    return matrix.joinToString("\n") { l ->
        l.map { i -> if (i == 0) red(i.toString())  else i }.joinToString("")
    }
}

fun day10(lines: List<String>) {
    val scores = lines.map { l -> computeScore(l) }.filter { i -> i > 0 }.sorted()
    println(scores[scores.size / 2])
}

fun computeScore(l: String): Long {
    val open = listOf('(', '[', '{', '<')
    val close = listOf(')', ']', '}', '>')
    val score = listOf(3, 57, 1197, 25137)
    val stack = ArrayDeque<Int>()
    var tot = 0L
    for (c in l) {
        val opening = open.indexOf(c)
        if (opening != -1) {
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
        tot = tot * 5 + (index + 1)
    }
    return tot
}

fun day9(lines: List<String>) {
//    println( lines.flatMapIndexed{ j, l -> l.mapIndexed{ i, nb -> riskLevel(i, j, nb.toString().toInt(), lines) }}.sum())
    val lowestPoints = lines.flatMapIndexed { y, l ->
        l.mapIndexed { i, nb ->
            if (isLowest(i, y, nb.toString().toInt(), lines)) Point9(i, y, nb.toString().toInt()) else null
        }
    }.filterNotNull()
    println(lowestPoints)
    val sortedBasinSize = lowestPoints.map { p -> p.basinSize(lines) }.sortedDescending()
    println(sortedBasinSize)
    println(sortedBasinSize[0] * sortedBasinSize[1] * sortedBasinSize[2])
}

class Point9(val x: Int, val y: Int, val nb: Int) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Point9

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }

    fun basinSize(lines: List<String>): Int {
        println("Compute for $this")
        val explored = java.util.HashSet<Point9>()
        var toVisit = ArrayDeque<Point9>()
        toVisit.addAll(adjacentPoints(lines).filter { p -> p.nb != 9 })
        while (!toVisit.isEmpty()) {
            val current = toVisit.pop()
            explored.add(current)
            val filter = current.adjacentPoints(lines).filter { p -> p.nb != 9 }.filter { p -> !explored.contains(p) }
            println("visiting $current $filter")
            filter.forEach { p -> toVisit.push(p) }
        }
        println("$this  " + explored)
        return explored.size
    }

    fun adjacentPoints(lines: List<String>): List<Point9> {
        val res = ArrayList<Point9>()
        // left
        val line = lines[y]
        if (x != 0) res.add(Point9(x - 1, y, line[x - 1].toString().toInt()))
        //right
        if (x < line.length - 1) res.add(Point9(x + 1, y, line[x + 1].toString().toInt()))
        //up
        if (y != 0) res.add(Point9(x, y - 1, lines[y - 1][x].toString().toInt()))
        //down
        if (y < lines.size - 1) res.add(Point9(x, y + 1, lines[y + 1][x].toString().toInt()))
//        println("$this....$res")
        return res
    }

    override fun toString(): String {
        return "($x,$y, nb=$nb)"
    }
}

fun isLowest(index: Int, lineNb: Int, nb: Int, lines: List<String>): Boolean {
    val line = lines[lineNb]
    var isLowest = true
    // left
    if (isLowest && index != 0) {
        val left = line[index - 1].toString().toInt()
        isLowest = nb < left
    }
    //right
    if (isLowest && index < line.length - 1) {
        val right = line[index + 1].toString().toInt()
        isLowest = nb < right
    }
    //up
    if (isLowest && lineNb != 0) {
        val up = lines[lineNb - 1][index].toString().toInt()
        isLowest = nb < up
    }
    //down
    if (isLowest && lineNb < lines.size - 1) {
        val down = lines[lineNb + 1][index].toString().toInt()
        isLowest = nb < down
    }
    return isLowest
}

fun riskLevel(index: Int, lineNb: Int, nb: Int, lines: List<String>): Int {
    return if (isLowest(index, lineNb, nb, lines)) nb + 1 else 0;
}

fun day8(lines: List<String>) {
    //                               1                        4                         7        8
    val numbers = listOf("abcefg", "cf", "acdeg", "acdfg", "bcdf", "abdfg", "abdefg", "acf", "abcdefg", "abcdfg")
    val parsedLines = lines.map { s -> OneInput.create(s) }
    println(parsedLines.map { i -> i.toNumber() }.sum())
//    val count = flatMap.count { l -> isUniqueDigit(l.length) }
//    println(count)
}

class OneInput(val digits: List<String>, val display: List<String>) {
    //                               1                        4                         7        8
    val numbers = listOf("abcefg", "cf", "acdeg", "acdfg", "bcdf", "abdfg", "abdefg", "acf", "abcdefg", "abcdfg")

    companion object {
        fun create(l: String): OneInput {
            val split = l.split("|")
            return OneInput(
                split[0].split(" ").filter { s -> s.isNotBlank() },
                split[1].split(" ").filter { s -> s.isNotBlank() })
        }
    }

    fun toNumber(): Int {
        val map =
            digits.associateBy({ d -> d.toCharArray().sortedArray().joinToString("") }, { d -> knownDigit(d, digits) })
                .toMap()
        return display.map { d -> map[d.toCharArray().sortedArray().joinToString("")] }.joinToString("").toInt()
    }

    private fun knownDigit(d: String, digits: List<String>): Int {
        if (isUniqueDigit(d.length)) {
            return numbers.indexOfFirst { l -> l.length == d.length }
        }
        val findOne = digits.first { nb -> nb.length == 2 }
        val findFour = digits.first { nb -> nb.length == 4 }
        if (d.length == 6) {
            // 9 6 0
            return if (containsEachChar(d, findOne)) (if (containsEachChar(d, findFour)) 9 else 0) else 6

        } else {
            return if (containsEachChar(d, findOne)) 3 else if (commonWithFour(d, findFour) == 2) 2 else 5
        }
        return -1
    }

    private fun commonWithFour(d: String, findFour: String): Int {
        return d.toCharArray().filter { c -> findFour.contains(c) }.count()
    }

    fun containsEachChar(s: String, sub: String): Boolean {
        return sub.toCharArray().all { c -> s.contains(c) }
    }

    override fun toString(): String {
        return "OneInput(digits=$digits, display=$display)"
    }

}

fun isUniqueDigit(length: Int): Boolean {
    return length == 2 || length == 3 || length == 4 || length == 7
}
