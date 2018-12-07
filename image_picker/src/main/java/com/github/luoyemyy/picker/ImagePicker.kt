package com.github.luoyemyy.picker

import android.Manifest
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.github.luoyemyy.bus.Bus
import com.github.luoyemyy.bus.BusMsg
import com.github.luoyemyy.bus.BusResult
import com.github.luoyemyy.ext.toList
import com.github.luoyemyy.permission.PermissionHelper
import com.github.luoyemyy.picker.album.AlbumActivity
import com.github.luoyemyy.picker.capture.CaptureFragment

class ImagePicker private constructor() {

    companion object {

        internal lateinit var option: PickerOption
        internal const val PICKER_RESULT = "com.github.luoyemyy.picker.ImagePicker.PICKER_RESULT"
        internal const val ALBUM_RESULT = "com.github.luoyemyy.picker.ImagePicker.ALBUM_RESULT"
        internal const val CROP_RESULT = "com.github.luoyemyy.picker.ImagePicker.CROP_RESULT"

        /**
         * @param fileProvider external-path#Pictures
         */
        fun create(fileProvider: String): Builder {
            return Builder(fileProvider)
        }
    }

    class Builder internal constructor(fileProvider: String) {

        private val mOption = PickerOption(fileProvider)

        /**
         * 相册和相机
         */
        fun albumAndCamera(): Builder {
            mOption.pickerType = 0
            return this
        }

        /**
         * 相册
         */
        fun album(): Builder {
            mOption.pickerType = 1
            return this
        }

        /**
         * 相机
         */
        fun camera(): Builder {
            mOption.pickerType = 2
            return this
        }

        /**
         * @param  0 < minSelect <= maxSelect （默认1）
         */
        fun minSelect(minSelect: Int): Builder {
            mOption.minSelect = minSelect
            return this
        }

        /**
         * @param maxSelect minSelect <= maxSelect  （默认1）
         */
        fun maxSelect(maxSelect: Int): Builder {
            mOption.maxSelect = maxSelect
            return this
        }

        /**
         * @param portrait true 竖屏（默认) ；false 横屏
         */
        fun portrait(portrait: Boolean): Builder {
            mOption.portrait = portrait
            return this
        }

        /**
         * 按照固定尺寸计算裁剪区域，不超过imageView的大小
         * @param size cropSize > 0
         * @param ratio  cropRatio > 0
         */
        fun cropBySize(size: Int, ratio: Float = 1f, require: Boolean = true): Builder {
            if (size <= 0) throw IllegalArgumentException("cropSize: cropSize > 0")
            if (ratio <= 0) throw IllegalArgumentException("cropRatio: cropRatio > 0")
            mOption.cropSize = size
            mOption.cropRatio = ratio
            mOption.cropRequire = require
            mOption.cropType = 1
            return this
        }

        /**
         * 按照imageView的最小边的百分比计算裁剪区域
         * @param percent 0 < cropPercent <= 1
         * @param ratio  cropRatio > 0
         */
        fun cropByPercent(percent: Float = 0.6f, ratio: Float = 1f, require: Boolean = true): Builder {
            if (percent <= 0 || percent > 1) throw IllegalArgumentException("cropPercent: 0 < cropPercent <= 1")
            if (ratio <= 0) throw IllegalArgumentException("cropRatio: cropRatio > 0")
            mOption.cropPercent = percent
            mOption.cropRatio = ratio
            mOption.cropRequire = require
            mOption.cropType = 2
            return this
        }

        fun build(): ImagePicker {
            if (mOption.maxSelect <= 0) {
                mOption.maxSelect = 1
            }
            if (mOption.minSelect <= 0) {
                mOption.minSelect = 1
            } else if (mOption.minSelect > mOption.maxSelect) {
                mOption.minSelect = mOption.maxSelect
            }
            ImagePicker.option = mOption
            return ImagePicker()
        }
    }

    fun picker(fragment: Fragment, callback: (List<String>?) -> Unit) {
        picker(fragment.requireActivity(), callback)
    }

    fun picker(activity: FragmentActivity, callback: (List<String>?) -> Unit) {
        when (option.pickerType) {
            0 -> {
                PermissionHelper.withPass {
                    toAlbumOrCapture(activity, callback)
                }.withDenied { future, _ ->
                    future.toSettings(activity, activity.getString(R.string.image_picker_need_camera_storage))
                    Log.e("ImagePicker", "权限不足，需要同时拥有相机和文件读写的权限")
                }.request(activity, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            }
            1 -> {
                PermissionHelper.withPass {
                    toAlbum(activity, callback)
                }.withDenied { future, _ ->
                    future.toSettings(activity, activity.getString(R.string.image_picker_need_storage))
                    Log.e("ImagePicker", "权限不足，需要拥有文件读写的权限")
                }.request(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            }
            2 -> {
                PermissionHelper.withPass {
                    toCapture(activity, callback)
                }.withDenied { future, _ ->
                    future.toSettings(activity, activity.getString(R.string.image_picker_need_camera_storage))
                    Log.e("ImagePicker", "权限不足，需要同时拥有相机和文件读写的权限")
                }.request(activity, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            }
        }
    }

    private fun toAlbumOrCapture(activity: FragmentActivity, callback: (List<String>?) -> Unit) {
        AlertDialog.Builder(activity).setItems(R.array.image_picker_type) { _, which ->
            if (which == 0) {
                toAlbum(activity, callback)
            } else if (which == 1) {
                toCapture(activity, callback)
            }
        }.show()
    }

    private fun toAlbum(activity: FragmentActivity, callback: (List<String>?) -> Unit) {
        Bus.setCallback(activity.lifecycle, PickerInternal(callback), PICKER_RESULT)
        activity.startActivity(Intent(activity, AlbumActivity::class.java))
    }

    private fun toCapture(activity: FragmentActivity, callback: (List<String>?) -> Unit) {
        Bus.setCallback(activity.lifecycle, PickerInternal(callback), PICKER_RESULT)
        CaptureFragment.start(activity.supportFragmentManager)
    }

    inner class PickerInternal(private var mCallback: (List<String>?) -> Unit) : BusResult {
        override fun busResult(event: String, msg: BusMsg) {
            mCallback.invoke(msg.stringValue?.toList())
        }
    }
}