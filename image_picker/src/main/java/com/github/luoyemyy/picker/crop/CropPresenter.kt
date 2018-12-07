package com.github.luoyemyy.picker.crop

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.github.luoyemyy.bus.Bus
import com.github.luoyemyy.ext.toJsonString
import com.github.luoyemyy.ext.toList
import com.github.luoyemyy.ext.toast
import com.github.luoyemyy.mvp.recycler.AbstractRecyclerPresenter
import com.github.luoyemyy.mvp.recycler.LoadType
import com.github.luoyemyy.mvp.recycler.Paging
import com.github.luoyemyy.picker.ImagePicker
import com.github.luoyemyy.picker.R
import com.github.luoyemyy.picker.entity.CropImage

class CropPresenter(var app: Application) : AbstractRecyclerPresenter<CropImage>(app) {

    val liveDataCropImage = MutableLiveData<CropImage>()
    val liveDataSingleImage = MutableLiveData<Boolean>()
    private var mImages = mutableListOf<CropImage>()

    fun startCrop(bundle: Bundle?) {
        bundle?.getString("images")?.toList<String>()?.forEachIndexed { index, it ->
            mImages.add(CropImage(it, it, index, false))
        }
        if (mImages.size == 1) {
            liveDataSingleImage.postValue(true)
            liveDataCropImage.postValue(mImages[0])
        } else {
            loadInit()
            if (mImages.size > 0) {
                liveDataCropImage.postValue(mImages[0])
            }
        }
    }

    override fun loadData(loadType: LoadType, paging: Paging, bundle: Bundle?, search: String?): List<CropImage>? {
        return mImages
    }

    fun resetCrop() {
        liveDataCropImage.value?.apply {
            cropPath = srcPath
            isCrop = false
            getAdapterSupport()?.apply {
                getDataSet().change(index, getAdapter())
            }
            liveDataCropImage.postValue(this)
        }
    }

    fun crop(filePath: String) {
        liveDataCropImage.value?.apply {
            cropPath = filePath
            isCrop = true
            getAdapterSupport()?.apply {
                getDataSet().change(index, getAdapter())
            }
            if (mImages.size > index + 1) {
                liveDataCropImage.postValue(mImages[index + 1])
            }
        }
    }

    fun cropResult(): Boolean {
        mImages.firstOrNull { !it.isCrop && ImagePicker.option.cropRequire }?.apply {
            clickImage(index)
            app.toast(R.string.image_picker_crop_tip)
            return false
        }
        Bus.post(ImagePicker.PICKER_RESULT, stringValue = mImages.map { it.cropPath }.toJsonString())
        return true
    }

    fun clickImage(position: Int) {

        val oldPosition = liveDataCropImage.value?.index ?: -1
        if (position != oldPosition) {
            getAdapterSupport()?.getAdapter()?.apply {
                getDataSet().change(oldPosition, this) {
                    it.selected = false
                }
                getDataSet().change(position, this) {
                    it.selected = true
                    liveDataCropImage.postValue(it)
                }
            }
        }
    }
}