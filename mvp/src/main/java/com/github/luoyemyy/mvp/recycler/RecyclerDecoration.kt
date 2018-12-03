package com.github.luoyemyy.mvp.recycler

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

class RecyclerDecoration(private val context: Context, bottomDp: Int = 0, topDp: Int = 0) : RecyclerView.ItemDecoration() {

    private val bottomPx = dp2px(bottomDp)
    private val topPx = dp2px(topDp)

    private fun dp2px(dp: Int): Int = (context.resources.displayMetrics.density * dp).roundToInt()

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (topPx > 0) {
            outRect.top = topPx
        }
        if (bottomPx > 0) {
            outRect.bottom = bottomPx
        }
    }
}