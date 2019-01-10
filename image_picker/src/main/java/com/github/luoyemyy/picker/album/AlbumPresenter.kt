package com.github.luoyemyy.picker.album

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.github.luoyemyy.ext.toJsonString
import com.github.luoyemyy.ext.toast
import com.github.luoyemyy.mvp.recycler.AbstractRecyclerPresenter
import com.github.luoyemyy.mvp.recycler.LoadType
import com.github.luoyemyy.mvp.recycler.Paging
import com.github.luoyemyy.picker.ImagePicker
import com.github.luoyemyy.picker.R
import com.github.luoyemyy.picker.entity.Bucket
import com.github.luoyemyy.picker.entity.Image
import kotlin.math.roundToInt

class AlbumPresenter(private val app: Application) : AbstractRecyclerPresenter<Image>(app) {

    var bucketId: Int = 0
    var mMenuText: String? = null
    var buckets: List<Bucket>? = null
    val liveDataInit = MutableLiveData<Boolean>()
    val liveDataMenu = MutableLiveData<Boolean>()

    private val mModel: AlbumModel by lazy { AlbumModel(app) }
    private var mSizePair: Pair<Int, Int>? = null

    fun setMenu() {
        val maxSelect = ImagePicker.option.maxSelect
        if (maxSelect > 1) {
            val selectCount = findSelectImages().size
            mMenuText = app.getString(R.string.image_picker_selected, selectCount, maxSelect)
            liveDataMenu.postValue(true)
        }
    }

    override fun loadInit(bundle: Bundle?) {
        if (!isInitialized()){
            reCalculateImageItemSize()
        }
        super.loadInit(bundle)
    }

    override fun loadData(loadType: LoadType, paging: Paging, bundle: Bundle?, search: String?): List<Image>? {
        if (loadType.isInit()) {
            buckets = mModel.queryImage()
            liveDataInit.postValue(true)
        }
        return buckets?.firstOrNull { it.id == bucketId }?.images?.mapIndexed { index, image ->
            image.index = index
            image
        }
    }

    fun reCalculateImageItemSize(): Pair<Int, Int> {
        val suggestSize = app.resources.displayMetrics.density * 100
        val screenWidth = app.resources.displayMetrics.widthPixels

        val span = (screenWidth / suggestSize).toInt()
        val size = screenWidth / span
        return Pair(span, size).apply {
            mSizePair = this
        }
    }

    private fun calculateImageItemSize(): Pair<Int, Int> {
        return mSizePair ?: reCalculateImageItemSize()
    }

    fun getSpan(): Int {
        return calculateImageItemSize().first
    }

    fun getSize(): Int {
        return calculateImageItemSize().second
    }

    fun getSpace(): Int {
        return (app.resources.displayMetrics.density * 1).roundToInt()
    }

    fun clickSelect(position: Int) {
        val image = getDataSet().item(position) ?: return
        val selectImages = findSelectImages()
        if (selectImages.contains(image)) {
            getAdapterSupport()?.getAdapter()?.apply {
                getDataSet().change(position, this) {
                    it.isChecked = false
                }
            }
            setMenu()
        } else {
            val maxSelect = ImagePicker.option.maxSelect
            if (selectImages.size >= maxSelect) {
                app.toast(message = app.getString(R.string.image_picker_tip2, maxSelect))
            } else {
                getAdapterSupport()?.getAdapter()?.apply {
                    getDataSet().change(position, this) {
                        it.isChecked = true
                    }
                }
                setMenu()
            }
        }
    }

    fun clickSure(): Pair<Boolean, String?> {
        val images = findSelectImages()
        if (images.isEmpty()) {
            app.toast(messageId = R.string.image_picker_tip1)
            return Pair(false, null)
        }
        val minSelect = ImagePicker.option.minSelect
        if (images.size < minSelect) {
            app.toast(message = app.getString(R.string.image_picker_tip3, minSelect))
            return Pair(false, null)
        }
        return Pair(true, images.map { it.path }.toJsonString())
    }

    private fun findSelectImages(): List<Image> {
        return buckets?.firstOrNull()?.images?.filter { it.isChecked } ?: listOf()
    }
}