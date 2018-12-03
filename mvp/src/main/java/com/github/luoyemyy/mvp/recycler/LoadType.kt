package com.github.luoyemyy.mvp.recycler

class LoadType private constructor(private val loadType: Int) {

    companion object {
        fun init(): LoadType = LoadType(1)
        fun refresh(): LoadType = LoadType(2)
        fun more(): LoadType = LoadType(3)
        fun search(): LoadType = LoadType(4)
    }

    fun isInit() = loadType == 1
    fun isRefresh() = loadType == 2
    fun isMore() = loadType == 3
    fun isSearch() = loadType == 4
}