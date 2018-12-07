package com.github.luoyemyy.exo

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer


class ExoPlayerHelper private constructor(lifecycle: Lifecycle, private val context: Context) : LifecycleObserver {

    companion object {
        fun newInstance(compatActivity: AppCompatActivity): ExoPlayerHelper {
            return ExoPlayerHelper(compatActivity.lifecycle, compatActivity.applicationContext)
        }

        fun newInstance(fragment: Fragment): ExoPlayerHelper {
            return ExoPlayerHelper(fragment.lifecycle, fragment.requireActivity().applicationContext)
        }
    }

    init {
        lifecycle.addObserver(this)
    }

    private val mPlayer: SimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context)
    private var mCurrentPlayerView: ExoPlayerView? = null

    fun play(url: String, exoPlayerView: ExoPlayerView) {
        if (exoPlayerView != mCurrentPlayerView) {
            mCurrentPlayerView?.clearPlayer(mPlayer)
            mCurrentPlayerView = null
        }
        mCurrentPlayerView = exoPlayerView.apply {
            exoPlayerView.initPlayer(url, mPlayer)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(source: LifecycleOwner?) {
        mCurrentPlayerView?.clearPlayer(mPlayer)
        stop(true)
        source?.lifecycle?.removeObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause(source: LifecycleOwner?) {
        stop()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop(source: LifecycleOwner?) {
        stop()
    }

    private fun stop(release: Boolean = false) {
        if (mPlayer.playbackState == Player.STATE_READY && mPlayer.playWhenReady) {
            mPlayer.stop()
        }
        if (release) {
            mPlayer.release()
        }
    }
}