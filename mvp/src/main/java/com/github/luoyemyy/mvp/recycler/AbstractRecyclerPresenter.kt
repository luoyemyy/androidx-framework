package com.github.luoyemyy.mvp.recycler

import android.app.Application
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner

abstract class AbstractRecyclerPresenter<T>(app: Application) : AndroidViewModel(app), RecyclerPresenterSupport<T>, RecyclerPresenterWrapper<T> {

    private val mDelegate: RecyclerPresenterDelegate<T> = RecyclerPresenterDelegate()

    fun setup(owner: LifecycleOwner, adapter: RecyclerAdapterSupport<T>) {
        adapter.setup(this)
        mDelegate.setPresenter(this)
        mDelegate.setAdapter(owner, adapter)
    }

    override fun getDataSet(): DataSet<T> {
        return mDelegate.getDataSet()
    }

    override fun getAdapterSupport(): RecyclerAdapterSupport<*>? {
        return mDelegate.getAdapterSupport()
    }

    override fun onScroll(position: Int, offset: Int) {
        return mDelegate.onScroll(position, offset)
    }

    override fun isInitialized(): Boolean {
        return mDelegate.isInitialized()
    }

    override fun setInitialized() {
        mDelegate.setInitialized()
    }

    override fun loadInit(bundle: Bundle?) {
        mDelegate.loadInit(bundle)
    }

    @CallSuper
    override fun afterLoadInit(ok: Boolean, list: List<T>?) {
        getAdapterSupport()?.apply {
            getDataSet().setData(list, getAdapter())
        }
    }

    override fun loadRefresh() {
        mDelegate.loadRefresh()
    }

    @CallSuper
    override fun afterLoadRefresh(ok: Boolean, list: List<T>?) {
        getAdapterSupport()?.apply {
            getDataSet().setData(list, getAdapter())
        }
    }

    override fun loadMore() {
        mDelegate.loadMore()
    }

    @CallSuper
    override fun afterLoadMore(ok: Boolean, list: List<T>?) {
        getAdapterSupport()?.apply {
            if (ok) {
                getDataSet().addData(list, getAdapter())
            } else {
                getDataSet().let {
                    it.getPaging().errorBack()
                    it.addDataError(getAdapter())
                }
            }
        }
    }

    override fun loadSearch(search: String) {
        mDelegate.loadSearch(search)
    }

    @CallSuper
    override fun afterLoadSearch(ok: Boolean, list: List<T>?) {
        getAdapterSupport()?.apply {
            getDataSet().setData(list, getAdapter())
        }
    }

    @WorkerThread
    override fun loadData(loadType: LoadType, paging: Paging, bundle: Bundle?, search: String?): List<T>? {
        return null
    }

    @MainThread
    override fun loadData(loadType: LoadType, paging: Paging, bundle: Bundle?, search: String?, afterLoad: (Boolean, List<T>?) -> Unit): Boolean {
        return false
    }
}