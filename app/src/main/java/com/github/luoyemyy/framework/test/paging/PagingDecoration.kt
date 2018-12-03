//package com.github.luoyemyy.framework.test.paging
//
//import android.graphics.Canvas
//import android.graphics.Rect
//import android.support.v7.widget.RecyclerView
//import android.view.View
//
//class PagingDecoration(private val loadLayout: View, private val emptyLayout: View) : RecyclerView.ItemDecoration() {
//
//    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
////        if (parent.adapter?.itemCount - 1 == parent.getChildAdapterPosition(view)) {
////            outRect.bottom = loadLayout.height
////        }
//    }
//
//    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
//        emptyLayout.visibility = if (parent.childCount == 0) View.VISIBLE else View.GONE
//        loadLayout.visibility = View.GONE
//        (0 until parent.childCount).forEach {
//            val view = parent.getChildAt(it)
//            if (view != null) {
//                val position = parent.getChildAdapterPosition(view)
////                if (position == parent.adapter.itemCount - 1) {
////                    loadLayout.visibility = View.VISIBLE
////                    loadLayout.translationY = view.bottom.toFloat()
////                }
//            }
//        }
//    }
//}