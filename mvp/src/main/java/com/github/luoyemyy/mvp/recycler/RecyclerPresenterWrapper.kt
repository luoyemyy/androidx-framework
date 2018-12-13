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
     * 在工作线程中加载数据
     * 运行在WorkerThread
     */
    @WorkerThread
    fun loadData(loadType: LoadType, paging: Paging, bundle: Bundle? = null, search: String? = null): List<T>?

    /**
     * 在主线程中加载数据
     * 需要切换线程去加载数据
     */
    @MainThread
    fun loadData(loadType: LoadType, paging: Paging, bundle: Bundle? = null, search: String? = null,@MainThread result: (Boolean, List<T>?) -> Unit): Boolean
}