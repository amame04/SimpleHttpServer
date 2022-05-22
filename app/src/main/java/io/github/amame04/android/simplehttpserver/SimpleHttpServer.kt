package io.github.amame04.android.simplehttpserver

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors
import io.github.amame04.android.simplehttpserver.SettingsActivity.Companion.startFlag

class SimpleHttpServer {
    companion object {
        const val TAG = "SimpleHttpServerDebug"
        const val CRLF = "\r\n"
        private val ExecuteService = Executors.newFixedThreadPool(100)
        @SuppressLint("StaticFieldLeak")
        private lateinit var logger : AccessLog
    }

    fun start(context: Context) {
        try {
            FileControl(context).createDir("log")
            logger = AccessLog(context, context.filesDir.path + "/log/log")
            logger.start()
        } catch (e: Exception){
            e.printStackTrace()
        }
        
        try {
            if (startFlag) {
                val serverSocket = ServerSocket(8080)
                while (startFlag) {
                    val socket = serverSocket.accept()
                    val server = Runnable {
                        val address = socket.inetAddress.toString()
                        Log.d(TAG, "START >>> $address")
                        logger.info("START >>> $address")

                        serverProcess(socket, context)

                        Log.d(TAG, "$address <<< END")
                        logger.info("$address <<< END")
                    }
                    ExecuteService.submit(server)
                }
                serverSocket.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        logger.stop()
    }

    private fun serverProcess(socket: Socket, context: Context){
        try {
            // 設定ファイルの読み込みとディレクトリアクセス
            val prefFile = context.getSharedPreferences(context.getString(R.string.pref_file_key), Context.MODE_PRIVATE)
            val savedPath = prefFile?.getString(context.getString(R.string.savedDir), "") ?: ""
            val docTree = DocumentFile.fromTreeUri(context, savedPath.toUri())
            val isReturnDefaultPage = savedPath.isBlank() || docTree?.listFiles().isNullOrEmpty()

            val input = socket.getInputStream()
            val output = socket.getOutputStream()

            val request = HttpRequest(input)
            val header = request.httpHeader

            Log.d(TAG, request.httpHeaderText + CRLF + (request.httpBodyText ?: ""))
            logger.info(request.httpHeaderText + CRLF + (request.httpBodyText ?: ""))

            val fileControl = FileControl(context)
            val response : HttpResponse

            when (header.getMethod()) {
                HttpMethod.GET, HttpMethod.POST, HttpMethod.HEAD -> {
                    var file : DocumentFile? = if (isReturnDefaultPage) {
                        fileControl.createDefaultPageFile()
                        DocumentFile.fromFile(File(context.filesDir.path + header.path))
                    } else {
                        header.path?.let {
                            fileControl.findResponseFile(it, savedPath.toUri())
                        }
                    }

                    if (file?.isDirectory == true){
                        if (header.path?.endsWith("/") != true){
                            response = HttpResponse(Status.MOVED_PERMANENTLY)
                            response.addHeader("Location", header.path + "/")
                            response.writeTo(output)
                            Log.d(TAG, response.responseText.toString())
                            return
                        }

                        file = file.findFile("index.html")
                    }

                    if (file?.exists() == true && file.isFile) {
                        response = HttpResponse(Status.OK)
                        response.setBody(file.name.toString())
                        context.contentResolver.openInputStream(file.uri)?.let { response.writeTo(it, output, file.uri.path.toString()) }
                    } else {
                        // Response 404 error
                        response = HttpResponse(Status.NOT_FOUND)
                        fileControl.createDefaultPageFile()
                        response.setBody(File(context.filesDir.path + "/404.html"))
                        response.writeTo(output)
                    }
                }

                null -> {
                    response = HttpResponse(Status.BAD_REQUEST)
                    fileControl.createDefaultPageFile()
                    response.setBody(File(context.filesDir.path + "/400.html"))
                    response.writeTo(output)
                }

                else -> {
                    response = HttpResponse(Status.METHOD_NOT_ALLOWED)
                    fileControl.createDefaultPageFile()
                    response.setBody(File(context.filesDir.path + "/405.html"))
                    response.writeTo(output)
                }
            }
            Log.d(TAG, response.responseText.toString())
            logger.info(response.responseText.toString())

        } catch (e : Exception){
            e.printStackTrace()
        }
    }
}