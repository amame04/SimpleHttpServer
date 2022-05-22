package io.github.amame04.android.simplehttpserver

import io.github.amame04.android.simplehttpserver.SimpleHttpServer.Companion.CRLF
import java.io.BufferedReader
import java.net.URLDecoder
import kotlin.text.StringBuilder

class HttpHeader(input: BufferedReader) {
    val headerText : String
    var path : String? = null
    private val messageHeaders = HashMap<String, String>()
    private val header = StringBuilder()
    private var method : HttpMethod? = null

    init {
        header.append(this.readRequestLine(input))
        header.append(this.readMessageLine(input))

        this.headerText = header.toString()
    }

    private fun readRequestLine(input: BufferedReader) : String {
        val requestLine = input.readLine()
        val tmp = requestLine.split(" ")
        method = HttpMethod.valueOf(tmp[0].uppercase())
        path = URLDecoder.decode(tmp[1], "UTF-8")

        return requestLine + CRLF
    }

    private fun readMessageLine(input: BufferedReader) : StringBuilder {
        val message = StringBuilder()
        var messageLine = input.readLine()

        while (messageLine != null && messageLine.isNotEmpty()) {
            this.putMessageLine(messageLine)

            message.append(messageLine + CRLF)
            messageLine = input.readLine()
        }

        return message
    }

    private fun putMessageLine(messageLine: String) {
        val tmp = messageLine.split(":")
        this.messageHeaders[tmp[0].trim()] = tmp[1].trim()
    }

    fun getContentLength() : Int {
        return Integer.parseInt(this.messageHeaders["Content-Length"] ?: "0")
    }

    fun isChunkedTransfer() : Boolean {
        return this.messageHeaders["Transfer-Encoding"] ?: "-" == "chunked"
    }

    fun getMethod() : HttpMethod? {
        return method
    }
}