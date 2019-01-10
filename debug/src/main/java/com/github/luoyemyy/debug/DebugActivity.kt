package com.github.luoyemyy.debug

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.github.luoyemyy.debug.databinding.DebugActivityDebugBinding
import com.github.luoyemyy.debug.databinding.DebugActivityDebugItemBinding
import com.github.luoyemyy.debug.log.LogActivity
import com.github.luoyemyy.mvp.getRecyclerPresenter
import com.github.luoyemyy.mvp.recycler.AbstractRecyclerPresenter
import com.github.luoyemyy.mvp.recycler.AbstractSingleRecyclerAdapter
import com.github.luoyemyy.mvp.recycler.VH
import com.github.luoyemyy.mvp.recycler.setLinearManager

class DebugActivity : AppCompatActivity() {
    private lateinit var mBinding: DebugActivityDebugBinding
    private lateinit var mPresenter: Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.debug_activity_debug)

        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.apply {
            setTitle(R.string.debug_title)
            setDisplayHomeAsUpEnabled(true)
        }

        mPresenter = getRecyclerPresenter(this, Adapter())

        mBinding.recyclerView.apply {
            setLinearManager()
        }

        mPresenter.loadInit()

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finishAfterTransition()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    data class Action(val id: Int, val name: String)

    inner class Adapter : AbstractSingleRecyclerAdapter<Action, DebugActivityDebugItemBinding>(mBinding.recyclerView) {

        override fun enableLoadMore(): Boolean {
            return false
        }

        override fun bindContentViewHolder(binding: DebugActivityDebugItemBinding, content: Action, position: Int) {
            binding.apply {
                name = content.name
                executePendingBindings()
            }
        }

        override fun onItemClickListener(vh: VH<DebugActivityDebugItemBinding>, view: View?) {
            val action = getItem(vh.adapterPosition) ?: return
            when (action.id) {
                1 -> startActivity(Intent(applicationContext, LogActivity::class.java).apply { putExtra("name", action.name) })
                2 -> startActivity(Intent(applicationContext, LogActivity::class.java).apply { putExtra("name", action.name) })
                3 -> startActivity(Intent(applicationContext, LogActivity::class.java).apply { putExtra("name", action.name) })
            }
        }

        override fun createContentView(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): DebugActivityDebugItemBinding {
            return DebugActivityDebugItemBinding.inflate(inflater, parent, false)
        }


    }

    class Presenter(app: Application) : AbstractRecyclerPresenter<Action>(app) {
//        override fun loadData(loadType: LoadType, paging: Paging, bundle: Bundle?, search: String?): List<Action>? {
//            return listOf(Action(1, "运行日志"),
//                    Action(2, "异常日志"),
//                    Action(3, "Profile"))
//        }
    }
}