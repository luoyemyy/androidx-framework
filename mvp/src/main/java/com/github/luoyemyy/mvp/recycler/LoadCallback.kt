package com.github.luoyemyy.mvp.recycler

import android.os.Bundle
import androidx.annotation.CallSuper

interface LoadCallback<T> {
    fun beforeLoadInit(bundle: Bundle?) {}
    fun beforeLoadRefresh() {}
    fun beforeLoadMore() {}
    fun beforeLoadSearch(search: String) {}
    @CallSuper
    fun afterLoadInit(ok: Boolean, list: List<T>?) {
    }

    @CallSuper
    fun afterLoadRefresh(ok: Boolean, list: List<T>?) {
    }

    @CallSuper
    fun afterLoadMore(ok: Boolean, list: List<T>?) {
    }

    @CallSuper
    fun afterLoadSearch(ok: Boolean, list: List<T>?) {
    }
}