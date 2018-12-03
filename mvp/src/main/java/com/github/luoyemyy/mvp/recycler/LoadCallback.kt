package com.github.luoyemyy.mvp.recycler

import android.os.Bundle

interface LoadCallback<T> {
    fun beforeLoadInit(bundle: Bundle?) {}
    fun beforeLoadRefresh() {}
    fun beforeLoadMore() {}
    fun beforeLoadSearch(search: String) {}
    fun afterLoadInit(list: List<T>?) {}
    fun afterLoadRefresh(list: List<T>?) {}
    fun afterLoadMore(list: List<T>?) {}
    fun afterLoadSearch(list: List<T>?) {}
}