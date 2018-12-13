package com.github.luoyemyy.mvp.recycler

import android.os.Bundle
import androidx.annotation.MainThread

/**
 * 扩展
 */
interface RecyclerPresenterSupport<T> {

    /**
     * 获得数据集
     */
    fun getDataSet(): DataSet<T>

    /**
     * 获得分页信息
     */
    fun getPaging(): Paging

    /**
     * 设置当前滚动的位置
     * @param position
     * @param offset
     */
    fun onScroll(position: Int, offset: Int)

    /**
     * 重新展示列表，用于recreate
     */
    fun reload()

    /**
     * 初始化第一页数据，并展示
     */
    @MainThread
    fun loadInit(bundle: Bundle? = null)

    /**
     * 刷新数据，并展示
     */
    @MainThread
    fun loadRefresh()

    /**
     * 加载更多数据，并展示
     */
    @MainThread
    fun loadMore()

    /**
     * 查询数据，并展示
     */
    @MainThread
    fun loadSearch(search: String)

}