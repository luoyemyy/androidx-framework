package com.github.luoyemyy.mvp.recycler

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView


abstract class AbstractSingleRecyclerAdapter<T, BIND : ViewDataBinding>(recyclerView: RecyclerView) : BaseRecyclerAdapter<T, BIND>(recyclerView) {

    override fun getContentType(position: Int, item: T?): Int {
        return DataSet.CONTENT
    }
}