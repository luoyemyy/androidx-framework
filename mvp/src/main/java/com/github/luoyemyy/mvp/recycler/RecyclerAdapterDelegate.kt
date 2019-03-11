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

internal class RecyclerAdapterDelegate<T, BIND : ViewDataBinding>(
        private var mWrapper: RecyclerAdapterWrapper<T, BIND>,
        private var mPresenter: RecyclerPresenterSupport<T>) {

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
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    recyclerView.canScrollVertically(-1)
//                    val count = recyclerView.childCount
//                    val itemCount = recyclerView.adapter?.itemCount ?: -1
//                    if (count > 0 && itemCount > 0) {
//                        val child = recyclerView.getChildAt(count - 1) ?: return
//                        val position = recyclerView.getChildAdapterPosition(child)
//                        if (position > itemCount - mWrapper.startLoadMorePosition()) {
//                            Handler().post {
//                                mPresenter.loadMore()
//                            }
//                        }
//                    }
//                }
//            }
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
        } else {
            holder.binding?.apply {
                mWrapper.bindExtraViewHolder(this, position)
            }
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
        if (isContentByType(viewType)) {
            mWrapper.createContentView(LayoutInflater.from(parent.context), parent, viewType)?.let { binding ->
                return VH(binding, binding.root).apply {
                    mWrapper.bindItemEvents(this)
                    mWrapper.getItemClickViews(binding).forEach {
                        it.setOnClickListener { v ->
                            mWrapper.onItemClickListener(this, v)
                        }
                    }
                }
            }
        }
        return VH(null, createExtraView(parent, viewType))
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
        return type >= DataSet.CONTENT
    }

    private fun createExtraView(parent: ViewGroup, viewType: Int): View {
        return when (viewType) {
            DataSet.EMPTY -> createEmptyView(parent.context, parent)
            DataSet.MORE_LOADING -> createMoreLoadingView(parent.context, parent)
            DataSet.MORE_END -> createMoreEndView(parent.context, parent)
            DataSet.MORE_ERROR -> createMoreErrorView(parent.context, parent)
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

    private fun createEmptyView(context: Context, parent: ViewGroup): View {
        return mWrapper.getEmptyBinding(context, parent)?.root ?: createLayout(context, "暂无数据")
    }

    private fun createMoreEndView(context: Context, parent: ViewGroup): View {
        return mWrapper.getMoreEndBinding(context, parent)?.root ?: createLayout(context, "暂无更多")
    }

    private fun createMoreErrorView(context: Context, parent: ViewGroup): View {
        return (mWrapper.getMoreErrorBinding(context, parent)?.root
                ?: createLayout(context, "加载失败")).apply {
            setOnClickListener {
                mPresenter.loadMore()
            }
        }
    }

    private fun createMoreLoadingView(context: Context, parent: ViewGroup): View {
        return mWrapper.getMoreLoadingBinding(context, parent)?.root ?: let {
            val layout = createLayout(context, "加载中...")
            val padding = dp2px(context, 8)
            val progressSize = dp2px(context, 20)
            val process = ProgressBar(context)
            layout.addView(process, 0, LinearLayout.LayoutParams(progressSize, progressSize).apply { marginEnd = padding })
            layout
        }
    }
}