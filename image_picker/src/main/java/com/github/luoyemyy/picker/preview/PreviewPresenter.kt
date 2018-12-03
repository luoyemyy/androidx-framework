package com.github.luoyemyy.picker.preview

import android.app.Application
import com.github.luoyemyy.mvp.AbstractPresenter
import com.github.luoyemyy.picker.entity.Image

class PreviewPresenter(app: Application) : AbstractPresenter<Image>(app) {

    var fullScreen: Boolean = false

}