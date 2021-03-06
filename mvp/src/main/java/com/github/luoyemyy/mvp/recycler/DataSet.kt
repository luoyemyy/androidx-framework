@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.github.luoyemyy.mvp.recycler

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class DataSet<T> {

    companion object {
        const val EMPTY = -1
        const val MORE_INIT = -100
        const val MORE_LOADING = -101
        const val MORE_END = -102
        const val MORE_COMPLETE = -103
        const val MORE_ERROR = -104
        const val MORE_END_GONE = -105
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
     * 分页
     */
    private val mPaging by lazy { Paging.Page() }

    fun getPaging(): Paging = mPaging

    /**
     * 加载更多状态
     * -100 未开始
     * -101 加载中
     * -102 加载结束，无更多
     * -105 加载结束，无更多，隐藏提示
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
    private fun loadingMore() {
        mLoadMoreState = MORE_LOADING
    }

    /**
     * 设置加载结束，无更多数据
     */
    private fun loadMoreEnd() {
        mLoadMoreState = if (moreEndGone) MORE_END_GONE else MORE_END
    }

    /**
     * 设置加载结束，还有数据可加载
     */
    private fun loadMoreCompleted() {
        mLoadMoreState = MORE_COMPLETE
    }

    /**
     * 设置加载结束，加载失败
     */
    private fun loadMoreError() {
        mLoadMoreState = MORE_ERROR
    }

    /**
     * 隐藏加载更多
     */
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
                    MORE_END_GONE -> { //nothing
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
     * 标记当前页已加载完，可以开始加载下一页
     */
    fun setData(list: List<T>?, adapter: RecyclerView.Adapter<*>) {
        mData.clear()
        if (list != null && list.isNotEmpty()) {
            mData.addAll(list)
        }
        loadMoreCompleted()
        adapter.notifyDataSetChanged()
    }

    /**
     * 初始化内容列表，标记已加载完全部数据
     */
    fun setDataEnd(list: List<T>?, adapter: RecyclerView.Adapter<*>) {
        mData.clear()
        if (list != null && list.isNotEmpty()) {
            mData.addAll(list)
        }
        loadMoreEnd()
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
                loadMoreEnd()
            }
        }.dispatchUpdatesTo(adapter)
    }

    /**
     * 增加内容列表，并刷新数据，标记为已加载全部数据
     * @param list
     */
    fun addDataEnd(list: List<T>?, adapter: RecyclerView.Adapter<*>) {
        postData {
            if (list != null && list.isNotEmpty()) {
                mData.addAll(list)
            }
            loadMoreEnd()
        }.dispatchUpdatesTo(adapter)
    }

    /**
     * 标记加载更多出现失败
     */
    fun addDataError(adapter: RecyclerView.Adapter<*>) {
        postData {
            loadMoreError()
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
     * 删除内容列表，并刷新数据
     */
    fun remove(indexArray: IntArray, adapter: RecyclerView.Adapter<*>) {
        val removeList = mutableListOf<T>()
        itemList().forEachIndexed { index, t ->
            if (index in indexArray && t != null) {
                removeList.add(t)
            }
        }
        remove(removeList, adapter)
    }

    /**
     * 修改某一项数据，并刷新变化的数据
     */
    fun change(position: Int, adapter: RecyclerView.Adapter<*>, payload: Any? = null, change: (value: T) -> Unit = {}) {
        item(position)?.let {
            change(it)
            adapter.notifyItemChanged(position, payload)
        }
    }

    /**
     * 修改某一项数据，并刷新变化的数据
     */
    fun change(value: T?, adapter: RecyclerView.Adapter<*>, payload: Any? = null) {
        val position = itemList().indexOfFirst { it == value }
        if (position >= 0) {
            adapter.notifyItemChanged(position, payload)
        }
    }

    /**
     * 将 content 从 startItemPosition 移动到 endItemPosition
     */
    fun move(startItemPosition: Int, endItemPosition: Int, adapter: RecyclerView.Adapter<*>) {
        if (startItemPosition == endItemPosition) return
        val startContentPosition = mapToContentPosition(startItemPosition)
        val endContentPosition = mapToContentPosition(endItemPosition)
        postData {
            if (startContentPosition < endContentPosition) {
                (startContentPosition until endContentPosition).forEach {
                    Collections.swap(mData, it, it + 1)
                }
            } else if (startContentPosition > endContentPosition) {
                (startContentPosition downTo endContentPosition + 1).forEach {
                    Collections.swap(mData, it, it - 1)
                }
            }
        }.dispatchUpdatesTo(adapter)
    }

    /**
     *
     * itemPosition -> contentPosition
     * itemPosition 包含其他额外的item的位置
     * contentPosition 全部内容的位置
     *
     */
    private fun mapToContentPosition(itemPosition: Int): Int {
        return item(itemPosition)?.let { mData.indexOf(it) } ?: -1
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