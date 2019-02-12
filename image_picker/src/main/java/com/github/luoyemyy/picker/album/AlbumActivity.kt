package com.github.luoyemyy.picker.album

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Pair
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.github.luoyemyy.bus.Bus
import com.github.luoyemyy.mvp.getRecyclerPresenter
import com.github.luoyemyy.mvp.recycler.AbstractSingleRecyclerAdapter
import com.github.luoyemyy.mvp.recycler.VH
import com.github.luoyemyy.mvp.recycler.setGridManager
import com.github.luoyemyy.mvp.recycler.setLinearManager
import com.github.luoyemyy.picker.ImagePicker
import com.github.luoyemyy.picker.R
import com.github.luoyemyy.picker.crop.CropActivity
import com.github.luoyemyy.picker.databinding.ImagePickerAlbumBinding
import com.github.luoyemyy.picker.databinding.ImagePickerAlbumRecyclerBinding
import com.github.luoyemyy.picker.databinding.ImagePickerBucketRecyclerBinding
import com.github.luoyemyy.picker.entity.Bucket
import com.github.luoyemyy.picker.entity.Image
import com.github.luoyemyy.picker.preview.PreviewActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior

class AlbumActivity : AppCompatActivity() {

    private lateinit var mBinding: ImagePickerAlbumBinding
    private lateinit var mAlbumPresenter: AlbumPresenter
    private lateinit var mBucketPresenter: BucketPresenter
    private lateinit var mBottomSheet: BottomSheetBehavior<RecyclerView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.image_picker_album)

        mBucketPresenter = getRecyclerPresenter(this, BucketAdapter())
        mAlbumPresenter = getRecyclerPresenter(this, AlbumAdapter())

        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.apply {
            setTitle(R.string.image_picker_album_title)
            setDisplayHomeAsUpEnabled(true)
        }

        mAlbumPresenter.setFlagObserver(this, Observer {
            when (it) {
                1 -> mBucketPresenter.loadInit(mAlbumPresenter.buckets)
                2 -> invalidateOptionsMenu()
            }
        })
        mAlbumPresenter.setMenu()

        mBinding.recyclerView.apply {
            setGridManager(mAlbumPresenter.getSpan())
            addItemDecoration(AlbumDecoration(mAlbumPresenter.getSpan(), mAlbumPresenter.getSpace()))
        }

        mBinding.recyclerView2.apply {
            setLinearManager()
            mBottomSheet = BottomSheetBehavior.from(this)
            mBottomSheet.peekHeight = 0
            mBottomSheet.isHideable = true
            hideBucket()
        }

        mBinding.fab.setOnClickListener {
            showBucket()
        }

        mAlbumPresenter.loadInit()
    }

    private fun showBucket() {
        mBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
        mBinding.fab.hide()
    }

    private fun hideBucket() {
        mBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        mBinding.fab.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.image_picker_albun_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (mAlbumPresenter.mMenuText == null) {
            menu?.findItem(R.id.selected)?.isVisible = false
        } else {
            menu?.findItem(R.id.selected)?.apply {
                isVisible = true
                title = mAlbumPresenter.mMenuText
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finishAfterTransition()
            }
            R.id.sure -> {
                val (r, paths) = mAlbumPresenter.clickSure()
                if (r) {
                    when (ImagePicker.option.cropType) {
                        0 -> {
                            Bus.post(ImagePicker.PICKER_RESULT, stringValue = paths)
                        }
                        1, 2 -> {
                            startActivity(Intent(this, CropActivity::class.java).putExtra("images", paths))
                        }
                    }
                    finishAfterTransition()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    inner class AlbumAdapter : AbstractSingleRecyclerAdapter<Image, ImagePickerAlbumRecyclerBinding>(mBinding.recyclerView) {

        override fun enableLoadMore(): Boolean {
            return false
        }

        override fun enableEmpty(): Boolean {
            return false
        }

        override fun createContentView(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): ImagePickerAlbumRecyclerBinding {
            return ImagePickerAlbumRecyclerBinding.inflate(inflater, parent, false).apply {
                root.layoutParams.width = mAlbumPresenter.getSize()
                root.layoutParams.height = mAlbumPresenter.getSize()
            }
        }

        override fun bindContentViewHolder(binding: ImagePickerAlbumRecyclerBinding, content: Image, position: Int) {
            binding.image = content
            val shareName = "${position}_share"
            ViewCompat.setTransitionName(binding.imageView, shareName)
            binding.executePendingBindings()
        }

        override fun getItemClickViews(binding: ImagePickerAlbumRecyclerBinding): Array<View> {
            return arrayOf(binding.mask, binding.imageView)
        }

        override fun onItemClickListener(vh: VH<ImagePickerAlbumRecyclerBinding>, view: View?) {
            if (view == null) return
            if (view is ImageView) {
                val image = getItem(vh.adapterPosition) ?: return
                val imageView = vh.binding?.imageView ?: return
                val shareName = ViewCompat.getTransitionName(imageView) ?: return

                startActivity(Intent(this@AlbumActivity, PreviewActivity::class.java).apply {
                    putExtra("shareName", shareName)
                    putExtra("image", image.path)
                }, ActivityOptions.makeSceneTransitionAnimation(this@AlbumActivity, Pair(imageView as View, shareName)).toBundle())
            } else {
                mAlbumPresenter.clickSelect(vh.adapterPosition)
            }
        }

        override fun afterLoadRefresh(ok: Boolean, list: List<Image>?) {
            if (list != null && list.isNotEmpty()) {
                mBinding.recyclerView.scrollToPosition(0)
            }
        }
    }

    inner class BucketAdapter : AbstractSingleRecyclerAdapter<Bucket, ImagePickerBucketRecyclerBinding>(mBinding.recyclerView2) {

        override fun enableEmpty(): Boolean {
            return false
        }

        override fun enableLoadMore(): Boolean {
            return false
        }

        override fun createContentView(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): ImagePickerBucketRecyclerBinding {
            return ImagePickerBucketRecyclerBinding.inflate(inflater, parent, false)
        }

        override fun bindContentViewHolder(binding: ImagePickerBucketRecyclerBinding, content: Bucket, position: Int) {
            binding.bucket = content
            binding.executePendingBindings()
        }

        override fun onItemClickListener(vh: VH<ImagePickerBucketRecyclerBinding>, view: View?) {
            val item = mBucketPresenter.getDataSet().item(vh.adapterPosition) ?: return
            mAlbumPresenter.bucketId = item.id
            mAlbumPresenter.loadRefresh()
            hideBucket()
        }
    }
}