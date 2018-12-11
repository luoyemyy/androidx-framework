package com.github.luoyemyy.mvp.recycler

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

class RecyclerDecoration private constructor(private val type: Int, private val context: Context, space: Int, spaceUnitPx: Boolean) : RecyclerView.ItemDecoration() {

    companion object {

        private const val BEGIN = 1
        private const val MIDDLE = 1 shl 1
        private const val END = 1 shl 2

        /**
         * @param context
         * @param space         分割距离
         * @param spaceUnit     space的单位 true px false dp
         */
        fun beginEnd(context: Context, space: Int = 1, spaceUnit: Boolean = false): RecyclerDecoration {
            return RecyclerDecoration(BEGIN or END, context, space, spaceUnit)
        }

        /**
         * @param context
         * @param space         分割距离
         * @param spaceUnit     space的单位 true px false dp
         */
        fun middleEnd(context: Context, space: Int = 1, spaceUnit: Boolean = false): RecyclerDecoration {
            return RecyclerDecoration(MIDDLE or END, context, space, spaceUnit)
        }

        /**
         * @param context
         * @param space         分割距离
         * @param spaceUnit     space的单位 true px false dp
         */
        fun beginMiddle(context: Context, space: Int = 1, spaceUnit: Boolean = false): RecyclerDecoration {
            return RecyclerDecoration(BEGIN or MIDDLE, context, space, spaceUnit)
        }

        /**
         * @param context
         * @param space         分割距离
         * @param spaceUnit     space的单位 true px false dp
         */
        fun beginMiddleEnd(context: Context, space: Int = 1, spaceUnit: Boolean = false): RecyclerDecoration {
            return RecyclerDecoration(BEGIN or MIDDLE or END, context, space, spaceUnit)
        }

        /**
         * @param context
         * @param space         分割距离
         * @param spaceUnit     space的单位 true px false dp
         */
        fun begin(context: Context, space: Int = 1, spaceUnit: Boolean = false): RecyclerDecoration {
            return RecyclerDecoration(BEGIN, context, space, spaceUnit)
        }

        /**
         * @param context
         * @param space         分割距离
         * @param spaceUnit     space的单位 true px false dp
         */
        fun middle(context: Context, space: Int = 1, spaceUnit: Boolean = false): RecyclerDecoration {
            return RecyclerDecoration(MIDDLE, context, space, spaceUnit)
        }

        /**
         * @param context
         * @param space         分割距离
         * @param spaceUnit     space的单位 true px false dp
         */
        fun end(context: Context, space: Int = 1, spaceUnit: Boolean = false): RecyclerDecoration {
            return RecyclerDecoration(END, context, space, spaceUnit)
        }
    }


    private var spacePx = if (spaceUnitPx) space else dp2px(context, space)

    private var drawDivider: Boolean = false
    private var leftPx: Int = 0
    private var rightPx: Int = 0
    private lateinit var paint: Paint

    /**
     * @param color         分割颜色  0 不绘制分割线
     * @param left          分割线左边padding
     * @param right         分割线右边padding
     * @param paddingUnitPx   分割线padding的单位 true px false dp
     */
    fun drawDivider(color: Int = 0x1e000000, left: Int = 0, right: Int = 0, paddingUnitPx: Boolean = false): RecyclerDecoration {
        this.drawDivider = true
        this.leftPx = if (paddingUnitPx) left else dp2px(context, left)
        this.rightPx = if (paddingUnitPx) right else dp2px(context, right)
        this.paint = Paint().apply {
            this.style = Paint.Style.FILL
            this.color = color
        }
        return this
    }


    private fun dp2px(context: Context, dp: Int): Int = (context.resources.displayMetrics.density * dp).roundToInt()

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val itemCount = parent.adapter?.itemCount ?: return
        if (itemCount > 0) {
            val position = parent.getChildAdapterPosition(view)
            if (type and BEGIN == BEGIN && position == 0) {
                outRect.top = spacePx
            }
            if (type and MIDDLE == MIDDLE && position >= 0 && position < itemCount - 1) {
                outRect.bottom = spacePx
            }
            if (type and END == END && position == itemCount - 1) {
                outRect.bottom = spacePx
            }
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val itemCount = parent.adapter?.itemCount ?: return
        val childCount = parent.childCount
        if (drawDivider && itemCount > 0 && childCount > 0) {
            c.save()
            (0 until childCount).forEach { i ->
                parent.getChildAt(i)?.apply {
                    val position = parent.getChildAdapterPosition(this)
                    if (type and BEGIN == BEGIN && position == 0) {
                        c.drawRect(Rect(leftPx, top - spacePx, right - rightPx, top), paint)
                    }
                    if (type and MIDDLE == MIDDLE && position >= 0 && position < itemCount - 1) {
                        c.drawRect(Rect(leftPx, bottom, right - rightPx, bottom + spacePx), paint)
                    }
                    if (type and END == END && position == itemCount - 1) {
                        c.drawRect(Rect(leftPx, bottom, right - rightPx, bottom + spacePx), paint)
                    }
                }
            }
            c.restore()
        }
    }
}