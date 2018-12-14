package com.github.luoyemyy.picker.preview

import android.os.Bundle
import android.os.Handler
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import com.github.luoyemyy.mvp.getPresenter
import com.github.luoyemyy.picker.R
import com.github.luoyemyy.picker.databinding.ImagePickerPreviewBinding
import com.github.luoyemyy.picker.helper.ImagePickerHelper
import com.github.luoyemyy.picker.view.ImageViewListener

class PreviewActivity : AppCompatActivity() {

    private lateinit var mBinding: ImagePickerPreviewBinding
    private lateinit var mPresenter: PreviewPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.image_picker_preview)
        mPresenter = getPresenter()

        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.apply {
            setTitle(R.string.image_picker_preview_title)
            setDisplayHomeAsUpEnabled(true)
        }

        mBinding.imgPreview.apply {
            val shareName = intent.extras?.getString("shareName")
            val path = intent.extras?.getString("image")
            ViewCompat.setTransitionName(this, shareName)
            ImagePickerHelper.imagePicker(this, path)


            addImageViewListener(object : ImageViewListener {
                override fun onChange() {
                    fullScreen(true)
                }

                override fun onSingleTap() {
                    fullScreen(!mPresenter.fullScreen)
                }
            })
        }
        Handler().postDelayed({
            fullScreen(false)
        }, 300)
    }

    private fun fullScreen(fullScreen: Boolean) {
        val visible = if (fullScreen) View.GONE else View.VISIBLE
        TransitionManager.beginDelayedTransition(mBinding.layoutContainer, Slide(Gravity.TOP).addTarget(mBinding.appBarLayout))
        mBinding.appBarLayout.visibility = visible
        mPresenter.fullScreen = fullScreen
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finishAfterTransition()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}