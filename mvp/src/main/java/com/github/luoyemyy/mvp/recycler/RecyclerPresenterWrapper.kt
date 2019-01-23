package com.github.luoyemyy.mvp.recycler

import android.os.Bundle
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread

/**
 * 扩展
 */
interface RecyclerPresenterWrapper<T> : LoadCallback<T> {

    /**
     * 获得Adapter扩展
     */
    fun getAdapterSupport(): RecyclerAdapterSupport<*>?

    /**
     * 初始化延迟加载数据
     */
    fun delayInitTime() = 400L

    /**
     *
     * 工作线程加载数据（已实现异步）
     */
    @WorkerThread
    fun loadData(loadType: LoadType, paging: Paging, bundle: Bundle? = null, search: String? = null): List<T>?

    /**
     * 主线程中加载数据（未实现异步）
     */
    @MainThread
    fun loadData(loadType: LoadType, paging: Paging, bundle: Bundle? = null, search: String? = null, afterLoad: (Boolean, List<T>?) -> Unit): Boolean

}