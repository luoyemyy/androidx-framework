package com.github.luoyemyy.config.app

import android.app.Application
import android.os.StrictMode
import com.github.luoyemyy.file.FileManager
import com.github.luoyemyy.logger.AppError
import com.github.luoyemyy.logger.Logger

object AppInfo {

    lateinit var packageName: String
    lateinit var appInfo: String
    lateinit var profile: Profile

    fun init(app: Application, enableConsoleLog: Boolean = true, enableFileLog: Boolean = true, spfName: String? = null) {

        packageName = app.packageName
        appInfo = spfName ?: "app_info"

        FileManager.initManager(app)
        AppError.init(app)
        Logger.enableConsoleLog = enableConsoleLog
        Logger.enableFileLog = enableFileLog
        Logger.logPath = FileManager.getInstance().inner().dir(FileManager.LOG)?.absolutePath

        StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog()
        StrictMode.VmPolicy.Builder().detectAll().penaltyLog()

    }
}
