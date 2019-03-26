package com.github.luoyemyy.picker.helper

import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.luoyemyy.file.FileManager
import com.github.luoyemyy.picker.ImagePicker
import java.io.File
import java.io.FileOutputStream

object ImagePickerHelper {

    @JvmStatic
    @BindingAdapter("image_picker_image_url")
    fun imagePicker(imageView: ImageView, url: String?) {
        Glide.with(imageView).load(url).into(imageView)
    }

    fun imagePickerCenterInside(imageView: ImageView, url: String?) {
        Glide.with(imageView).load(url).apply(RequestOptions.centerInsideTransform()).into(imageView)
    }

    fun saveBitmap(bitmap: Bitmap, result: (Boolean, String?) -> Unit) {
        AsyncTask.execute {
            val file = compress(bitmap)
            Handler(Looper.getMainLooper()).post {
                result(file != null, file?.absolutePath)
            }
        }
    }

    private fun compress(bitmap: Bitmap): File? {
        return if (ImagePicker.option.cropType == 2 && ImagePicker.option.compress) {
            val w = bitmap.width.toFloat()
            val h = bitmap.height.toFloat()
            val dw = ImagePicker.option.compressWidth
            val dh = (dw.toFloat() * (h / w)).toInt()
            Bitmap.createScaledBitmap(bitmap, dw, dh, false)
        } else {
            bitmap
        }.let {
            FileManager.getInstance().outer().privateCacheFile(FileManager.IMAGE, FileManager.getInstance().getName())?.apply {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, FileOutputStream(this))
//                Log.e("ImagePickerHelper", "compress: ${this.length() / 1024.0f} ")
            }
        }
    }
}