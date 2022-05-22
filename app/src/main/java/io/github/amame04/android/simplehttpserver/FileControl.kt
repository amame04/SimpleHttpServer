package io.github.amame04.android.simplehttpserver

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import io.github.amame04.android.simplehttpserver.SimpleHttpServer.Companion.TAG
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.lang.Exception
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.isDirectory

class FileControl(private val context: Context) {
    fun createDir(name: String) {
        val path = Paths.get(context.filesDir.path + "/$name")

        if(path.isDirectory() || path.toFile().exists()) return

        path.toFile().mkdir()
    }

    fun createDefaultPageFile() {
        val assets = context.assets

        assets.list("src")?.forEach { filename ->
            try {
                val outputPath = Paths.get(context.filesDir.path + "/" + filename)
                if(!outputPath.toFile().exists()) {
                    val input = assets.open("src/$filename")
                    Files.copy(input, outputPath)
                }
            } catch (e : Exception) {
                e.printStackTrace()
            }
        }
    }

    fun findResponseFile(path: String, root: Uri) : DocumentFile? {
        var doc = DocumentFile.fromTreeUri(context, root)
        path.split("/").forEach {
            if (it.isNotEmpty()) {
                doc = doc?.findFile(it)
            }
        }
        return doc
    }


}