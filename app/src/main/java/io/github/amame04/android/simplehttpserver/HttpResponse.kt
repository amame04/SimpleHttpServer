package io.github.amame04.android.simplehttpserver

import android.util.Log
import io.github.amame04.android.simplehttpserver.SimpleHttpServer.Companion.CRLF
import io.github.amame04.android.simplehttpserver.SimpleHttpServer.Companion.TAG
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import kotlin.collections.HashMap

class HttpResponse(private val status: Status) {
    var responseText : String? = null
    private var headers = HashMap<String, String>()
    private var bodyFile : File? = null

    fun addHeader(string: String, value: Any) {
        this.headers[string] = value.toString()
    }

    fun writeTo(output: OutputStream) {
        val strBlr = StringBuilder()

        output.write(("HTTP/1.1 " + this.status + CRLF).toByteArray(UTF_8))
        strBlr.append("HTTP/1.1 " + this.status + CRLF)

        this.headers.forEach { (key, value) ->
            output.write(("$key: $value $CRLF").toByteArray(UTF_8))
            strBlr.append("$key: $value $CRLF")
        }

        bodyFile?.let{
            output.write(("" + CRLF).toByteArray(UTF_8))
            Files.copy(it.toPath(), output)

            strBlr.append("" + CRLF)
            strBlr.append(it.path + CRLF)
        }
        output.close()
        responseText = strBlr.toString()
    }

    fun writeTo(input: InputStream, output: OutputStream, filePath: String) {
        val strBlr = StringBuilder()
        output.write(("HTTP/1.1 " + this.status + CRLF).toByteArray(UTF_8))
        strBlr.append("HTTP/1.1 " + this.status + CRLF)

        this.headers.forEach { (key, value) ->
            output.write(("$key: $value $CRLF").toByteArray(UTF_8))
            strBlr.append("$key: $value $CRLF")
        }

        output.write(("" + CRLF).toByteArray(UTF_8))

        toOutputStream(input, output)

        output.close()
        input.close()

        strBlr.append("" + CRLF)
        strBlr.append(filePath + CRLF)

        responseText = strBlr.toString()
    }

    private fun toOutputStream(input: InputStream, output: OutputStream){
        val buf = ByteArray(1024)

        var len = input.read(buf)
        while(len != -1) {
            output.write(buf, 0, len)
            //Log.i(TAG, buf.decodeToString())
            len = input.read(buf)
        }
    }

    fun setBody(file: File){
        try {
            bodyFile = file
            bodyFile?.let {
                val fileName = it.name
                val extension = fileName.substring(fileName.lastIndexOf('.').plus(1))

                val contentType = ContentType.TEXT_PLAIN.toContentType(extension)
                addHeader("Content-Type", contentType)
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun setBody(filename: String) {
        try {
            val extension = filename.substring(filename.lastIndexOf('.').plus(1))
            val contentType = ContentType.TEXT_PLAIN.toContentType(extension)
            addHeader("Content-Type", contentType)

        } catch (e: Exception){
            e.printStackTrace()
        }
    }
}