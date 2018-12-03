package com.github.luoyemyy.picker.album

import android.app.Application
import android.provider.MediaStore
import com.github.luoyemyy.mvp.BaseModel
import com.github.luoyemyy.picker.R
import com.github.luoyemyy.picker.entity.Bucket
import com.github.luoyemyy.picker.entity.Image

class AlbumModel(val app: Application) : BaseModel() {

    fun queryImage(): List<Bucket> {

        val data = MediaStore.Images.Media.query(app.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.MediaColumns.DATA, MediaStore.Images.ImageColumns.BUCKET_ID, MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, MediaStore.MediaColumns.SIZE),
                "${MediaStore.Files.FileColumns.MIME_TYPE} like '%image/jpeg%' or ${MediaStore.Files.FileColumns.MIME_TYPE} like '%image/jpg%'",
                "${MediaStore.Images.Media.DATE_ADDED} DESC")

        val buckets = mutableListOf<Bucket>()
        val bucketMap = mutableMapOf<String, Bucket>()

        val bucketAll = Bucket(app.getString(R.string.image_picker_all_image))
        buckets.add(bucketAll)

        if (data != null) {
            while (data.moveToNext()) {
                if (data.getInt(data.getColumnIndex(MediaStore.Images.Media.SIZE)) <= 0) {
                    continue
                }
                val bucketId = data.getString(data.getColumnIndex(MediaStore.Images.Media.BUCKET_ID))
                val bucketName = data.getString(data.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                val path = data.getString(data.getColumnIndex(MediaStore.Images.Media.DATA))
                val image = Image(path)
                if (bucketId != null && bucketName != null) {
                    val bucket = bucketMap[bucketId]
                            ?: Bucket(bucketName).apply {
                                bucketMap[bucketId] = this
                                buckets.add(this)
                            }
                    bucket.images.add(image)
                }
                bucketAll.images.add(image)
            }
        }
        data?.close()

        buckets.forEachIndexed { index, bucket ->
            bucket.id = index
        }
        return buckets
    }
}