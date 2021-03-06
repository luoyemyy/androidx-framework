package com.github.luoyemyy.picker.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import com.github.luoyemyy.picker.ImagePicker
import kotlin.math.min

class CropImageView(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : PreviewImageView(context, attributeSet, defStyleAttr, defStyleRes) {

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : this(context, attributeSet, defStyleAttr, 0)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0, 0)
    constructor(context: Context) : this(context, null, 0, 0)

    private var mCropType = ImagePicker.option.cropType
    private var mCropWidth = ImagePicker.option.cropWidth
    private var mCropHeight = ImagePicker.option.cropHeight
    private var mCropPercent = ImagePicker.option.cropPercent
    private var mCropRatio = ImagePicker.option.cropRatio
    private var mMaskColor: Int = 0x80000000.toInt()
    private val mPaint = Paint().apply {
        color = mMaskColor
    }
    private val mStrokePaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawMask(canvas)
    }

    /**
     * 画裁剪区域
     */
    private fun drawMask(canvas: Canvas?) {
        val cropRect = calculateCropSpace()
        val leftRect = RectF(0f, cropRect.top, cropRect.left, cropRect.bottom)
        val topRect = RectF(0f, 0f, width.toFloat(), cropRect.top)
        val rightRect = RectF(cropRect.right, cropRect.top, width.toFloat(), cropRect.bottom)
        val bottomRect = RectF(0f, cropRect.bottom, width.toFloat(), height.toFloat())

        canvas?.drawRect(leftRect, mPaint)
        canvas?.drawRect(topRect, mPaint)
        canvas?.drawRect(rightRect, mPaint)
        canvas?.drawRect(bottomRect, mPaint)
        canvas?.drawRect(cropRect, mStrokePaint)
    }

    private fun cropSize(): Pair<Int, Int> {
        return when (mCropType) {
            1 -> Pair(min(width, mCropWidth), min(height, mCropHeight))
            2 -> {
                val w = width * mCropPercent
                val h = w * mCropRatio
                Pair(w.toInt(), min(height, h.toInt()))
            }
            else -> return Pair(0, 0)
        }
    }

    /**
     * 计算裁剪区域
     */
    private fun calculateCropSpace(): RectF {
        val (x, y) = cropSize()
        return RectF((width / 2 - x / 2).toFloat(), (height / 2 - y / 2).toFloat(), (width / 2 + x / 2).toFloat(), (height / 2 + y / 2).toFloat())
    }

    fun changeCropMask(radio: Float) {
        mCropRatio = radio
        invalidate()
    }

    override fun calculateLimitMatrix(): Matrix? {

        val endMatrix = copyCurrentMatrix()
        var bitmapRect = getBitmapRect(endMatrix) ?: return null
        val limitRect = calculateCropSpace()

        val bitmapWidth = bitmapRect.right - bitmapRect.left
        val bitmapHeight = bitmapRect.bottom - bitmapRect.top
        val limitWidth = limitRect.right - limitRect.left
        val limitHeight = limitRect.bottom - limitRect.top
        val dw = bitmapWidth - limitWidth
        val dh = bitmapHeight - limitHeight

        //缩放  保持高度和宽度不小于裁剪区域
        if (dw < 0 || dh < 0) {
            val scale = if (dw < dh) {
                Math.ceil(limitHeight.toDouble()) / bitmapWidth
            } else {
                Math.ceil(limitHeight.toDouble()) / bitmapHeight
            }.toFloat()
            endMatrix.postScale(scale, scale, bitmapRect.left + bitmapWidth / 2, bitmapRect.top + bitmapHeight / 2)
            bitmapRect = getBitmapRect(endMatrix) ?: return null
        }

        //平移
        val (x, y) = calculateScroll(bitmapRect, limitRect)
        endMatrix.postTranslate(x, y)
        return endMatrix
    }

    private fun checkBounds(): Boolean {
        val afterCropRect = calculateCropSpace() //缩放后-裁剪区域坐标
        val bitmap = (drawable as? BitmapDrawable)?.bitmap ?: return true
        val afterBitmapRect = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat()).also {
            imageMatrix.mapRect(it)//缩放后-图片区域坐标
        }
        return when {
            afterBitmapRect.right - afterBitmapRect.left < afterCropRect.right - afterCropRect.left -> false
            afterBitmapRect.bottom - afterBitmapRect.top < afterCropRect.bottom - afterCropRect.top -> false
            afterBitmapRect.left > afterCropRect.left -> false
            afterBitmapRect.top > afterCropRect.top -> false
            afterBitmapRect.right < afterCropRect.right -> false
            afterBitmapRect.bottom < afterCropRect.bottom -> false
            else -> true
        }
    }

    fun crop(failure: ((Throwable?) -> Unit)? = null, success: (Bitmap) -> Unit) {
        if (!checkBounds()) {
            failure?.invoke(null)
            addAction(LimitAction())
            return
        }
        AsyncTask.execute {
            var cropBitmap: Bitmap? = null
            val handler = Handler(Looper.getMainLooper())
            try {
                //变换后-裁剪区域坐标
                val afterCropRect = calculateCropSpace()
                val bitmap = (drawable as BitmapDrawable).bitmap
                //变换后-图片区域坐标
                val afterBitmapRect = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat()).also {
                    imageMatrix.mapRect(it)
                }

                //变换后-裁剪区域相对于图片坐标
                val cw = afterCropRect.right - afterCropRect.left
                val ch = afterCropRect.bottom - afterCropRect.top
                val cx = afterCropRect.left - afterBitmapRect.left
                val cy = afterCropRect.top - afterBitmapRect.top

                //计算图片的缩放比例
                val scale = getCurrentValues()[Matrix.MSCALE_X] // getResetValues()[Matrix.MSCALE_X]
                //变换前-裁剪区域坐标
                val beforeCropRect = RectF(cx, cy, cx + cw, cy + ch).also {
                    Matrix().apply {
                        postScale(1 / scale, 1 / scale)
                        mapRect(it)
                    }
                }

                //变换前-裁剪区域相对于图片坐标
                val x = beforeCropRect.left.toInt()
                val y = beforeCropRect.top.toInt()
                val w = (beforeCropRect.right - beforeCropRect.left).toInt()
                val h = (beforeCropRect.bottom - beforeCropRect.top).toInt()

//                Log.e("CropImageView", "crop:  图片尺寸 ${bitmap.width} ${bitmap.height}")
//                Log.e("CropImageView", "crop:  变换后-裁剪区域坐标 afterCropRect")
//                Log.e("CropImageView", "crop:  变换后-图片区域坐标 afterBitmapRect")
//                Log.e("CropImageView", "crop:  变换后-裁剪区域相对于图片坐标 ${RectF(cx, cy, cx + cw, cy + ch)}")
//                Log.e("CropImageView", "crop:  缩放比例 $scale")
//                Log.e("CropImageView", "crop:  变换前-裁剪区域相对于图片坐标 $beforeCropRect")

                val matrix = Matrix().apply {
                    postScale(scale, scale)
                }

                cropBitmap = Bitmap.createBitmap(bitmap, x, y, w, h, matrix, false)
//                Log.e("CropImageView", "crop:  裁剪图片宽度 ${cropBitmap.width} 高度 ${cropBitmap.height}")
            } catch (e: Throwable) {
                Log.e("CropImageView", "crop:  ", e)
                handler.post {
                    failure?.invoke(e)
                }
            } finally {
                handler.post {
                    cropBitmap?.apply {
                        setImageBitmap(this)
                        success(this)
                    } ?: let { failure?.invoke(null) }
                }
            }
        }
    }
}