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
    private var mVWidth: Int = 0
    private var mVHeight: Int = 0
    private var mLastScaleX: Float = 0f
    private var mLastScaleY: Float = 0f
    private val mAnimDuration = 240
    private val mMaxScale = 10f
    private val mMinScale = 0.5f
    private var mCurrentAction: Action? = null

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
        if (dWidth > 0 && dHeight > 0 && scaleType != ScaleType.MATRIX) {
            mMatrix.set(imageMatrix)
            mResetMatrix.set(imageMatrix)
            scaleType = ScaleType.MATRIX
            imageMatrix = mMatrix
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

    private fun equalsMatrix(matrix1: Matrix, matrix2: Matrix): Boolean {
        val array1 = getMatrixValues(matrix1)
        val array2 = getMatrixValues(matrix2)
        return (0..8).all { array1[it] == array2[it] }
    }

    fun addAction(action: Action) {
        mCurrentAction?.apply {
            if (action.canRun(this)) {
                if (isRunning) {
                    cancel()
                }
                mCurrentAction = action
                action.start()
            }
        } ?: let {
            mCurrentAction = action
            action.start()
        }
    }

    /**
     * 接管事件
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return false
        mGestureDetector.onTouchEvent(event)
        mScaleGestureDetector.onTouchEvent(event)

        //停止手势时 先执行fling  然后检查缩放和边界
        if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_UP) {
            (mCurrentAction as? FlingAction) ?: let {
                addAction(LimitAction())
            }
        }
        return true
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
     * 动画
     */
    protected fun animator(startMatrix: Matrix, endMatrix: Matrix): ValueAnimator {
        return ValueAnimator().apply {
            val start = getMatrixValues(startMatrix)
            val end = getMatrixValues(endMatrix)
            setObjectValues(start, end)
            duration = mAnimDuration.toLong()
            setEvaluator { fraction, _, _ ->
                val v = FloatArray(9)
                for (i in (0..8)) {
                    v[i] = start[i] + (end[i] - start[i]) * fraction
                }
                Matrix().apply { setValues(v) }
            }
            addUpdateListener {
                mMatrix.set(it.animatedValue as Matrix)
                imageMatrix = mMatrix
            }
            start()
        }
    }

    protected fun copyCurrentMatrix(): Matrix {
        return Matrix(mMatrix)
    }

    private fun getDrawableRect(): RectF? {
        val dWidth = drawable?.intrinsicWidth ?: 0
        val dHeight = drawable?.intrinsicHeight ?: 0
        if (dWidth == 0 || dHeight == 0) return null
        return RectF(0f, 0f, dWidth.toFloat(), dHeight.toFloat())
    }

    protected fun getBitmapRect(matrix: Matrix): RectF? {
        return getDrawableRect()?.let {
            matrix.mapRect(it)
            it
        }
    }

    private fun getLimitRect(): RectF {
        return RectF(0f, 0f, mVWidth.toFloat(), mVHeight.toFloat())
    }


    /**
     * 限制
     */
    protected open fun calculateLimitMatrix(): Matrix? {
        val endMatrix = copyCurrentMatrix()

        //scale
        val currentScale = getMatrixValues(mMatrix)[Matrix.MSCALE_X]
        val dScale = when {
            currentScale > mMaxScale -> mMaxScale / currentScale
            currentScale < mMinScale -> mMinScale / currentScale
            else -> 0f
        }
        if (dScale > 0) {
            endMatrix.postScale(dScale, dScale, mLastScaleX, mLastScaleY)
        }

        val bitmapRect = getBitmapRect(endMatrix) ?: return null
        val limitRect = getLimitRect()

        //scroll
        val (x, y) = calculateScroll(bitmapRect, limitRect)
        endMatrix.postTranslate(x, y)
        return endMatrix
    }

    protected fun calculateScroll(bitmapRect: RectF, limitRect: RectF): Pair<Float, Float> {
        val x = when {
            //bitmap 宽度 小于 限制宽度
            bitmapRect.right - bitmapRect.left <= limitRect.right - limitRect.left -> when {
                //bitmap 左边 小于 限制左边 ，向右移差值
                bitmapRect.left < limitRect.left -> limitRect.left - bitmapRect.left
                //bitmap 右边 大于 限制右边 ，向左移差值
                bitmapRect.right > limitRect.right -> limitRect.right - bitmapRect.right
                else -> 0f
            }
            //bitmap 宽度 大于 限制宽度
            bitmapRect.right - bitmapRect.left > limitRect.right - limitRect.left -> when {
                //bitmap 左边 大于 限制左边 ，向左移差值
                bitmapRect.left > limitRect.left -> limitRect.left - bitmapRect.left
                //bitmap 右边 小于 限制右边 ，向右移差值
                bitmapRect.right < limitRect.right -> limitRect.right - bitmapRect.right
                else -> 0f
            }
            else -> 0f
        }

        val y = when {
            //bitmap 高度度 小于 限制高度
            bitmapRect.bottom - bitmapRect.top <= limitRect.bottom - limitRect.top -> when {
                //bitmap 上边 小于 限制上边 ，向下移差值
                bitmapRect.top < limitRect.top -> limitRect.top - bitmapRect.top
                //bitmap 下边 大于 限制下边 ，向上移差值
                bitmapRect.bottom > limitRect.bottom -> limitRect.bottom - bitmapRect.bottom
                else -> 0f
            }
            //bitmap 高度 大于 限制高度
            bitmapRect.bottom - bitmapRect.top > limitRect.bottom - limitRect.top -> when {
                //bitmap 上边 大于 限制上边 ，向上移差值
                bitmapRect.top > limitRect.top -> limitRect.top - bitmapRect.top
                //bitmap 下边 小于 限制下边 ，向下移差值
                bitmapRect.bottom < limitRect.bottom -> limitRect.bottom - bitmapRect.bottom
                else -> 0f
            }
            else -> 0f
        }
        return Pair(x, y)
    }

    inner class ScaleGestureListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {

        override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
            mImageViewListeners.forEach { it.onChange() }
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mLastScaleX = detector.focusX
            mLastScaleY = detector.focusY
            addAction(ScaleAction(detector.scaleFactor, detector.focusX, detector.focusY))
            return true
        }
    }

    inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent?): Boolean {
            setMatrixType()
            return false
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            addAction(ScrollAction(-distanceX, -distanceY))
            return true
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            addAction(ResetAction())
            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            mImageViewListeners.forEach { it.onSingleTap() }
            return super.onSingleTapConfirmed(e)
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            addAction(FlingAction(velocityX, velocityY))
            return true
        }

    }

    inner class ScrollAction(private val dx: Float, private val dy: Float) : Action(1) {
        override fun start() {
            isRunning = true
            translate(dx, dy)
            isRunning = false
        }
    }

    inner class ScaleAction(private val scale: Float, private val x: Float, private val y: Float) : Action(1) {
        override fun start() {
            isRunning = true
            scale(scale, x, y)
            isRunning = false
        }
    }

    inner class ResetAction : Action(2) {

        private lateinit var mAnimator: ValueAnimator

        override fun start() {
            mAnimator = animator(mMatrix, mResetMatrix).apply {
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        this@ResetAction.isRunning = false
                    }
                })
            }
            isRunning = true
        }

        override fun cancel() {
            if (isRunning && this::mAnimator.isInitialized) {
                mAnimator.cancel()
                isRunning = false
            }
        }
    }

    inner class FlingAction(velocityX: Float, velocityY: Float) : Action(3) {
        private lateinit var mAnimator: ValueAnimator
        private var prev = 0.0f
        private var prevVelocityX = velocityX / 3000
        private var prevVelocityY = velocityY / 3000
        private val ax = prevVelocityX / mAnimDuration.toFloat()
        private val ay = prevVelocityY / mAnimDuration.toFloat()
        override fun start() {
            mAnimator = ObjectAnimator.ofFloat(0f, mAnimDuration.toFloat()).apply {
                interpolator = LinearInterpolator()
                duration = mAnimDuration.toLong()

                addUpdateListener {
                    val time = it.animatedValue as Float
                    val dt = time - prev
                    val dx = prevVelocityX * dt
                    val dy = prevVelocityY * dt

                    translate(dx, dy)
                    prevVelocityX -= ax * dt
                    prevVelocityY -= ay * dt
                    prev = time
                }

                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        this@FlingAction.isRunning = false
                        addAction(LimitAction())
                    }
                })
                start()
            }
            isRunning = true
        }

        override fun cancel() {
            if (isRunning && this::mAnimator.isInitialized) {
                mAnimator.cancel()
                isRunning = false
            }
        }

    }

    inner class LimitAction : Action(3) {
        private lateinit var mAnimator: ValueAnimator
        override fun start() {
            val endMatrix = calculateLimitMatrix() ?: return
            if (!equalsMatrix(mMatrix, endMatrix)) {
                mAnimator = animator(mMatrix, endMatrix).apply {
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            this@LimitAction.isRunning = false
                        }
                    })
                }
                isRunning = true
            }
        }

        override fun cancel() {
            if (isRunning && this::mAnimator.isInitialized) {
                mAnimator.cancel()
                isRunning = false
            }
        }

    }

    abstract inner class Action(private val priority: Int) {

        var isRunning: Boolean = false

        fun canRun(prevAction: Action) = !prevAction.isRunning || priority <= prevAction.priority

        abstract fun start()

        open fun cancel() {}
    }

}