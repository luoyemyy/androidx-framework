package com.github.luoyemyy.framework.test.picker

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.github.luoyemyy.ext.alert
import com.github.luoyemyy.ext.dp2px
import com.github.luoyemyy.ext.toJsonString
import com.github.luoyemyy.framework.test.R
import com.github.luoyemyy.framework.test.databinding.ActivityMainBinding
import com.github.luoyemyy.framework.test.databinding.ActivityMainRecyclerBinding
import com.github.luoyemyy.mvp.getPresenter
import com.github.luoyemyy.mvp.recycler.*
import com.github.luoyemyy.picker.ImagePicker

class PickerImageActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mPresenter: Presenter
    private val thisActivity = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mPresenter = getPresenter()
        mPresenter.setup(this, Adapter())

        mBinding.recyclerView.setLinearManager()

        mPresenter.loadInit()
    }

    inner class Adapter : AbstractSingleRecyclerAdapter<Action, ActivityMainRecyclerBinding>(mBinding.recyclerView) {


        override fun createContentView(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): ActivityMainRecyclerBinding {
            return ActivityMainRecyclerBinding.inflate(inflater, parent, false)
        }

        override fun bindContentViewHolder(binding: ActivityMainRecyclerBinding, content: Action, position: Int) {
            binding.apply {
                name = content.name
                executePendingBindings()
            }
        }

        override fun enableLoadMore(): Boolean {
            return false
        }

        override fun onItemClickListener(vh: VH<ActivityMainRecyclerBinding>, view: View?) {
            when (getItem(vh.adapterPosition)?.id) {
                1 -> ImagePicker.create(packageName).album().maxSelect(9).build().picker(thisActivity, callback)
                2 -> ImagePicker.create(packageName).album().maxSelect(9).cropByPercent().build().picker(thisActivity, callback)
                3 -> ImagePicker.create(packageName).camera().build().picker(thisActivity, callback)
                4 -> ImagePicker.create(packageName).camera().cropBySize(dp2px(300)).build().picker(thisActivity, callback)
                5 -> ImagePicker.create(packageName).albumAndCamera().build().picker(thisActivity, callback)
                6 -> ImagePicker.create(packageName).albumAndCamera().cropByPercent().build().picker(thisActivity, callback)
            }
        }

        private val callback: (List<String>?) -> Unit = {
            alert(message = it?.toJsonString() ?: "")
        }

    }

    data class Action(val id: Int, val name: String)

    class Presenter(app: Application) : AbstractRecyclerPresenter<Action>(app) {


        override fun loadData(loadType: LoadType, paging: Paging, bundle: Bundle?, search: String?): List<Action>? {
            return listOf(
                    Action(1, "album"),
                    Action(2, "album cropByPercent"),
                    Action(3, "camera"),
                    Action(4, "camera cropBySize"),
                    Action(5, "album camera"),
                    Action(6, "album camera cropByPercent")

            )
        }
    }

}