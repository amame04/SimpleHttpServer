package io.github.amame04.android.simplehttpserver

import android.content.Context
import java.lang.Exception
import java.util.logging.FileHandler
import java.util.logging.Level
import java.util.logging.Logger
import java.util.logging.SimpleFormatter

class AccessLog(private val context: Context, private val path: String) {
    private val logger = Logger.getLogger(context.getString(R.string.app_name) + ":AccessLog")
    private val fileHandler = FileHandler(path, true)

    fun start() {
        try {
            fileHandler.formatter = SimpleFormatter()
            logger.addHandler(fileHandler)
            logger.level = Level.ALL

        } catch (e: Exception) {
            e.printStackTrace()
            logger.log(Level.SEVERE, e.toString())
        }
    }

    fun info(msg: String) {
        try {
            logger.info(msg)
        } catch (e: Exception) {
            logger.log(Level.SEVERE, e.toString())
        }
    }

    fun stop() {
        fileHandler.close()
    }
}