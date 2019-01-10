package com.github.luoyemyy.picker.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.animation.LinearInterpolator
import android.widget.ImageView

open class PreviewImageView(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : ImageView(context, attributeSet, defStyleAttr, defStyleRes) {

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : this(context, attributeSet, defStyleAttr, 0)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0, 0)
    constructor(context: Context) : this(context, null, 0, 0)

    private var mMatrix = Matrix()
    private var mResetMatrix = Matrix()
    private var mImageViewListeners = mutableListOf<ImageViewListener>()
    private val mScaleGestureDetector = ScaleGestureDetector(context, ScaleGestureListener())
    private val mGestureDetector = GestureDetector(context, GestureListener())
    private var mChange = false
    private var mFling = false
    private var mVWidth: Int = 0
    private var mVHeight: Int = 0
    private var mLastScaleX: Float = 0f
    private var mLastScaleY: Float = 0f

    init {
        viewTreeObserver.addOnGlobalLayoutListener {
            if (mVWidth == 0 && mVHeight == 0) {
                mVWidth = width
                mVHeight = height
            }
        }
    }

    fun addImageViewListener(listener: ImageViewListener) {
        mImageViewListeners.add(listener)
    }

    /**
     * 设置图片时，设置当前的scaleType
     */
    override fun setImageDrawable(drawable: Drawable?) {
        scaleType = ScaleType.CENTER_INSIDE
        super.setImageDrawable(drawable)
    }

    private fun setMatrixType() {
        val dWidth = drawable?.intrinsicWidth ?: 0
        val dHeight = drawable?.intrinsicHeight ?: 0
        if (dWidth > 0 && dHeight > 0) {
            mMatrix.set(imageMatrix)
            mResetMatrix.set(imageMatrix)
            scaleType = ScaleType.MATRIX
            imageMatrix = mMatrix

//            val vWidth = mVWidth
//            val vHeight = mVHeight
//
//            mMatrix.reset()
//
//            val scale = if (dWidth <= vWidth && dHeight <= vHeight) {
//                1.0f
//            } else {
//                Math.min(vWidth.toFloat() / dWidth.toFloat(), vHeight.toFloat() / dHeight.toFloat())
//            }
//
//            val dx = Math.round((vWidth - dWidth * scale) * 0.5f).toFloat()
//            val dy = Math.round((vHeight - dHeight * scale) * 0.5f).toFloat()
//
//            mMatrix.postScale(scale, scale)
//            mMatrix.postTranslate(dx, dy)
//
//            mResetMatrix.set(mMatrix)
//            scaleType = ScaleType.MATRIX
//            imageMatrix = mMatrix
        }
    }

    /**
     * 获得指定matrix的值
     */
    protected fun getMatrixValues(matrix: Matrix): FloatArray {
        val array = FloatArray(9)
        matrix.getValues(array)
        return array
    }

    protected fun getResetValues(): FloatArray {
        return getMatrixValues(mResetMatrix)
    }

    protected fun getCurrentValues(): FloatArray {
        return getMatrixValues(mMatrix)
    }

    /**
     * 重置当前图片
     */
    private fun reset() {
        animator(mMatrix, mResetMatrix)
    }

    /**
     * 动画
     */
    protected fun animator(startMatrix: Matrix, endMatrix: Matrix) {
        val start = getMatrixValues(startMatrix)
        val end = getMatrixValues(endMatrix)

        val animator = ValueAnimator()
        animator.setObjectValues(start, end)
        animator.duration = 300
        animator.setEvaluator { fraction, _, _ ->
            val v = FloatArray(9)
            for (i in (0..8)) {
                v[i] = start[i] + (end[i] - start[i]) * fraction
            }
            Matrix().apply { setValues(v) }
        }
        animator.addUpdateListener {
            mMatrix.set(it.animatedValue as Matrix)
            imageMatrix = mMatrix
        }
        animator.start()
    }

    /**
     * 接管事件
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return false
        mGestureDetector.onTouchEvent(event)
        mScaleGestureDetector.onTouchEvent(event)

        if (mChange && event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_UP) {
            changeEnd()
        }
        return true
    }

    /**
     * 图片有变化时，变化结束会调用该方法
     */
    open fun changeEnd() {
        if (!mFling) {
            limit()
        }
    }

    /**
     * 图片缩放
     */
    fun scale(scale: Float, x: Float, y: Float) {
        mMatrix.postScale(scale, scale, x, y)
        imageMatrix = mMatrix
    }

    /**
     * 图片平移
     */
    fun translate(x: Float, y: Float) {
        mMatrix.postTranslate(x, y)
        imageMatrix = mMatrix
    }

    /**
     * 限制
     */
    private fun limit() {
        val endMatrix = Matrix(mMatrix)
        val scale = getMatrixValues(mMatrix)[Matrix.MSCALE_X]
        val dScale = if (scale > 10) 10f / scale else if (scale < 0.5) 0.5f / scale else 0f
        if (dScale > 0) {
            endMatrix.postScale(dScale, dScale, mLastScaleX, mLastScaleY)
        }

        val dWidth = drawable?.intrinsicWidth ?: 0
        val dHeight = drawable?.intrinsicHeight ?: 0
        if (dWidth == 0 || dHeight == 0) return
        val src = RectF(0f, 0f, dWidth.toFloat(), dHeight.toFloat())
        endMatrix.mapRect(src)

        val bWidth = src.right - src.left
        val bHeight = src.bottom - src.top

        val vWidth = width
        val vHeight = height

        val x = when {
            bWidth <= vWidth -> when {
                src.left < 0 -> -src.left
                src.right > vWidth -> vWidth.toFloat() - src.right
                else -> 0f
            }
            bWidth > vWidth -> when {
                src.left > 0 -> -src.left
                src.right < vWidth -> vWidth.toFloat() - src.right
                else -> 0f
            }
            else -> 0f
        }

        val y = when {
            bHeight <= vHeight -> when {
                src.top < 0 -> -src.top
                src.bottom > vHeight -> vHeight.toFloat() - src.bottom
                else -> 0f
            }
            bHeight > vHeight -> when {
                src.top > 0 -> -src.top
                src.bottom < vHeight -> vHeight.toFloat() - src.bottom
                else -> 0f
            }
            else -> 0f
        }

        endMatrix.postTranslate(x, y)
        animator(mMatrix, endMatrix)
    }

    inner class ScaleGestureListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {

        override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
            mImageViewListeners.forEach { it.onChange() }
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mLastScaleX = detector.focusX
            mLastScaleY = detector.focusY
            scale(detector.scaleFactor, detector.focusX, detector.focusY)
            mChange = true
            return true
        }
    }

    inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent?): Boolean {
            mChange = false
            setMatrixType()
            return false
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            mImageViewListeners.forEach { it.onChange() }
            translate(-distanceX, -distanceY)
            mChange = true
            return true
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            mImageViewListeners.forEach { it.onChange() }
            reset()
            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            mImageViewListeners.forEach { it.onSingleTap() }
            return super.onSingleTapConfirmed(e)
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            mFling = true
            val ax = velocityX / 400
            val ay = velocityY / 400
            var prev = 0.0f
            var prevVelocityX = velocityX
            var prevVelocityY = velocityY
            ObjectAnimator.ofInt(0, 400).apply {
                interpolator = LinearInterpolator()
                duration = 400

                addUpdateListener {
                    val time = it.animatedValue as Float
                    val dt = time - prev
                    val dx = prevVelocityX * dt
                    val dy = prevVelocityX * dt

                    translate(dx, dy)
                    prevVelocityX -= ax * dt
                    prevVelocityY -= ay * dt
                    prev = time
                }

                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        mFling = false
                        limit()
                    }

                })
            }.start()
            return true
        }

    }

}