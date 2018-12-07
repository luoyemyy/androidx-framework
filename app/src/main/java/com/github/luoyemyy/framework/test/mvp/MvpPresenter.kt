package com.github.luoyemyy.framework.test.mvp

import android.app.Application
import android.os.Bundle
import com.github.luoyemyy.bus.Bus
import com.github.luoyemyy.config.ext.runOnWorker
import com.github.luoyemyy.mvp.AbstractPresenter

class MvpPresenter(var app: Application) : AbstractPresenter<String>(app) {

    override fun load(bundle: Bundle?) {
        runOnWorker {
            Bus.post(MvpActivity.EVENT_BUS)
        }
    }
}