package com.github.luoyemyy.mvp.recycler

import android.content.Context
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

internal class RecyclerAdapterDelegate<T, BIND : ViewDataBinding>(private var mWrapper: RecyclerAdapterWrapper<T, BIND>, private var mPresenter: RecyclerPresenterSupport<T>) {

    init {
        /**
         * 监听当前滑动的位置，并保存下来，用于recreate时，还原到该位置
         */
        mWrapper.getRecyclerView().addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val childCount = recyclerView.childCount
                if (childCount > 0) {
                    val view = recyclerView.getChildAt(0) ?: return
                    val position = recyclerView.getChildAdapterPosition(view)
                    if (position >= 0) {
                        mPresenter.onScroll(position, view.top)
                    }
                }
            }
        })
    }

    fun scrollIfNeed(scrollToPosition: Int, scrollToOffset: Int) {
        if (scrollToPosition >= 0 && scrollToPosition < getItemCount() - 1) {
            val layoutManager = mWrapper.getRecyclerView().layoutManager
            when (layoutManager) {
                is LinearLayoutManager -> layoutManager.scrollToPositionWithOffset(scrollToPosition, scrollToOffset)
                is StaggeredGridLayoutManager -> layoutManager.scrollToPositionWithOffset(scrollToPosition, scrollToOffset)
            }
        }
    }

    fun onBindViewHolder(holder: VH<BIND>, position: Int) {
        if (isContentByType(mPresenter.getDataSet().type(position))) {
            val item = getItem(position, true) ?: return
            val binding = holder.binding ?: return
            mWrapper.bindContentViewHolder(binding, item, position)
        }
    }

    fun onBindViewHolder(holder: VH<BIND>, position: Int, payloads: MutableList<Any>) {
        var dispatch = true
        if (payloads.isNotEmpty()) {
            val item = getItem(position, true)
            val binding = holder.binding
            dispatch = if (item == null || binding == null) {
                true
            } else {
                !mWrapper.bindContentViewHolder(binding, item, position, payloads)
            }
        }
        if (dispatch) {
            onBindViewHolder(holder, position)
        }
    }

    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH<BIND> {
        return if (isContentByType(viewType)) {
            val binding = mWrapper.createContentView(LayoutInflater.from(parent.context), parent, viewType)
            VH(binding, binding.root).apply {
                mWrapper.bindItemEvents(this)
                mWrapper.getItemClickViews(binding).forEach {
                    it.setOnClickListener { v ->
                        mWrapper.onItemClickListener(this, v)
                    }
                }
            }
        } else {
            VH(null, createExtraView(parent, viewType))
        }
    }

    fun getItemViewType(position: Int): Int {
        val type = mPresenter.getDataSet().type(position)
        return if (isContentByType(type)) {
            mWrapper.getContentType(position, getItem(position))
        } else {
            type
        }
    }

    fun getItemCount(): Int {
        return mPresenter.getDataSet().count()
    }

    private fun getItem(position: Int, triggerLoadMore: Boolean): T? {
        if (triggerLoadMore && (position > getItemCount() - mWrapper.startLoadMorePosition())) {
            Handler().post {
                mPresenter.loadMore()
            }
        }
        return mPresenter.getDataSet().item(position)
    }

    fun getItem(position: Int): T? {
        return getItem(position, false)
    }

    private fun isContentByType(type: Int): Boolean {
        return type > 0
    }

    private fun createExtraView(parent: ViewGroup, viewType: Int): View {
        return when (viewType) {
            DataSet.EMPTY -> createEmptyView(parent.context)
            DataSet.MORE_LOADING -> createMoreLoadingView(parent.context)
            DataSet.MORE_END -> createMoreEndView(parent.context)
            DataSet.MORE_ERROR -> createMoreErrorView(parent.context)
            else -> View(parent.context)
        }
    }

    private fun dp2px(context: Context, dp: Int): Int {
        return Math.round(context.resources.displayMetrics.density * dp)
    }

    private fun createLayout(context: Context, text: String): LinearLayout {
        val padding = dp2px(context, 16)
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.HORIZONTAL
        layout.gravity = Gravity.CENTER
        layout.setPadding(padding, padding, padding, padding)
        val textView = TextView(context)
        textView.text = text
        layout.addView(textView)
        layout.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
        return layout
    }

    private fun createEmptyView(context: Context): View {
        return if (mWrapper.getEmptyLayout() == 0) {
            createLayout(context, "暂无数据")
        } else {
            LayoutInflater.from(context).inflate(mWrapper.getEmptyLayout(), mWrapper.getRecyclerView(), false)
        }
    }

    private fun createMoreEndView(context: Context): View {
        return if (mWrapper.getMoreEndLayout() == 0) {
            createLayout(context, "暂无更多")
        } else {
            LayoutInflater.from(context).inflate(mWrapper.getMoreEndLayout(), mWrapper.getRecyclerView(), false)
        }
    }

    private fun createMoreErrorView(context: Context): View {
        return if (mWrapper.getMoreErrorLayout() == 0) {
            createLayout(context, "加载失败")
        } else {
            LayoutInflater.from(context).inflate(mWrapper.getMoreErrorLayout(), mWrapper.getRecyclerView(), false)
        }.apply {
            setOnClickListener {
                mPresenter.loadMore()
            }
        }
    }

    private fun createMoreLoadingView(context: Context): View {
        return if (mWrapper.getMoreLoadingLayout() == 0) {
            val layout = createLayout(context, "加载中...")
            val padding = dp2px(context, 8)
            val progressSize = dp2px(context, 20)
            val process = ProgressBar(context)
            layout.addView(process, 0, LinearLayout.LayoutParams(progressSize, progressSize).apply { marginEnd = padding })
            layout
        } else {
            LayoutInflater.from(context).inflate(mWrapper.getMoreLoadingLayout(), mWrapper.getRecyclerView(), false)
        }
    }
}