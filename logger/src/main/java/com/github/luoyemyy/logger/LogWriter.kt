package com.github.luoyemyy.logger

import android.util.Log
import com.github.luoyemyy.handler.ThreadHandler
import java.io.FileWriter
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*

internal object LogWriter {

    fun write(throwable: Throwable?, threadName: String, level: String, tag: String, msg: String, path: String) {
        ThreadHandler.post {
            try {
                val logDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val logFileName = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                FileWriter("$path$logFileName.log.txt", true).use { sw ->
                    PrintWriter(sw, true).use { writer ->
                        writer.println()
                        writer.println("$logDateTime [$threadName]-$level/$tag:$msg")
                        var e = throwable
                        while (e != null) {
                            e.printStackTrace(writer)
                            e = e.cause
                        }
                    }
                }
            } catch (e: Throwable) {
                Log.e("LogWriter", "write:  ", e)
            }
        }
    }
}


