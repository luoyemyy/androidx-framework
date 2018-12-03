//package com.github.luoyemyy.framework.test.paging
//
//import android.arch.lifecycle.MutableLiveData
//import android.arch.paging.DataSource
//import android.arch.paging.PageKeyedDataSource
//import android.support.annotation.WorkerThread
//
//abstract class AbstractPagingFactory<T> : DataSource.Factory<Int, T>() {
//
//    private val dataSourceLiveData = MutableLiveData<PagingDataSource>()
//
//    override fun create(): DataSource<Int, T> = PagingDataSource().apply {
//        dataSourceLiveData.postValue(this)
//    }
//
//    @WorkerThread
//    abstract fun loadData(page: Int): List<T>
//
//    fun refresh() {
//        dataSourceLiveData.value?.invalidate()
//    }
//
//    inner class PagingDataSource : PageKeyedDataSource<Int, T>() {
//
//        override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, T>) {
//            val data = loadData(1)
//            callback.onResult(data, 0, data.size + 1, 0, 2)
//        }
//
//        override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {
//            callback.onResult(loadData(params.key), params.key + 1)
//        }
//
//        override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {
//
//        }
//    }
//}