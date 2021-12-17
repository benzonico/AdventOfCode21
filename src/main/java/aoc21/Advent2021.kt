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
//    val lines = File("src/main/resources/aoc21/day"+da//y+"_test.txt").readLines()
    val lines = puzzleInputWriter.writeDayPuzzleToFile("2021", day).readLines()
    day(lines)
}

fun day(lines: List<String>) {

}
fun day16(lines: List<String>) {
    lines.forEach { l ->
        val binaryStr = l.toCharArray().map { c -> Integer.toBinaryString(c.toString().toInt(16)) }
            .joinToString("") { b -> b.padStart(4, '0') }

        class Packet {
            val subPacket: MutableList<Packet> = emptyList<Packet>().toMutableList()
            var literal: Long = 0
            var version: Int = 0
            var type: Int = 0
            override fun toString(): String {
                return "Packet(version=$version, type=$type, literal=$literal, sub=$subPacket)"
            }

            fun sumVersion(): Int {
                return version + subPacket.sumOf { p -> p.sumVersion() }
            }

            fun calculate():Long {
                return when (type) {
                    0 -> subPacket.sumOf { p -> p.calculate() }
                    1 -> subPacket.map { p -> p.calculate() }.reduce{ l1,l2->l1*l2}
                    2 -> subPacket.minOfOrNull { p -> p.calculate() }?:0
                    3 -> subPacket.maxOfOrNull { p -> p.calculate() }?:0
                    4 -> literal
                    5 -> if(subPacket[0].calculate() > subPacket[1].calculate()) 1 else 0
                    6 -> if(subPacket[0].calculate() < subPacket[1].calculate()) 1 else 0
                    7 -> if(subPacket[0].calculate() == subPacket[1].calculate()) 1 else 0
                    else -> 0
                }
            }

        }

        fun parsePacket(str: String): Pair<Packet, Int> {
            val packet = Packet()
            packet.version = str.substring(0, 3).toInt(2)
            packet.type = str.substring(3, 6).toInt(2)
            var strIndex = 6
            if (packet.type == 4) {
                var currentVal = str.substring(strIndex + 1, strIndex + 5)
                var currentBit = str.substring(strIndex, strIndex + 5)
                strIndex += 5
                while (currentBit[0] != '0') {
                    currentBit = str.substring(strIndex, strIndex + 5)
                    currentVal += currentBit.substring(1)
                    strIndex += 5
                }
                packet.literal = currentVal.toLong(2)
            } else {
                val lTypeId = str[strIndex]
                strIndex++
                if (lTypeId == '0') {
                    val length = str.substring(strIndex, strIndex + 15).toInt(2)
                    strIndex += 15
                    val stop = strIndex + length
                    while (strIndex != stop) {
                        val parseSubPacket = parsePacket(str.substring(strIndex))
                        strIndex += parseSubPacket.second
                        packet.subPacket.add(parseSubPacket.first)
                    }
                } else {
                    val nbSubPacket = str.substring(strIndex, strIndex + 11).toInt(2)
                    strIndex += 11
                    for (i in 1..nbSubPacket) {
                        val parseSubPacket = parsePacket(str.substring(strIndex))
                        strIndex += parseSubPacket.second
                        packet.subPacket.add(parseSubPacket.first)
                    }
                }

            }
            return Pair(packet, strIndex)
        }

        val parsePacket = parsePacket(binaryStr)
        println(parsePacket.first.calculate())
    }


}
fun day15(lines: List<String>) {
    val cave = lines.map { s -> s.toCharArray().map { c -> c.toString().toInt() } }

    println(cave)
    val maxY = cave.size*5-1
    val maxX = cave[0].size*5-1
    val MAX = maxX *9*2

    class Path(val x:Int, val y:Int) {
        fun positionRisk(): Int {
            val hTile = x/cave.size
            val vTile = y/cave.size
            val positionRisk = cave[y-vTile*cave.size][x-hTile*cave.size] + hTile + vTile
            return if(positionRisk > 9) positionRisk -9 else positionRisk
        }
        var risk:Int = MAX
    }
    val allPaths = (0..maxY).map { y -> (0..maxX).map { x-> Path(x,y) } }
    fun getPath(y: Int, x: Int): Path {
        return allPaths[y][x]
    }

    fun neighbours(path:Path):List<Path> {
        val x = path.x
        val y = path.y
        val res = emptyList<Path>().toMutableList()
        if(y != 0) res.add(getPath(y - 1, x))
        if(y!=maxY) res.add(getPath(y + 1, x))
        if(x!=0) res.add(getPath(y, x-1))
        if(x!=maxX) res.add(getPath(y,x+1))
        return res
    }

    fun newPaths(path:Path):List<Path> {
        val neighbours = neighbours(path).filter { n->path.risk+n.positionRisk()<n.risk }
        neighbours.forEach { n -> n.risk = path.risk+n.positionRisk() }
        return neighbours
    }
    val toExplore = listOf(Path(0,0)).toMutableList()
    toExplore[0].risk = 0
    while (toExplore.isNotEmpty()) {
        val currentPath = toExplore.removeAt(0)
        toExplore.addAll(newPaths(currentPath))
    }
    println(allPaths[maxY][maxX].risk)
//    paths.forEach { row -> row.forEach { path -> newPaths(path) }
    //part1 with dijkstra
    //data class Node(val x:Int, val y:Int, val risk:Int)

    /*val nodes = cave.mapIndexed { y, row -> row.mapIndexed { x, e -> Node(x, y, e) } }
    fun neighbours(nodes:List<List<Node>>, node:Node):List<Node> {
        val x = node.x
        val y = node.y
        val res = emptyList<Node>().toMutableList()
        if(y != 0) res.add(nodes[y - 1][x])
        if(y!=maxY) res.add(nodes[y + 1][x])
        if(x!=0) res.add(nodes[y][x-1])
        if(x!=maxX) res.add(nodes[y][x+1])
        return res
    }*/
    /*val weights =  nodes.flatMap { l -> l.flatMap { n -> neighbours(nodes, n).map { v -> Pair(Pair(n, v), v.risk) } } }.toMap()
    val graph = Graph(weights)
    val dijkstra = dijkstra(graph, nodes[0][0])
    println(shortestPath(dijkstra, nodes[0][0], nodes[maxY][maxX]).sumOf { n -> n.risk }-nodes[0][0].risk)*/

}

fun day14(lines: List<String>) {
    val start = lines[0]
    println(start)
    val startPairs = toPairs(start)
    val rules =
        (2 until lines.size).map { i -> lines[i] }.associate { l -> l.split(" -> ").let { s -> Pair(s[0], s[1]) } }
    var polymer = start

    fun newPairs(pair:String, number:Long):Map<String,Long> {
         return toPairs(pair[0].toString() + rules[pair] + pair[1].toString()).groupingBy { it }.eachCount().mapValues { e -> e.value*number }
    }
    fun newPairs(pairs:Map<String,Long>):Map<String,Long> {
        return pairs.map { p -> newPairs(p.key, p.value) }.reduce{  acc, map ->
            val res = acc.toMutableMap()
            map.forEach { (key, value) ->
                res[key] = res.getOrDefault(key, 0) + value
            }
            res }
    }

    var pairMap = startPairs.groupingBy { it }.eachCount().mapValues { e -> e.value.toLong()  }
    var pairs = startPairs
    println(pairMap)
    for (i in 1..40) {
//        polymer = pairs[0][0].toString() + pairs.map { p -> rules[p] + p[1] }.joinToString("")
//        pairs = toPairs(polymer)
//        println(polymer)
        pairMap = newPairs(pairMap)
        println(pairMap.values.sum())
        println(pairMap)
    }

    fun score(pairMap:Map<String, Long>):Long {
        val next = pairMap.flatMap { e -> listOf(Pair(e.key[0], e.value)) }.groupBy({ it.first }, { it.second })
        val charMap = next.map { (key, values) -> key to values.sum() }.toMap()
        println(next)
        println(charMap)
        val lastChar = start[start.length - 1]
        val toMutableMap = charMap.toMutableMap()
        toMutableMap[lastChar] =  charMap.getOrDefault(lastChar, 0) +1L
        return toMutableMap.maxOf { e -> e.value }-toMutableMap.minOf { e->e.value }
    }
    println(score(pairMap))

///(mapA.asSequence() + mapB.asSequence())
//    .distinct()
//    .groupBy({ it.key }, { it.value })
//    .mapValues { (_, values) -> values.joinToString(",") }
    var max = mutableMapOf<String, Int>()
    var min = mutableMapOf<String, Int>()
//    startPairs.forEach { pair ->
//        var pairs = listOf(pair)
//        for (step in 1..40) {
//            polymer = pairs[0][0].toString() + pairs.map { p -> rules[p] + p[1] }.joinToString("")
//            println("  $pair : ${polymer.length}")
//            pairs = toPairs(polymer)
//        }
//        val groupBy = polymer.groupBy { c -> c }.map { e -> Pair(e.key, e.value.size) }//.forEach { elem -> myMap.put(elem.first, myMap.getOrDefault(elem.first,0)+elem.second) }
//        val maxOf = groupBy.maxOf { e -> e.second }
//        val minOf = groupBy.minOf { e -> e.second }
//        max[pair] = maxOf
//        min[pair] = minOf
//    }
//    println(maxOf-minOf)
}

private fun toPairs(start: String) =
    start.mapIndexedNotNull { i, c -> if (i == start.length - 1) null else start.substring(i, i + 2) }

fun day13(lines: List<String>) {
    var mut = emptyList<Point>().toMutableList()
    var instr = emptyList<String>().toMutableList()
    fun foldUp(fold:Int, points:List<Point>):List<Point>{
        return points.map { p -> if(p.second>fold) Point(p.first, 2*fold-p.second) else p }.toSet().toList()
    }

    fun foldLeft(fold:Int, points:List<Point>):List<Point>{
        return points.map { p -> if(p.first>fold) Point(2*fold-p.first, p.second) else p }.toSet().toList()
    }
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
