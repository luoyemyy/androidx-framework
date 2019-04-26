package com.github.luoyemyy.picker.helper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
            val file = if (ImagePicker.option.cropType == 2 && ImagePicker.option.compress) compress(bitmap) else saveFile(bitmap)
            Handler(Looper.getMainLooper()).post {
                result(file != null, file?.absolutePath)
            }
        }
    }

    private fun compress(bitmap: Bitmap): File? {
        val w = bitmap.width.toFloat()
        val h = bitmap.height.toFloat()
        val dw = ImagePicker.option.compressWidth
        val dh = (dw.toFloat() * (h / w)).toInt()
        return saveFile(Bitmap.createScaledBitmap(bitmap, dw, dh, false))
    }

    private fun saveFile(bitmap: Bitmap): File? {
        return FileManager.getInstance().outer().privateCacheFile(FileManager.IMAGE, FileManager.getInstance().getName())?.apply {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(this))
        }
    }

    fun compress(path: String): File? {
        return compress(BitmapFactory.decodeFile(path))
    }

}