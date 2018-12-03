//package com.github.luoyemyy.framework.test.paging
//
//import android.app.Application
//import android.os.Bundle
//
//class PagingPresenter(app: Application) : AbstractPagingPresenter<Paging>(app) {
//    override fun loadData(page: Int, bundle: Bundle?): List<Paging> {
//        Thread.sleep(3000L)
//        return (0..19).map { Paging((page - 1) * 20 + it) }
//    }
//}