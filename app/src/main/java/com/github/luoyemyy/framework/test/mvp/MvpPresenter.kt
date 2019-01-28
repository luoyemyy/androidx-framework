package com.github.luoyemyy.framework.test.mvp

import android.app.Application
import com.github.luoyemyy.mvp.AbstractPresenter

class MvpPresenter(var app: Application) : AbstractPresenter<String>(app) {

//    override fun load(bundle: Bundle?) {
//        runOnWorker {
//            //            userDao().saveUser(User(0, "188", "222", "22", null))
//            Bus.post(MvpActivity.EVENT_BUS)
//            Thread.sleep(1000)
//            userDao().saveUser(User(0, "188", "222", "22", null))
//        }
//    }
//
//    val username = Transformations.map(userDao().selectUser()) {
//        it.toJsonString()
//    }
}