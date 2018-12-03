package com.github.luoyemyy.framework.test.exoplayer

import android.app.AlertDialog
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

@Suppress("DEPRECATION")
class ExoPlayerControl(val context: Context) : Player.EventListener, ControlDispatcher {

    init {
        if (context is Fragment || context is FragmentActivity) {

        } else {
            throw IllegalArgumentException("context 必须是 Fragment 或 FragmentActivity")
        }
    }

    private var appContext: Context = context.applicationContext
    private var mCurrentPlayer: SimpleExoPlayer? = null
    private var mCurrentPlayerView: PlayerView? = null
    private var mExoLoadControl: DefaultLoadControl? = null
    private var mHasError: Boolean = false
    private var mIsLoading: Boolean = false
    private var mPlaying: Boolean = false
    private var mUserCellular: Boolean = false

    private var networkReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (!mUserCellular && networkType() != 1) {
                mExoLoadControl?.onStopped()
                stop()
            }
        }
    }

    fun prepareAndPlay(playerView: PlayerView, url: String) {
        context.registerReceiver(networkReceiver, IntentFilter().apply { addAction(ConnectivityManager.CONNECTIVITY_ACTION) })
        mCurrentPlayerView = playerView
        mCurrentPlayer = newPlayer().also {
            it.addListener(this)
            playerView.setControlDispatcher(this)
            playerView.player = it
            it.prepare(newMediaSource(url))
        }
    }

    fun play() {
        mCurrentPlayer?.playWhenReady = true
    }

    fun stop() {
        mCurrentPlayer?.stop()
    }

    fun release() {
        context.unregisterReceiver(networkReceiver)
        mCurrentPlayerView?.apply {
            player = null

        }
        mCurrentPlayer?.apply {
            stop()
            release()
        }
        mCurrentPlayer = null
        mCurrentPlayerView = null
    }

    /**
     * @return 0 none 1 wifi 2 else
     */
    fun networkType(): Int {
        val cm = (appContext.getSystemService(Service.CONNECTIVITY_SERVICE) as? ConnectivityManager)
                ?: return 0
        val networkInfo = cm.activeNetworkInfo
        return if (networkInfo == null) 0 else if (networkInfo.type == ConnectivityManager.TYPE_WIFI) 1 else 2
    }

    private fun newPlayer(): SimpleExoPlayer {
        return ExoPlayerFactory.newSimpleInstance(null, DefaultRenderersFactory(appContext), DefaultTrackSelector(), DefaultLoadControl().apply {
            mExoLoadControl = this
        })
    }

    private fun newDataSourceFactory(): DataSource.Factory {
        return DefaultDataSourceFactory(appContext, Util.getUserAgent(appContext, appContext.packageName))
    }

    private fun newMediaSource(url: String): MediaSource {
        return ExtractorMediaSource.Factory(newDataSourceFactory()).createMediaSource(Uri.parse(url))
    }


    // ControlDispatcher
    override fun dispatchSetPlayWhenReady(player: Player, playWhenReady: Boolean): Boolean {
        var dispatch = true
        var retry = false
        if (playWhenReady) {
            when (networkType()) {
                0 -> {
                    AlertDialog.Builder(context).setMessage("无法连接到网络，请稍后重试").setPositiveButton("确认", null).show()
                    dispatch = false
                }
                1 -> {
                    dispatch = true
                    retry = true
                }
                2 -> {
                    if (mUserCellular) {
                        dispatch = true
                        retry = true
                    } else {
                        AlertDialog.Builder(context).setMessage("当前网络不是wifi，继续播放会消耗流量？")
                                .setPositiveButton("取消") { _, _ ->
                                    dispatch = false
                                }
                                .setNegativeButton("继续播放") { _, _ ->
                                    retry = true
                                    dispatch = true
                                    mUserCellular = true
                                }.show()
                    }
                }
            }
        }
        if (retry) {
            //
            val currentPlayer = mCurrentPlayer ?: return false
            if (!currentPlayer.isLoading || mHasError) {
                mHasError = false
                currentPlayer.retry()
            }
        }
        if (dispatch) {
            player.playWhenReady = playWhenReady
        }
        return true
    }

    override fun dispatchSeekTo(player: Player, windowIndex: Int, positionMs: Long): Boolean {
        player.seekTo(windowIndex, positionMs)
        return true
    }

    override fun dispatchSetRepeatMode(player: Player, @Player.RepeatMode repeatMode: Int): Boolean {
        player.repeatMode = repeatMode
        return true
    }

    override fun dispatchSetShuffleModeEnabled(player: Player, shuffleModeEnabled: Boolean): Boolean {
        player.shuffleModeEnabled = shuffleModeEnabled
        return true
    }

    override fun dispatchStop(player: Player, reset: Boolean): Boolean {
        player.stop(reset)
        return true
    }


    // Player.EventListener
    override fun onPlayerError(error: ExoPlaybackException?) {
        Log.e("ExoPlayerManager", "onPlayerError:  ", error)
        mHasError = true
    }

    override fun onLoadingChanged(isLoading: Boolean) {
        Log.e("ExoPlayerManager", "onLoadingChanged:  $isLoading")
        mIsLoading = isLoading
    }

    private fun stateDesc(state: Int): String {
        return when (state) {
            Player.STATE_IDLE -> "STATE_IDLE"
            Player.STATE_BUFFERING -> "STATE_BUFFERING"
            Player.STATE_READY -> "STATE_READY"
            Player.STATE_ENDED -> "STATE_ENDED"
            else -> ""
        }
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        Log.e("ExoPlayerManager", "onPlayerStateChanged: playbackState=${stateDesc(playbackState)}")
        Log.e("ExoPlayerManager", "onPlayerStateChanged: playWhenReady=$playWhenReady")
        mPlaying = playWhenReady
    }
}