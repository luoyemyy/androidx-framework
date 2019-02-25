package com.github.luoyemyy.mvp.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

interface RecyclerAdapterWrapper<T, BIND : ViewDataBinding> {

    /**
     * 获得指定位置的数据，如果是加载更多或空数据项，则为null
     */
    fun getItem(position: Int): T?

    /**
     * 绑定额外项数据
     */
    fun bindExtraViewHolder(binding: ViewDataBinding, position: Int){}

    /**
     * 绑定数据
     */
    fun bindContentViewHolder(binding: BIND, content: T, position: Int)

    /**
     * 绑定数据
     */
    fun bindContentViewHolder(binding: BIND, content: T, position: Int, payloads: MutableList<Any>): Boolean {
        return false
    }

    /**
     * 创建内容view
     */
    fun createContentView(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): BIND?

    /**
     * 获得内容类型id，如果是多类型需要重写该方法
     */
    fun getContentType(position: Int, item: T?): Int

    /**
     * 获得需要绑定点击事件的view
     */
    fun getItemClickViews(binding: BIND): Array<View> {
        return arrayOf(binding.root)
    }

    /**
     * 点击绑定了事件的view的回调方法
     */
    fun onItemClickListener(vh: VH<BIND>, view: View?) {}

    /**
     * 增加绑定除了点击事件之外的其他事件
     */
    fun bindItemEvents(vh: VH<BIND>) {}

    /**
     * 获得加载更多-加载中样式
     */
    fun getMoreLoadingBinding(context: Context,parent: ViewGroup): ViewDataBinding? = null

    /**
     * 获得加载更多-暂无更多样式
     */
    fun getMoreEndBinding(context: Context,parent: ViewGroup): ViewDataBinding? = null

    /**
     * 获得空数据样式
     */
    fun getEmptyBinding(context: Context,parent: ViewGroup): ViewDataBinding? = null

    /**
     * 获得加载更多-加载失败样式
     */
    fun getMoreErrorBinding(context: Context,parent: ViewGroup): ViewDataBinding? = null

    fun getRecyclerView(): RecyclerView

    /**
     * 当展示到倒数第N个的时候就开始加载更多
     */
    fun startLoadMorePosition(): Int {
        return 3
    }
}