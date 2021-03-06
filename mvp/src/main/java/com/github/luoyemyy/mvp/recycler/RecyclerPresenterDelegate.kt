package com.github.luoyemyy.mvp.recycler

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.lifecycle.*
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RecyclerPresenterDelegate<T> : LifecycleObserver {

    private var mPresenter: RecyclerPresenterWrapper<T>? = null
    private var mAdapter: RecyclerAdapterSupport<T>? = null
    private val mDataSet by lazy { DataSet<T>() }
    private val mLiveDataRefreshState = MutableLiveData<Boolean>()
    private var mScrollPosition: Int = -1
    private var mScrollOffset: Int = -1
    private var mInitialized = false

    internal fun setPresenter(presenterWrapper: RecyclerPresenterWrapper<T>) {
        mPresenter = presenterWrapper
    }

    internal fun setAdapter(owner: LifecycleOwner, adapter: RecyclerAdapterSupport<T>) {
        if (mAdapter == null) {
            adapter.apply {
                mDataSet.enableEmpty = enableEmpty()
                mDataSet.enableMore = enableLoadMore()
                mDataSet.moreEndGone = loadMoreEndGone()
            }
        } else {
            mLiveDataRefreshState.removeObservers(owner)
        }
        mAdapter = adapter

        owner.lifecycle.addObserver(this)
        mLiveDataRefreshState.observe(owner, Observer<Boolean> {
            mAdapter?.setRefreshState(it ?: false)
        })
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(source: LifecycleOwner?) {
        mAdapter = null
        source?.lifecycle?.removeObserver(this)
    }

    fun getDataSet(): DataSet<T> {
        return mDataSet
    }

    fun getAdapterSupport(): RecyclerAdapterSupport<*>? {
        return mAdapter
    }

    fun onScroll(position: Int, offset: Int) {
        mScrollPosition = position
        mScrollOffset = offset
    }

    fun isInitialized(): Boolean = mInitialized

    fun setInitialized() {
        mInitialized = true
    }

    private fun startRefresh() {
        mLiveDataRefreshState.value = true
    }

    private fun endRefresh() {
        mLiveDataRefreshState.value = false
    }

    /**
     ******************************************************************************************************************
     ** init **********************************************************************************************************
     ******************************************************************************************************************
     */
    private fun beforeLoadInit(bundle: Bundle?): Boolean {
        return mPresenter?.let { presenter ->
            mAdapter?.let { adapter ->
                presenter.beforeLoadInit(bundle)
                adapter.beforeLoadInit(bundle)
                getDataSet().getPaging().reset()
                startRefresh()
                true
            } ?: false
        } ?: false
    }

    fun loadInit(bundle: Bundle?) {
        if (isInitialized()) {
            mAdapter?.attachToRecyclerView(mScrollPosition, mScrollOffset)
        } else if (beforeLoadInit(bundle)) {
            val delay = mPresenter?.delayInitTime() ?: 0
            if (delay > 0) {
                Handler().postDelayed({
                    loadData(LoadType.init(), bundle)
                }, delay)
            } else {
                loadData(LoadType.init(), bundle)
            }
        }
    }

    private fun afterLoadInit(ok: Boolean, list: List<T>? = null) {
        mPresenter?.let { presenter ->
            mAdapter?.let { adapter ->
                presenter.afterLoadInit(ok, list)
                adapter.afterLoadInit(ok, list)
                adapter.attachToRecyclerView(-1, 0)
                endRefresh()
                setInitialized()
            }
        }
    }

    /**
     ******************************************************************************************************************
     ** refresh *******************************************************************************************************
     ******************************************************************************************************************
     */
    private fun beforeLoadRefresh(): Boolean {
        return mPresenter?.let { presenter ->
            mAdapter?.let { adapter ->
                presenter.beforeLoadRefresh()
                adapter.beforeLoadRefresh()
                getDataSet().getPaging().reset()
                startRefresh()
                true
            } ?: false
        } ?: false
    }

    fun loadRefresh() {
        if (beforeLoadRefresh()) {
            loadData(LoadType.refresh())
        }
    }

    private fun afterLoadRefresh(ok: Boolean, list: List<T>? = null) {
        mPresenter?.let { presenter ->
            mAdapter?.let { adapter ->
                presenter.afterLoadRefresh(ok, list)
                adapter.afterLoadRefresh(ok, list)
                endRefresh()
            }
        }
    }

    /**
     ******************************************************************************************************************
     ** more **********************************************************************************************************
     ******************************************************************************************************************
     */
    private fun beforeLoadMore(): Boolean {
        return mPresenter?.let { presenter ->
            mAdapter?.let { adapter ->
                presenter.beforeLoadMore()
                adapter.beforeLoadMore()
                getDataSet().getPaging().next()
                true
            } ?: false
        } ?: false
    }

    fun loadMore() {
        if (!mDataSet.canLoadMore()) {
            return
        } else if (beforeLoadMore()) {
            loadData(LoadType.more())
        }
    }

    private fun afterLoadMore(ok: Boolean, list: List<T>?) {
        mPresenter?.let { presenter ->
            mAdapter?.let { adapter ->
                presenter.afterLoadMore(ok, list)
                adapter.afterLoadMore(ok, list)
            }
        }
    }

    /**
     ******************************************************************************************************************
     ** search ********************************************************************************************************
     ******************************************************************************************************************
     */
    private fun beforeLoadSearch(search: String): Boolean {
        return mPresenter?.let { presenter ->
            mAdapter?.let { adapter ->
                presenter.beforeLoadSearch(search)
                adapter.beforeLoadSearch(search)
                getDataSet().getPaging().reset()
                startRefresh()
                true
            } ?: false
        } ?: false
    }

    fun loadSearch(search: String) {
        if (beforeLoadSearch(search)) {
            loadData(LoadType.search(), search = search)
        }
    }

    private fun afterLoadSearch(ok: Boolean, list: List<T>? = null) {
        mPresenter?.let { presenter ->
            mAdapter?.let { adapter ->
                presenter.afterLoadSearch(ok, list)
                adapter.afterLoadSearch(ok, list)
                endRefresh()
            }
        }
    }

    private fun loadData(loadType: LoadType, bundle: Bundle? = null, search: String? = null) {
        mPresenter?.let { presenter ->
            //sync
            val async = presenter.loadData(loadType, mDataSet.getPaging(), bundle, search) { ok, value ->
                afterLoad(loadType, ok, value)
            }
            if (async) {
                return
            }

            // async
            Single
                    .create<List<T>> {
                        val list = try {
                            presenter.loadData(loadType, mDataSet.getPaging(), bundle, search)
                        } catch (e: Throwable) {
                            Log.e("PresenterDelegate", "loadData:  ", e)
                            null
                        }
                        it.onSuccess(list ?: listOf())
                    }
                    .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe { value, error ->
                        afterLoad(loadType, error == null, value)
                    }
        }
    }

    private fun afterLoad(loadType: LoadType, ok: Boolean, value: List<T>?) {
        when {
            loadType.isInit() -> afterLoadInit(ok, value)
            loadType.isRefresh() -> afterLoadRefresh(ok, value)
            loadType.isMore() -> afterLoadMore(ok, value)
            loadType.isSearch() -> afterLoadSearch(ok, value)
        }
    }
}