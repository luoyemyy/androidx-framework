package com.github.luoyemyy.mvp.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView


abstract class AbstractMultiRecyclerAdapter(recyclerView: RecyclerView) : BaseRecyclerAdapter<Any, ViewDataBinding>(recyclerView) {

    @LayoutRes
    abstract fun getLayoutId(viewType: Int): Int

    override fun createContentView(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): ViewDataBinding? {
        val layoutId = getLayoutId(viewType)
        return if (layoutId > 0) {
            DataBindingUtil.inflate(inflater, layoutId, parent, false)
        } else {
            null
        }
    }
}

abstract class AbstractSingleRecyclerAdapter<T, BIND : ViewDataBinding>(recyclerView: RecyclerView) : BaseRecyclerAdapter<T, BIND>(recyclerView) {

    @LayoutRes
    abstract fun getLayoutId(): Int

    override fun getContentType(position: Int, item: T?): Int {
        return DataSet.CONTENT
    }

    override fun createContentView(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): BIND? {
        return DataBindingUtil.inflate(inflater, getLayoutId(), parent, false)
    }
}

abstract class BaseRecyclerAdapter<T, BIND : ViewDataBinding>(private var mRecyclerView: RecyclerView) : RecyclerView.Adapter<VH<BIND>>(), RecyclerAdapterWrapper<T, BIND>, RecyclerAdapterSupport<T> {

    /**
     * 辅助类
     */
    private lateinit var mDelegate: RecyclerAdapterDelegate<T, BIND>

    override fun setup(presenterSupport: RecyclerPresenterSupport<T>) {
        mDelegate = RecyclerAdapterDelegate(this, presenterSupport)
    }

    /**
     * 获得指定项内容实体
     */
    override fun getItem(position: Int): T? {
        return mDelegate.getItem(position)
    }

    override fun getAdapter(): RecyclerView.Adapter<*> {
        return this
    }

    /**
     * 将adapter设置到recyclerView并滑动到指定的位置
     */
    override fun attachToRecyclerView(scrollToPosition: Int, scrollToOffset: Int) {
        mRecyclerView.adapter = this
        mDelegate.scrollIfNeed(scrollToPosition, scrollToOffset)
    }

    override fun onBindViewHolder(holder: VH<BIND>, position: Int) {
        mDelegate.onBindViewHolder(holder, position)
    }

    override fun onBindViewHolder(holder: VH<BIND>, position: Int, payloads: MutableList<Any>) {
        mDelegate.onBindViewHolder(holder, position, payloads)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH<BIND> {
        return mDelegate.onCreateViewHolder(parent, viewType)
    }

    /**
     * @return 如果没有匹配的类型则返回0 内容视图的类型必须大于0
     */
    override fun getItemViewType(position: Int): Int {
        return mDelegate.getItemViewType(position)
    }

    override fun getItemCount(): Int {
        return mDelegate.getItemCount()
    }

    override fun getRecyclerView(): RecyclerView {
        return mRecyclerView
    }
}