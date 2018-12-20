package com.github.luoyemyy.framework.test

import android.app.Application
import com.github.luoyemyy.config.app.AppInfo
import com.github.luoyemyy.framework.test.db.DB

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppInfo.init(this)
        DB.init(this)
    }
}