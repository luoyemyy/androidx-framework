package com.github.luoyemyy.mvp

import android.app.Application
import androidx.lifecycle.*

abstract class BasePresenter(app: Application) : AndroidViewModel(app) {

    protected val flag: LiveData<Int> by lazy { MutableLiveData<Int>() }

    fun setFlagObserver(owner: LifecycleOwner, observer: Observer<Int>) {
        flag.observe(owner, observer)
    }

    fun eqBit(flagKey: Int, flagValue: Int?): Boolean {
        return flagValue != null && flagKey and flagValue == flagKey
    }

    fun eq(flagKey: Int, flagValue: Int?): Boolean {
        return flagKey == flagValue
    }
}