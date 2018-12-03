package com.github.luoyemyy.mvp.recycler

import android.app.Application
import android.os.Bundle
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner

abstract class AbstractRecyclerPresenter<T>(app: Application) : AndroidViewModel(app), RecyclerPresenterSupport<T>, RecyclerPresenterWrapper<T> {

    private lateinit var mDelegate: RecyclerPresenterDelegate<T>

    fun setup(owner: LifecycleOwner, adapter: RecyclerAdapterSupport<T>) {
        mDelegate = adapter.let {
            it.setup(this)
            RecyclerPresenterDelegate(owner, adapter, this)
        }
    }

    override fun getPaging(): Paging {
        return mDelegate.getPaging()
    }

    override fun getDataSet(): DataSet<T> {
        return mDelegate.getDataSet()
    }

    override fun getAdapterSupport(): RecyclerAdapterSupport<*>? {
        return mDelegate.getAdapterSupport()
    }

    override fun loadInit(bundle: Bundle?) {
        mDelegate.loadInit(bundle)
    }

    override fun loadRefresh() {
        mDelegate.loadRefresh()
    }

    override fun loadMore() {
        mDelegate.loadMore()
    }

    override fun loadSearch(search: String) {
        mDelegate.loadSearch(search)
    }

    @WorkerThread
    override fun loadData(loadType: LoadType, paging: Paging, bundle: Bundle?, search: String?): List<T>? {
        return null
    }

    @MainThread
    override fun loadData(loadType: LoadType, paging: Paging, bundle: Bundle?, search: String?, @MainThread result: (Boolean, List<T>?) -> Unit): Boolean {
        return false
    }
}