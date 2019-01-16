package com.github.luoyemyy.mvp

import androidx.lifecycle.LiveData

open class PresenterLiveData<A : Action> : LiveData<A>() {
    fun postAction(action: A) {
        postValue(action)
    }
}