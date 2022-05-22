package io.github.amame04.android.simplehttpserver

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class HttpRequest(input: InputStream) {
    val httpHeader : HttpHeader
    val httpHeaderText: String
    val httpBodyText: String?
    init{
        val bufInput = BufferedReader(InputStreamReader(input))
        httpHeader = HttpHeader(bufInput)
        httpHeaderText = httpHeader.headerText
        httpBodyText = readBody(bufInput)
    }

    private fun readBody(input: BufferedReader): String?{
        return if (httpHeader.isChunkedTransfer()) {
            this.readBodyByChunkedTransfer(input)
        } else {
            this.readBodyByContentLength(input)
        }
    }

    private fun readBodyByChunkedTransfer(input: BufferedReader) : String {
        var body = StringBuilder()
        var chunkSize = Integer.parseInt(input.readLine(), 16)

        while (chunkSize != 0) {
            var buffer = CharArray(chunkSize)
            input.read(buffer)

            body.append(buffer)

            input.readLine() // chunk-body末尾の CRLF読み飛ばし
            chunkSize = Integer.parseInt(input.readLine(), 16)
        }

        return body.toString()
    }

    private fun readBodyByContentLength(input : BufferedReader) : String? {
        val contentLength = httpHeader.getContentLength()

        if (contentLength <= 0) return null

        var c = CharArray(contentLength)
        input.read(c)

        return String(c)
    }
}