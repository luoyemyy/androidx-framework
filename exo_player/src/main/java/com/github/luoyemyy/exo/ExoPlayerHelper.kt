package com.github.luoyemyy.exo

import android.content.Context
import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util


class ExoPlayerHelper private constructor() {

    companion object {

//        const val NETWORK_EVENT = ""

        fun getInstance(): ExoPlayerHelper {
            return ExoPlayerHelper()
        }
    }

    private var mPlayer: SimpleExoPlayer? = null
    private var mFullscreenDialog: FullscreenPlayerFragment? = null
    private var mPlayerView: PlayerView? = null

    fun enterFullscreen(context: Context) {
        val fm = when (context) {
            is FragmentActivity -> {
                context.supportFragmentManager
            }
            is Fragment -> {
                context.fragmentManager
            }
            else -> return
        }
        mFullscreenDialog = FullscreenPlayerFragment().apply { show(fm, "fullscreenPlayer") }
    }

    fun exitFullScreen(dismissDialog: Boolean = true) {
        if (dismissDialog) {
            mFullscreenDialog?.dismiss()
        }
        mPlayerView?.player = mPlayer
    }

    fun playCurrent(playerView: PlayerView) {
        mPlayer?.apply {
            PlayerView.switchTargetView(this, mPlayerView, playerView)
        }
    }

    fun play(url: String, playerView: PlayerView) {
        val context = playerView.context
        val source = ExtractorMediaSource.Factory(DefaultDataSourceFactory(context, Util.getUserAgent(context, context.packageName))).createMediaSource(Uri.parse(url))
        mPlayer = ExoPlayerFactory.newSimpleInstance(context).apply { prepare(source) }
        mPlayerView = playerView
    }

//    val networkReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//
//        }
//    }
//
//    fun s(context: Context) {
//        context.registerReceiver(networkReceiver, IntentFilter().apply {
//            addAction(ConnectivityManager.CONNECTIVITY_ACTION)
//        })
//    }
//
//    fun hasWifi() {
//
//    }

}