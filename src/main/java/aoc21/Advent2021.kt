package aoc21

import utils.PuzzleInputWriter
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


fun main(args:Array<String>) {
    val sessionId = args[0]
    PuzzleInputWriter(sessionId).writeDayPuzzleToFile("2021", 8).readLines().forEach(::println)
}

fun day8() {

}