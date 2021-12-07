package utils

import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class PuzzleInputWriter(private val sessionId: String) {
    fun writeDayPuzzleToFile(year: String, day: Int): File {
        val file = File("src/main/resources/aoc${year.substring(2)}/day$day.txt")
        if (!file.exists()) {
            file.writeText(requestPuzzle(year, day).toString())
        }
        return file
    }

    fun requestPuzzle(year: String, day: Int): String? {
        println("Requesting puzzle $day $year")
        val client = HttpClient.newBuilder().build();
        val request = HttpRequest.newBuilder()
                .uri(URI.create("https://adventofcode.com/$year/day/$day/input"))
                .setHeader("cookie", "session=$sessionId")
                .build();
        val response = client.send(request, HttpResponse.BodyHandlers.ofString());
        val body = response.body()
        if (body.endsWith("\r\n\r\n")) {
            //remove last line
            return body.substring(0, body.length - 2)
        }
        return body
    }
}