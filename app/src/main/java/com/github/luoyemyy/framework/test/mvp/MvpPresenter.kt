package com.github.luoyemyy.framework.test.mvp

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.Transformations
import com.github.luoyemyy.bus.Bus
import com.github.luoyemyy.config.runOnWorker
import com.github.luoyemyy.ext.toJsonString
import com.github.luoyemyy.framework.test.db.User
import com.github.luoyemyy.framework.test.db.userDao
import com.github.luoyemyy.mvp.AbstractPresenter

class MvpPresenter(var app: Application) : AbstractPresenter<String>(app) {

    override fun load(bundle: Bundle?) {
        runOnWorker {
            //            userDao().saveUser(User(0, "188", "222", "22", null))
            Bus.post(MvpActivity.EVENT_BUS)
            Thread.sleep(1000)
            userDao().saveUser(User(0, "188", "222", "22", null))
        }
    }

    val username = Transformations.map(userDao().selectUser()) {
        it.toJsonString()
    }
}