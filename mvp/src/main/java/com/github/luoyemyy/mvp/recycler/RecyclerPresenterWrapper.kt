package com.github.luoyemyy.mvp.recycler

import android.os.Bundle
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread

/**
 * 扩展
 */
interface RecyclerPresenterWrapper<T> : LoadCallback<T> {

    fun getAdapterSupport(): RecyclerAdapterSupport<*>?

    @WorkerThread
    fun loadData(loadType: LoadType, paging: Paging, bundle: Bundle? = null, search: String? = null): List<T>?

    @MainThread
    fun loadData(loadType: LoadType, paging: Paging, bundle: Bundle? = null, search: String? = null,@MainThread result: (Boolean, List<T>?) -> Unit): Boolean
}