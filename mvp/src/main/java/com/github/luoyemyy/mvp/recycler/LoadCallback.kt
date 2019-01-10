package com.github.luoyemyy.mvp.recycler

import android.os.Bundle

interface LoadCallback<T> {
    fun beforeLoadInit(bundle: Bundle?) {}
    fun beforeLoadRefresh() {}
    fun beforeLoadMore() {}
    fun beforeLoadSearch(search: String) {}
    fun afterLoadInit(ok: Boolean, list: List<T>?) {}
    fun afterLoadRefresh(ok: Boolean, list: List<T>?) {}
    fun afterLoadMore(ok: Boolean, list: List<T>?) {}
    fun afterLoadSearch(ok: Boolean, list: List<T>?) {}
}