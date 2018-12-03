package com.github.luoyemyy.framework.test.exoplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.github.luoyemyy.framework.test.R
import com.github.luoyemyy.framework.test.databinding.ActivityExoplayerBinding

class ExoPlayerActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityExoplayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_exoplayer)
        val url = "http://jzvd.nathen.cn/c494b340ff704015bb6682ffde3cd302/64929c369124497593205a4190d7d128-5287d2089db37e62345123a1be272f8b.mp4"
        ExoPlayerControl(this).prepareAndPlay(mBinding.playView, url)
    }
}