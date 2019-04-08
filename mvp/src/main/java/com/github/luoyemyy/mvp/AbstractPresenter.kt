package com.github.luoyemyy.mvp

import android.app.Application
import android.os.Bundle
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.github.luoyemyy.mvp.recycler.LoadType


abstract class AbstractPresenter<T>(app: Application) : BasePresenter(app) {

    protected var dataValue: T? = null
    protected val data: MutableLiveData<T> by lazy { MutableLiveData<T>() }

    protected var listValue: List<T>? = null
    protected val list: MutableLiveData<List<T>> by lazy { MutableLiveData<List<T>>() }

    private var mInitialized = false

    fun setDataObserver(owner: LifecycleOwner, observer: Observer<T>) {
        data.observe(owner, observer)
    }

    fun setListObserver(owner: LifecycleOwner, observer: Observer<List<T>>) {
        list.observe(owner, observer)
    }

    fun isInitialized(): Boolean = mInitialized

    fun setInitialized() {
        mInitialized = true
    }

    /**
     * 初始化延迟加载数据
     */
    open fun delayInitTime() = 400L

    /**
     * 初始化第一页数据，并展示
     * @param bundle        初始化参数
     */
    @MainThread
    open fun loadInit(bundle: Bundle? = null) {
        if (!isInitialized() || reload()) {
            val delay = delayInitTime()
            if (delay > 0) {
                runOnMainDelay(delay) { load(LoadType.init(), bundle) }
            } else {
                load(LoadType.init(), bundle)
            }
        }
    }

    /**
     * 刷新数据，并展示
     */
    @MainThread
    open fun loadRefresh() {
        load(LoadType.refresh())
    }

    private fun load(loadType: LoadType, bundle: Bundle? = null) {
        runOnWorker {
            loadData(loadType, bundle)
        }
    }

    /**
     *
     * 工作线程加载数据（已实现异步）
     */
    @WorkerThread
    open fun loadData(loadType: LoadType, bundle: Bundle? = null) {
    }


    open fun reload(): Boolean = true

}