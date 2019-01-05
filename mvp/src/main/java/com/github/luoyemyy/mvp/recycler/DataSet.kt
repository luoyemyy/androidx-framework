@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.github.luoyemyy.mvp.recycler

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView


class DataSet<T> {

    companion object {
        const val EMPTY = -1
        const val MORE_INIT = -100
        const val MORE_LOADING = -101
        const val MORE_END = -102
        const val MORE_END_GONE = -105
        const val MORE_COMPLETE = -103
        const val MORE_ERROR = -104
        const val CONTENT = 1
    }

    /**
     * 额外的item（加载更多，无数据）
     */
    private data class ExtraItem(val type: Int)

    /**
     * 是否显示空数据item
     */
    internal var enableEmpty = true
    /**
     * 是否显示加载更多item
     */
    internal var enableMore = true
    /**
     * 加载完全部数据后，加载更多item
     */
    internal var moreEndGone = false

    /**
     * 内容列表
     */
    private val mData: MutableList<T> = mutableListOf()

    private val mEmptyItem by lazy { ExtraItem(EMPTY) }
    private val mMoreLoadingItem by lazy { ExtraItem(MORE_LOADING) }
    private val mMoreEndItem by lazy { ExtraItem(MORE_END) }
    private val mMoreErrorItem by lazy { ExtraItem(MORE_ERROR) }

    /**
     * 加载更多状态
     * -100 未开始
     * -101 加载中
     * -102 加载结束，无更多
     * -103 加载结束，还有更多
     * -104 加载结束，加载错误
     */
    private var mLoadMoreState = MORE_INIT
    private val mEnableLoadStates = arrayOf(MORE_INIT, MORE_COMPLETE, MORE_ERROR)

    /**
     * 判断是否可以加载更多
     * @return true 会将状态调整为加载中
     */
    fun canLoadMore(): Boolean {
        return if (enableMore && mLoadMoreState in mEnableLoadStates) {
            loadingMore()
            true
        } else {
            false
        }
    }

    /**
     * 设置加载中状态
     */
    fun loadingMore() {
        mLoadMoreState = MORE_LOADING
    }

    /**
     * 设置加载结束，无更多数据
     */
    fun loadMoreEnd(gone: Boolean) {
        mLoadMoreState = if (gone) MORE_END_GONE else MORE_END
    }

    /**
     * 设置加载结束，还有数据可加载
     */
    fun loadMoreCompleted() {
        mLoadMoreState = MORE_COMPLETE
    }

    /**
     * 设置加载结束，加载失败
     */
    fun loadMoreError() {
        mLoadMoreState = MORE_ERROR
    }

    private fun hideMoreEnd(): Boolean {
        return mLoadMoreState == MORE_END_GONE
    }

    /**
     * 统计所有的item数量
     */
    fun count(): Int {
        var count = mData.size
        if (count == 0) {
            if (enableEmpty) {
                count++
            }
        } else {
            if (enableMore && !hideMoreEnd()) {
                count++
            }
        }
        return count
    }

    /**
     * item 的类型
     */
    fun type(position: Int): Int {
        val item = itemListWithExtra().let {
            if (position in 0 until it.size) {
                it[position]
            } else {
                null
            }
        }
        return when (item) {
            null -> 0
            is ExtraItem -> item.type
            else -> CONTENT
        }
    }

    /**
     * 取出item 如果不是内容类型则为null
     */
    fun item(position: Int): T? {
        return itemList().let {
            if (position in 0 until it.size) {
                it[position]
            } else {
                null
            }
        }
    }

    /**
     * item列表 额外的item为null
     */
    fun itemList(): List<T?> {
        val list = mutableListOf<T?>()
        if (mData.isEmpty()) {
            if (enableEmpty) {
                list.add(null)
            }
        } else {
            mData.forEach {
                list.add(it)
            }
            if (enableMore && !hideMoreEnd()) {
                list.add(null)
            }
        }
        return list
    }

    /**
     * item 列表 包含额外的item
     */
    private fun itemListWithExtra(): List<Any?> {
        val list = mutableListOf<Any?>()
        if (mData.isEmpty()) {
            if (enableEmpty) {
                list.add(mEmptyItem)
            }
        } else {
            mData.forEach {
                list.add(it)
            }
            if (enableMore) {
                when (mLoadMoreState) {
                    MORE_INIT, MORE_LOADING, MORE_COMPLETE -> list.add(mMoreLoadingItem)
                    MORE_END -> list.add(mMoreEndItem)
                    MORE_ERROR -> list.add(mMoreErrorItem)
                    MORE_END_GONE -> {
                        //nothing
                    }
                }
            }
        }
        return list
    }

    /**
     * 内容列表
     */
    fun dataList(): List<T> = mData

    /**
     * 初始化内容列表
     * 如果可以加载更多，则设置当前页已加载完，不判断所有数据是否已经全部加载，
     * 如果不允许加载更多则设置全部加载结束
     */
    fun initData(list: List<T>?, adapter: RecyclerView.Adapter<*>) {
        setData(list, adapter)
    }

    /**
     * 初始化内容列表
     * 如果可以加载更多，则设置当前页已加载完，不判断所有数据是否已经全部加载，
     * 如果不允许加载更多则设置全部加载结束
     */
    fun setData(list: List<T>?, adapter: RecyclerView.Adapter<*>) {
        mData.clear()
        if (list != null && list.isNotEmpty()) {
            mData.addAll(list)
        }
        if (enableMore) {
            loadMoreCompleted()
        } else {
            loadMoreEnd(moreEndGone)
        }
        adapter.notifyDataSetChanged()
    }

    /**
     * 增加内容列表，并刷新数据
     * @param list 如果为null或为空，则判定数据已全部加载
     */
    fun addData(list: List<T>?, adapter: RecyclerView.Adapter<*>) {
        postData {
            if (list != null && list.isNotEmpty()) {
                mData.addAll(list)
                loadMoreCompleted()
            } else {
                loadMoreEnd(moreEndGone)
            }
        }.dispatchUpdatesTo(adapter)
    }

    /**
     * 在指定数据后面添加列表，未指定或找不到指定数据则默认为0
     * @param anchor 相对于该对象
     */
    fun addDataAfter(anchor: T?, list: List<T>?, adapter: RecyclerView.Adapter<*>) {
        postData {
            if (list != null && list.isNotEmpty()) {
                val index = if (anchor == null) {
                    0
                } else {
                    val i = mData.indexOf(anchor)
                    if (i > -1) {
                        i + 1
                    } else {
                        0
                    }
                }
                mData.addAll(index, list)
            }
        }.dispatchUpdatesTo(adapter)
    }

    /**
     * 增加内容列表，出现错误，刷新变化
     */
    fun setMoreError(adapter: RecyclerView.Adapter<*>) {
        postData {
            loadMoreError()
        }.dispatchUpdatesTo(adapter)
    }

    /**
     * @param gone 会覆盖 [moreEndGone] 属性
     */
    fun setMoreEnd(gone: Boolean = false, adapter: RecyclerView.Adapter<*>) {
        postData {
            loadMoreEnd(gone)
        }.dispatchUpdatesTo(adapter)
    }

    /**
     * 删除内容列表，并刷新数据
     */
    fun remove(list: List<T>?, adapter: RecyclerView.Adapter<*>) {
        postData {
            if (list != null && list.isNotEmpty()) {
                mData.removeAll(list)
            }
        }.dispatchUpdatesTo(adapter)
    }

    /**
     * 修改某一项数据，并刷新变化的数据
     */
    fun change(position: Int, adapter: RecyclerView.Adapter<*>, change: (value: T) -> Unit = {}) {
        item(position)?.let {
            change(it)
            adapter.notifyItemChanged(position, "change")
        }
    }

    /**
     * 修改某一项数据，并刷新变化的数据
     */
    fun change(value: T?, adapter: RecyclerView.Adapter<*>) {
        val position = itemList().indexOfFirst { it == value }
        if (position >= 0) {
            adapter.notifyItemChanged(position, "change")
        }
    }

    /**
     * 比较数据集前后变化，返回数据集的变化结果
     */
    fun postData(post: () -> Unit): DiffUtil.DiffResult {
        val oldList = itemListWithExtra()
        post()
        val newList = itemListWithExtra()
        return diff(oldList, newList)
    }

    private fun diff(oldList: List<Any?>, newList: List<Any?>): DiffUtil.DiffResult {
        return DiffUtil.calculateDiff(object : DiffUtil.Callback() {

            override fun getOldListSize(): Int = oldList.size

            override fun getNewListSize(): Int = newList.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] == newList[newItemPosition]
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return true
            }
        })
    }
}