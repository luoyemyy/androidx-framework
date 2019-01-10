package com.github.luoyemyy.mvp.recycler

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

fun RecyclerView.setLinearManager() {
    layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
}

fun RecyclerView.setHorizontalManager() {
    layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
}

fun RecyclerView.setGridManager(span: Int) {
    layoutManager = StaggeredGridLayoutManager(span, StaggeredGridLayoutManager.VERTICAL)
}

fun SwipeRefreshLayout.wrap(presenter: RecyclerPresenterSupport<*>) {
    setOnRefreshListener {
        setProgressViewEndTarget(false, (context.resources.displayMetrics.density * 64).toInt())
        presenter.loadRefresh()
    }
}