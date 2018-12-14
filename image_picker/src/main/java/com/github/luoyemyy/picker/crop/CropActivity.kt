package com.github.luoyemyy.picker.crop

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.luoyemyy.mvp.getRecyclerPresenter
import com.github.luoyemyy.mvp.recycler.AbstractSingleRecyclerAdapter
import com.github.luoyemyy.mvp.recycler.VH
import com.github.luoyemyy.picker.R
import com.github.luoyemyy.picker.databinding.ImagePickerCropBinding
import com.github.luoyemyy.picker.databinding.ImagePickerCropRecyclerBinding
import com.github.luoyemyy.picker.entity.CropImage
import com.github.luoyemyy.picker.helper.ImagePickerHelper
import kotlin.math.roundToInt

class CropActivity : AppCompatActivity() {

    private lateinit var mBinding: ImagePickerCropBinding
    private lateinit var mPresenter: CropPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.image_picker_crop)
        mPresenter = getRecyclerPresenter(this, Adapter())

        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.apply {
            setTitle(R.string.image_picker_crop_title)
            setDisplayHomeAsUpEnabled(true)
        }

        mPresenter.liveDataCropImage.observe(this, Observer {
            if (it != null) {
                ImagePickerHelper.imagePicker(mBinding.imgPreview, it.cropPath)
            }
        })
        mPresenter.liveDataSingleImage.observe(this, Observer {
            mBinding.recyclerView.visibility = View.GONE
        })

        mBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(Decoration(context))
        }

        mPresenter.startCrop(savedInstanceState != null, intent.extras)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.image_picker_crop_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finishAfterTransition()
            }
            R.id.reset -> {
                mPresenter.resetCrop()
            }
            R.id.crop -> {
                mBinding.imgPreview.crop {
                    ImagePickerHelper.saveBitmap(it) { ok, path ->
                        if (ok && path != null) {
                            mPresenter.crop(path)
                        }
                    }
                }
            }
            R.id.sure -> {
                if (mPresenter.cropResult()) {
                    finishAfterTransition()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    inner class Adapter : AbstractSingleRecyclerAdapter<CropImage, ImagePickerCropRecyclerBinding>(mBinding.recyclerView) {

        override fun enableLoadMore(): Boolean {
            return false
        }

        override fun enableEmpty(): Boolean {
            return false
        }

        override fun createContentView(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): ImagePickerCropRecyclerBinding {
            return ImagePickerCropRecyclerBinding.inflate(inflater, parent, false)
        }

        override fun bindContentViewHolder(binding: ImagePickerCropRecyclerBinding, content: CropImage, position: Int) {
            binding.image = content
            binding.executePendingBindings()
        }

        override fun onItemClickListener(vh: VH<ImagePickerCropRecyclerBinding>, view: View?) {
            mPresenter.clickImage(vh.adapterPosition)
        }
    }

    class Decoration(context: Context) : RecyclerView.ItemDecoration() {
        private val space = (context.resources.displayMetrics.density * 2).roundToInt()
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.top = space
            outRect.bottom = space
            outRect.right = space
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.left = space
            }
        }
    }
}