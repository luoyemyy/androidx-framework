package com.github.luoyemyy.mvp

import android.app.Application
import androidx.annotation.CallSuper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

abstract class BasePresenter(app: Application) : AndroidViewModel(app) {

    protected val flag: MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    private var mInitFlag: Boolean = false

    fun setFlagObserver(owner: LifecycleOwner, observer: Observer<Int>) {
        mInitFlag = true
        flag.observe(owner, observer)
    }

    fun eqBit(flagKey: Int, flagValue: Int?): Boolean {
        return flagValue != null && flagKey and flagValue == flagKey
    }

    fun eq(flagKey: Int, flagValue: Int?): Boolean {
        return flagKey == flagValue
    }

    @CallSuper
    open fun removeObservers(owner: LifecycleOwner) {
        if (mInitFlag) {
            flag.removeObservers(owner)
        }
    }
}

object Flag {
    const val FAILURE = 0
    const val SUCCESS = 1
    const val CANCEL = 3
    const val RESULT = 4
}