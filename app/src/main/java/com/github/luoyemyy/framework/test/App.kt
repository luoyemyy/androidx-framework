package com.github.luoyemyy.framework.test

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.github.luoyemyy.config.AppInfo
import com.github.luoyemyy.config.Language
import com.github.luoyemyy.framework.test.db.DB

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppInfo.init(this)
        DB.init(this)
        Language.listenerLanguageChange(this)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(Language.attachBaseContext(base))
    }
}