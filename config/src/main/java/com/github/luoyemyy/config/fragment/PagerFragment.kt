package com.github.luoyemyy.config.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment

/**
 * 第一次显示的时候才开始加载数据，用于ViewPager中
 */
abstract class PagerFragment : Fragment(), ILoadData {

    private var loaded = false

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