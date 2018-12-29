package com.github.luoyemyy.exo

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageView
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ControlDispatcher
import com.google.android.exoplayer2.DefaultControlDispatcher
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerControlView

class ExoPlayerControlView constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : PlayerControlView(context, attributeSet, defStyleAttr) {

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    constructor(context: Context) : this(context, null, 0)

    private lateinit var mImgBack: ImageView
    private lateinit var mImgPlay: ImageView
    private lateinit var mImgPause: ImageView
    private lateinit var mImgFullscreen: ImageView
    private lateinit var mLayout: View


    private lateinit var mControlDispatcher: ControlDispatcher
    private var mFullscreen: Boolean = false

    init {
        initView()
    }

    private fun initView() {
        mImgBack = findViewById(R.id.imgBack)
        mImgPlay = findViewById(R.id.exo_play)
        mImgPause = findViewById(R.id.exo_pause)
        mImgFullscreen = findViewById(R.id.imgFullscreen)
        mLayout = findViewById(R.id.controlLayout)
        mImgBack.setOnClickListener(clickListener)
        mImgPlay.setOnClickListener(clickListener)
        mImgPause.setOnClickListener(clickListener)
        mImgFullscreen.setOnClickListener(clickListener)
        mLayout.setOnClickListener(clickListener)

        mControlDispatcher = DefaultControlDispatcher()
        setControlDispatcher(mControlDispatcher)

        visibility(mImgBack, mFullscreen)
        visibility(mImgFullscreen, !mFullscreen)
        mImgFullscreen.setImageResource(if (mFullscreen) R.drawable.exo_controls_fullscreen_exit else R.drawable.exo_controls_fullscreen_enter)

        post {
            visibility(mImgPlay, true)
        }
    }

    private fun visibility(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private val clickListener = OnClickListener { v ->
        when (v) {
            mImgBack -> {
                ExoPlayerHelper.getInstance().exitFullScreen(true)
            }
            mImgPause -> {
                mControlDispatcher.dispatchSetPlayWhenReady(player, false)
            }
            mImgPlay -> {
                if (player.playbackState == Player.STATE_ENDED) {
                    mControlDispatcher.dispatchSeekTo(player, player.currentWindowIndex, C.TIME_UNSET)
                }
                mControlDispatcher.dispatchSetPlayWhenReady(player, true)
            }
            mImgFullscreen -> {
                ExoPlayerHelper.getInstance().enterFullscreen(context)
            }
            mLayout -> {
                show()
            }
        }
    }

}