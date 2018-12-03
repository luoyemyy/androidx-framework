package com.github.luoyemyy.mvp.recycler

import android.os.Bundle
import androidx.annotation.MainThread

/**
 * 扩展
 */
interface RecyclerPresenterSupport<T>{

    fun getDataSet(): DataSet<T>
    fun getPaging(): Paging

    @MainThread
    fun loadInit(bundle: Bundle? = null)

    @MainThread
    fun loadRefresh()

    @MainThread
    fun loadMore()

    @MainThread
    fun loadSearch(search: String)

}