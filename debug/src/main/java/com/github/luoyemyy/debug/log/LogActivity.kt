package com.github.luoyemyy.debug.log

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.github.luoyemyy.debug.R
import com.github.luoyemyy.debug.databinding.DebugActivityDebugBinding
import com.github.luoyemyy.debug.databinding.DebugActivityDebugItemBinding
import com.github.luoyemyy.logger.Logger
import com.github.luoyemyy.mvp.getRecyclerPresenter
import com.github.luoyemyy.mvp.recycler.*
import java.io.File

class LogActivity : AppCompatActivity() {

    private lateinit var mBinding: DebugActivityDebugBinding
    private lateinit var mPresenter: Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.debug_activity_debug)

        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.apply {
            title = intent.getStringExtra("name")
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

    data class Log(val name: String, val path: String)

    inner class Adapter : AbstractSingleRecyclerAdapter<Log, DebugActivityDebugItemBinding>(mBinding.recyclerView) {

        override fun bindContentViewHolder(binding: DebugActivityDebugItemBinding, content: Log, position: Int) {
            binding.apply {
                name = content.name
                executePendingBindings()
            }
        }

        override fun onItemClickListener(vh: VH<DebugActivityDebugItemBinding>, view: View?) {
            val log = getItem(vh.adapterPosition) ?: return
            openLog(log.path)
        }

        override fun createContentView(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): DebugActivityDebugItemBinding {
            return DebugActivityDebugItemBinding.inflate(inflater, parent, false)
        }

        override fun enableLoadMore(): Boolean {
            return false
        }
    }

    private fun openLog(path: String) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setDataAndType(Uri.parse(path), "text/*")
        startActivity(intent)
    }

    class Presenter(app: Application) : AbstractRecyclerPresenter<Log>(app) {
        override fun loadData(loadType: LoadType, paging: Paging, bundle: Bundle?, search: String?): List<Log>? {
            val path = Logger.logPath ?: return null
            return File(path).listFiles().map {
                Log(it.name, it.absolutePath)
            }
        }
    }
}