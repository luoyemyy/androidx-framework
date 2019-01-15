package com.github.luoyemyy.framework.test.status

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.github.luoyemyy.framework.test.R
import com.github.luoyemyy.framework.test.databinding.ActivityStatusBinding


class StatusActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityStatusBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_status)

//        mBinding.imageView.setImageDrawable(CircularProgressDrawable(this).apply {
//            setStyle(CircularProgressDrawable.LARGE)
//            setColorSchemeColors(ContextCompat.getColor(applicationContext, R.color.colorAccent))
//            start()
//        })

    }


}