package com.github.luoyemyy.mvp.recycler

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.*
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class RecyclerPresenterDelegate<T> : LifecycleObserver {

    private var mPresenterWrapper: RecyclerPresenterWrapper<T>? = null
    private val mDataSet by lazy { DataSet<T>() }
    private var mPaging: Paging = Paging.Page()
    private var mAdapterSupport: RecyclerAdapterSupport<T>? = null
    private val mLiveDataRefreshState = MutableLiveData<Boolean>()
    private var mDisposable: Disposable? = null
    private var mScrollPosition: Int = -1
    private var mScrollOffset: Int = -1

    internal fun setPresenterWrapper(presenterWrapper: RecyclerPresenterWrapper<T>) {
        mPresenterWrapper = presenterWrapper
    }

    internal fun setAdapterSupport(owner: LifecycleOwner, adapter: RecyclerAdapterSupport<T>) {

        mAdapterSupport = adapter.apply {
            mDataSet.enableEmpty = enableEmpty()
            mDataSet.enableMore = enableLoadMore()
            mDataSet.moreEndGone = loadMoreEndGone()
        }

        owner.lifecycle.addObserver(this)
        mLiveDataRefreshState.observe(owner, Observer<Boolean> {
            mAdapterSupport?.setRefreshState(it ?: false)
        })
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(source: LifecycleOwner?) {
        mDisposable?.apply {
            if (!isDisposed) {
                dispose()
            }
        }
        mDisposable = null
        mAdapterSupport = null
        source?.lifecycle?.removeObserver(this)

        Log.i(this::class.java.simpleName, "onDestroy:")
    }

    fun getDataSet(): DataSet<T> {
        return mDataSet
    }

    fun getPaging(): Paging {
        return mPaging
    }

    fun setPaging(paging: Paging) {
        mPaging = paging
    }

    fun getAdapterSupport(): RecyclerAdapterSupport<*>? {
        return mAdapterSupport
    }

    fun onScroll(position: Int, offset: Int) {
        mScrollPosition = position
        mScrollOffset = offset
    }

    fun reload() {
        mAdapterSupport?.attachToRecyclerView(mScrollPosition, mScrollOffset)
    }

    private fun beforeLoadInit(bundle: Bundle?) {
        mPresenterWrapper?.beforeLoadInit(bundle)
        mAdapterSupport?.apply {
            beforeLoadInit(bundle)
            mPaging.reset()
            mLiveDataRefreshState.value = true
        }
    }

    fun loadInit(bundle: Bundle?) {
        beforeLoadInit(bundle)
        loadData(LoadType.init(), bundle)
    }

    private fun afterLoadInit(list: List<T>?) {
        mPresenterWrapper?.afterLoadInit(list)
        mAdapterSupport?.apply {
            mDataSet.initData(list, getAdapter())
            attachToRecyclerView(mScrollPosition, mScrollOffset)
            afterLoadInit(list)
            mLiveDataRefreshState.value = false
        }
    }

    private fun beforeLoadRefresh() {
        mPresenterWrapper?.beforeLoadRefresh()
        mAdapterSupport?.apply {
            beforeLoadRefresh()
            mPaging.reset()
        }
    }

    fun loadRefresh() {
        beforeLoadRefresh()
        loadData(LoadType.refresh())
    }

    private fun afterLoadRefresh(list: List<T>?) {
        mPresenterWrapper?.afterLoadRefresh(list)
        mAdapterSupport?.apply {
            mDataSet.setData(list, getAdapter())
            afterLoadRefresh(list)
            mLiveDataRefreshState.value = false
        }
    }

    private fun beforeLoadMore() {
        mPresenterWrapper?.beforeLoadMore()
        mAdapterSupport?.apply {
            beforeLoadMore()
            mPaging.next()
        }
    }

    fun loadMore() {
        if (!mDataSet.canLoadMore()) {
            return
        }
        beforeLoadMore()
        loadData(LoadType.more())

    }

    private fun afterLoadMore(list: List<T>?) {
        mPresenterWrapper?.afterLoadMore(list)
        mAdapterSupport?.apply {
            mDataSet.addData(list, getAdapter())
            afterLoadMore(list)
        }
    }

    private fun beforeLoadSearch(search: String) {
        mPresenterWrapper?.beforeLoadSearch(search)
        mAdapterSupport?.apply {
            beforeLoadSearch(search)
            mPaging.reset()
            mLiveDataRefreshState.value = true
        }
    }

    fun loadSearch(search: String) {
        beforeLoadSearch(search)
        loadData(LoadType.search(), null, search)
    }

    private fun afterLoadSearch(list: List<T>?) {
        mPresenterWrapper?.afterLoadSearch(list)
        mAdapterSupport?.apply {
            mDataSet.setData(list, getAdapter())
            afterLoadSearch(list)
            mLiveDataRefreshState.value = false
        }
    }

    private fun loadData(loadType: LoadType, bundle: Bundle? = null, search: String? = null) {
        val wrapper = mPresenterWrapper ?: return
        val r = wrapper.loadData(loadType, mPaging, bundle, search) { ok, value ->
            if (ok) {
                loadAfter(loadType, value)
            } else {
                loadAfterError(loadType)
            }
        }
        if (!r) {
            mDisposable?.apply {
                if (!isDisposed) dispose()
            }
            mDisposable = Single
                    .create<List<T>> {
                        it.onSuccess(wrapper.loadData(loadType, mPaging, bundle, search)
                                ?: listOf())
                    }
                    .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ loadAfter(loadType, it) }, { loadAfterError(loadType) })
        }
    }

    private fun loadAfter(loadType: LoadType, list: List<T>?) {
        when {
            loadType.isInit() -> afterLoadInit(list)
            loadType.isRefresh() -> afterLoadRefresh(list)
            loadType.isMore() -> afterLoadMore(list)
            loadType.isSearch() -> afterLoadSearch(list)
        }
    }

    private fun loadAfterError(loadType: LoadType) {
        when {
            loadType.isInit() -> afterLoadInit(null)
            loadType.isRefresh() -> afterLoadRefresh(null)
            loadType.isMore() -> {
                mPaging.nextError()
                mAdapterSupport?.apply {
                    mDataSet.setMoreError(getAdapter())
                }
            }
            loadType.isSearch() -> afterLoadSearch(null)
        }
    }
}