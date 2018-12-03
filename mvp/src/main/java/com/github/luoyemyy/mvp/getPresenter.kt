package com.github.luoyemyy.mvp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import com.github.luoyemyy.mvp.recycler.AbstractRecyclerPresenter
import com.github.luoyemyy.mvp.recycler.RecyclerAdapterSupport

/**
 * presenter
 */
inline fun <reified T : AndroidViewModel> Fragment.getPresenter(): T = ViewModelProviders.of(this).get(T::class.java)

inline fun <reified T : AndroidViewModel> FragmentActivity.getPresenter(): T = ViewModelProviders.of(this).get(T::class.java)

inline fun <R, reified T : AbstractRecyclerPresenter<R>> Fragment.getRecyclerPresenter(owner: LifecycleOwner, adapter: RecyclerAdapterSupport<R>): T {
    return ViewModelProviders.of(this).get(T::class.java).apply {
        setup(owner, adapter)
    }
}

inline fun <R, reified T : AbstractRecyclerPresenter<R>> FragmentActivity.getRecyclerPresenter(owner: LifecycleOwner, adapter: RecyclerAdapterSupport<R>): T {
    return ViewModelProviders.of(this).get(T::class.java).apply {
        setup(owner, adapter)
    }
}
