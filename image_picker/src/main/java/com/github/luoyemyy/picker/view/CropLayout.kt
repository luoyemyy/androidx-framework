package com.github.luoyemyy.picker.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.SeekBar
import com.github.luoyemyy.ext.show
import com.github.luoyemyy.picker.databinding.ImagePickerCropLayoutBinding

class CropLayout(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : FrameLayout(context, attributeSet, defStyleAttr, defStyleRes), SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : this(context, attributeSet, defStyleAttr, 0)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0, 0)
    constructor(context: Context) : this(context, null, 0, 0)

    private var mBinding: ImagePickerCropLayoutBinding = ImagePickerCropLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        mBinding.seekBarCropHeight.setOnSeekBarChangeListener(this)
        mBinding.seekBarCropWidth.setOnSeekBarChangeListener(this)
        mBinding.seekBarRotate.setOnSeekBarChangeListener(this)
        mBinding.seekBarScale.setOnSeekBarChangeListener(this)
        mBinding.seekBarTranslationHorizontal.setOnSeekBarChangeListener(this)
        mBinding.seekBarTranslationVertical.setOnSeekBarChangeListener(this)

        mBinding.imgBack.setOnClickListener(this)
        mBinding.imgCrop.setOnClickListener(this)
        mBinding.imgScale.setOnClickListener(this)
        mBinding.imgRotate.setOnClickListener(this)
        mBinding.imgReset.setOnClickListener(this)
        mBinding.imgTranslation.setOnClickListener(this)
        mBinding.imgSubmit.setOnClickListener(this)

        mBinding.txtCropCustom.setOnClickListener(this)
        mBinding.txtCropOrigin.setOnClickListener(this)
        mBinding.txtCrop11.setOnClickListener(this)
        mBinding.txtCrop34.setOnClickListener(this)
        mBinding.txtCrop43.setOnClickListener(this)
        mBinding.txtCrop916.setOnClickListener(this)
        mBinding.txtCrop169.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            mBinding.imgBack -> {
            }
            mBinding.imgCrop -> {
                mBinding.layoutCrop.show()
            }
            mBinding.imgScale -> {
                mBinding.layoutScale.show()
            }
            mBinding.imgRotate -> {
                mBinding.layoutRotate.show()
            }
            mBinding.imgTranslation -> {
                mBinding.layoutTranslation.show()
            }
            mBinding.imgReset -> {
            }
            mBinding.imgSubmit -> {
            }
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            when (seekBar) {
                mBinding.seekBarCropHeight -> {
                }
                mBinding.seekBarCropWidth -> {
                }
                mBinding.seekBarRotate -> {
                }
                mBinding.seekBarScale -> {
                }
                mBinding.seekBarTranslationHorizontal -> {
                }
                mBinding.seekBarTranslationVertical -> {
                }
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }
}