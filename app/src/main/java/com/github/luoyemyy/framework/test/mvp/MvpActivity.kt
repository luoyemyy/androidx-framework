package com.github.luoyemyy.framework.test.mvp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.github.luoyemyy.bus.BusManager
import com.github.luoyemyy.bus.BusMsg
import com.github.luoyemyy.bus.BusResult
import com.github.luoyemyy.ext.toast
import com.github.luoyemyy.framework.test.R
import com.github.luoyemyy.framework.test.databinding.ActivityMvpBinding
import com.github.luoyemyy.mvp.getPresenter

class MvpActivity : AppCompatActivity(), BusResult {

    private lateinit var mBinding: ActivityMvpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_mvp)

        val presenter = getPresenter<MvpPresenter>()

        mBinding.setLifecycleOwner(this)
        mBinding.presenter = presenter

        mBinding.presenter?.load()

        BusManager.setCallback(lifecycle, this, EVENT_BUS)

    }

    override fun busResult(event: String, msg: BusMsg) {
        toast(message = event)
    }

    companion object {
        const val EVENT_BUS = "com.github.luoyemyy.framework.test.mvp.MvpActivity"
    }
}