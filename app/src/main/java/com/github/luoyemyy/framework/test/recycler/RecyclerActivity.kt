package com.github.luoyemyy.framework.test.recycler

import android.app.Application
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.github.luoyemyy.bus.Bus
import com.github.luoyemyy.bus.BusMsg
import com.github.luoyemyy.bus.BusResult
import com.github.luoyemyy.ext.toast
import com.github.luoyemyy.framework.test.MainActivity
import com.github.luoyemyy.framework.test.R
import com.github.luoyemyy.framework.test.databinding.ActivityRecyclerBinding
import com.github.luoyemyy.framework.test.databinding.ActivityRecyclerRecyclerBinding
import com.github.luoyemyy.framework.test.databinding.ActivityRecyclerStickBinding
import com.github.luoyemyy.mvp.getPresenter
import com.github.luoyemyy.mvp.recycler.*

class RecyclerActivity : AppCompatActivity(), BusResult {

    private lateinit var mBinding: ActivityRecyclerBinding
    private lateinit var mPresenter: Presenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_recycler)
        mPresenter = getPresenter()
        mPresenter.setup(this, Adapter())

        mBinding.recyclerView.setLinearManager()
        mBinding.swipeRefreshLayout.wrap(mPresenter)

        Bus.addCallback(lifecycle, this, BUS_EVENT)

        StickHelper.attachToRecyclerView(mBinding.recyclerView, mBinding.stick.root, object : StickHelper.Callback {
            override fun isStick(position: Int): Boolean {
                return (mPresenter.getDataSet().item(position) as? Item)?.type == 1
            }

            override fun setStickViewContent(position: Int) {
                val item = mPresenter.getDataSet().item(position) as? Item ?: return
                Log.e("RecyclerActivity", "setStickViewContent:  ${item.type},${item.name}")
                Log.e("RecyclerActivity", "visible: ${mBinding.stick.root.visibility == View.VISIBLE}")
                Log.e("RecyclerActivity", "height: ${mBinding.stick.root.height}")
                Log.e("RecyclerActivity", "width: ${mBinding.stick.root.width}")
                mBinding.stick.name = "${item.type},${item.name}"
            }
        })
        mBinding.recyclerView.addItemDecoration(RecyclerDecoration.middle(this, 1, true).drawDivider())

        mPresenter.loadInit()
    }

    override fun busResult(event: String, msg: BusMsg) {
        toast(message = event)
    }

    companion object {
        const val BUS_EVENT = "com.github.luoyemyy.framework.test.recycler.RecyclerActivity"
    }

    inner class Adapter : AbstractMultiRecyclerAdapter(mBinding.recyclerView) {

        override fun createContentView(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): ViewDataBinding {
            return when (viewType) {
                2 -> ActivityRecyclerRecyclerBinding.inflate(inflater, parent, false)
                else -> ActivityRecyclerStickBinding.inflate(inflater, parent, false)
            }
        }

        override fun getContentType(position: Int, item: Any?): Int {
            return (item as? Item)?.type ?: 0
        }

        override fun bindContentViewHolder(binding: ViewDataBinding, content: Any, position: Int) {
            if (content is Item) {
                if (binding is ActivityRecyclerRecyclerBinding) {
                    binding.name = "${content.type},${content.name}"
                    binding.executePendingBindings()
                } else if (binding is ActivityRecyclerStickBinding) {
                    binding.name = "${content.type},${content.name}"
                    binding.executePendingBindings()
                }
            }
        }

        override fun setRefreshState(refreshing: Boolean) {
            mBinding.swipeRefreshLayout.isRefreshing = refreshing
        }

        override fun onItemClickListener(vh: VH<ViewDataBinding>, view: View?) {
            TestDialogFragment().show(supportFragmentManager, "1111")
        }
    }

    data class Item(val type: Int, var name: String)

    class Presenter(app: Application) : AbstractRecyclerPresenter<Any>(app) {

        override fun beforeLoadRefresh() {
            super.beforeLoadRefresh()
            Bus.post(BUS_EVENT)
            Bus.post(MainActivity.BUS_EVENT)
        }

        override fun loadData(loadType: LoadType, paging: Paging, bundle: Bundle?, search: String?): List<Any>? {
            return if (paging.current() < 10) {
                (0..9).map { Item(if (it % 5 == 0) 1 else 2, ((paging.current() - 1) * 10 + it).toString()) }
            } else {
                null
            }
        }
    }

}
