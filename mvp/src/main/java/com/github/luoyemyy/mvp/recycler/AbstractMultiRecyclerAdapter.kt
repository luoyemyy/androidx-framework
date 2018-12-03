package com.github.luoyemyy.mvp.recycler

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView


abstract class AbstractMultiRecyclerAdapter(recyclerView: RecyclerView) : BaseRecyclerAdapter<Any, ViewDataBinding>(recyclerView)