package com.github.luoyemyy.framework.test.status

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.github.luoyemyy.ext.immerse
import com.github.luoyemyy.framework.test.R
import com.github.luoyemyy.framework.test.databinding.ActivityStatusBinding


class StatusActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityStatusBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_status)

    }


}