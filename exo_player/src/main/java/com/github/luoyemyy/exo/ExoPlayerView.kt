package com.github.luoyemyy.exo

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import com.github.luoyemyy.bus.BusMsg
import com.github.luoyemyy.bus.BusResult
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class ExoPlayerView constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : PlayerView(context, attributeSet, defStyleAttr), BusResult {

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context) : this(context, null, 0)

    private var mControlView: ExoPlayerControlView = findViewById(R.id.exo_controller)
    private var mSource: ExtractorMediaSource? = null
    private var mPlayer: SimpleExoPlayer? = null

    init {
        showController()
    }

    fun setup(url: String?) {
        if (url == null) return
        mSource = ExtractorMediaSource.Factory(DefaultDataSourceFactory(context, Util.getUserAgent(context, context.packageName))).createMediaSource(Uri.parse(url))
        mPlayer = mPlayer ?: ExoPlayerFactory.newSimpleInstance(context).also {
            player = it
        }
    }

    fun play() {
        mPlayer?.prepare(mSource)
        mPlayer?.playWhenReady = true
    }

    override fun busResult(event: String, msg: BusMsg) {
        Utils.networkType(context)
    }
}