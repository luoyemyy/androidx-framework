//package com.github.luoyemyy.framework.test.paging
//
//import android.app.Application
//import android.arch.lifecycle.AndroidViewModel
//import android.arch.lifecycle.MutableLiveData
//import android.arch.paging.LivePagedListBuilder
//import android.arch.paging.PagedList
//import android.os.Bundle
//
//abstract class AbstractPagingPresenter<T>(var app: Application) : AndroidViewModel(app) {
//
//    private val factory = PagingFactory()
//    var bundle: Bundle? = null
//    val liveData by lazy { LivePagedListBuilder<Int, T>(factory, PagedList.Config.Builder().setPageSize(20).setEnablePlaceholders(true).build()).build() }
//    val refreshLiveData = MutableLiveData<Boolean>()
//    val emptyLiveData = MutableLiveData<Boolean>()
//
//    abstract fun loadData(page: Int, bundle: Bundle?): List<T>
//
//    fun refreshData() {
//        factory.refresh()
//    }
//
//    inner class PagingFactory : AbstractPagingFactory<T>() {
//        override fun loadData(page: Int): List<T> {
//            return loadData(page, bundle).also {
//                refreshLiveData.postValue(false)
//                if (page == 1 && it.isEmpty()) {
//                    emptyLiveData.postValue(true)
//                }
//            }
//        }
//    }
//}