package com.github.luoyemyy.mvp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment

/**
 * 第一次显示的时候才开始加载数据，用于ViewPager中
 */
abstract class PagerFragment : Fragment() {

    private var loaded = false

    abstract fun loadData(bundle: Bundle? = null)

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if (isVisibleToUser && !loaded) {
            loaded = true
            loadData(arguments)
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        loaded = savedInstanceState?.getBoolean("loaded") ?: false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("loaded", loaded)
    }

}