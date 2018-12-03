package com.github.luoyemyy.permission

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class PermissionPresenter(app: Application) : AndroidViewModel(app) {

    private var data: MutableLiveData<Array<String>>? = null

    fun addObserver(owner: LifecycleOwner, observer: Observer<Array<String>>) {
        data = MutableLiveData<Array<String>>().apply {
            observe(owner, observer)
        }
    }

    fun removeObserver(observer: Observer<Array<String>>) {
        data?.removeObserver(observer)
        data = null
    }

    fun postValue(array: Array<String>) {
        data?.postValue(array)
    }
}