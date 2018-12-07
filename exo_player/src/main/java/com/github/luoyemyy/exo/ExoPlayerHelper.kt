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


class ExoPlayerHelper private constructor(lifecycle: Lifecycle, private val context: Context) : LifecycleObserver, Player.EventListener {

    companion object {
        fun newInstance(compatActivity: AppCompatActivity): ExoPlayerHelper {
            return ExoPlayerHelper(compatActivity.lifecycle, compatActivity.applicationContext)
        }

        fun newInstance(fragment: Fragment): ExoPlayerHelper {
            return ExoPlayerHelper(fragment.lifecycle, fragment.requireActivity().applicationContext)
        }
    }

    private val mPlayer: SimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context)
    private var mCurrentPlayerView: ExoPlayerView? = null

    init {
        mPlayer.addListener(this)
        lifecycle.addObserver(this)
    }


    fun play(url: String, exoPlayerView: ExoPlayerView) {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(source: LifecycleOwner?) {
        stop(true)
        source?.lifecycle?.removeObserver(this)
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