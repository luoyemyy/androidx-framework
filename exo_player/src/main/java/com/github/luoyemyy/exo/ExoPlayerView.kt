package com.github.luoyemyy.exo

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.*
import android.widget.*
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

open class ExoPlayerView constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : FrameLayout(context, attributeSet, defStyleAttr) {

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context) : this(context, null, 0)

    private lateinit var mPlayerLayout: ViewGroup
    private lateinit var surfaceView: View
    private lateinit var imgPlaceHolder: ImageView

    private lateinit var mControlLayout: ViewGroup
    private lateinit var mImgBack: View
    private lateinit var mTxtTitle: TextView
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mTxtMessage: TextView
    private lateinit var mImgFullscreen: View
    private lateinit var mImgPlay: View
    private lateinit var mImgStop: View
    private lateinit var mTxtPosition: TextView
    private lateinit var mTxtLength: TextView
    private lateinit var mSeekBar: SeekBar

    private var mPlayer: Player? = null
    private val playerListener: PlayerListener = PlayerListener()

    init {
        initView()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.exo_player_exo_player_view, this)
        mPlayerLayout = findViewById(R.id.playerLayout)
        surfaceView = newSurfaceView()
        mPlayerLayout.addView(surfaceView, 0, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        imgPlaceHolder = findViewById(R.id.imgPlaceHolder)
        mControlLayout = findViewById(R.id.controlLayout)
        mImgBack = findViewById(R.id.imgBack)
        mTxtTitle = findViewById(R.id.txtTitle)
        mProgressBar = findViewById(R.id.progressBar)
        mTxtMessage = findViewById(R.id.txtMessage)
        mImgFullscreen = findViewById(R.id.imgFullscreen)
        mImgPlay = findViewById(R.id.imgPlay)
        mImgStop = findViewById(R.id.imgStop)
        mTxtPosition = findViewById(R.id.txtPosition)
        mTxtLength = findViewById(R.id.txtLength)
        mSeekBar = findViewById(R.id.seekBar)

        mImgBack.setOnClickListener(playerListener)
        mImgFullscreen.setOnClickListener(playerListener)
        mImgPlay.setOnClickListener(playerListener)
        mImgStop.setOnClickListener(playerListener)
        mSeekBar.setOnSeekBarChangeListener(playerListener)
    }

    open fun newSurfaceView(): View = SurfaceView(context)

    fun initPlayer(url: String, player: SimpleExoPlayer) {
        val surfaceView = surfaceView
        player.addListener(playerListener)
        player.videoComponent?.apply {
            clearVideoSurface()
            when (surfaceView) {
                is SurfaceView -> setVideoSurfaceView(surfaceView)
                is TextureView -> setVideoTextureView(surfaceView)
            }
        }
        val dataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context, context.packageName))
        val videoSource = ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(url))
        player.prepare(videoSource)
    }

    fun clearPlayer(player: SimpleExoPlayer) {
        player.removeListener(playerListener)
        player.videoComponent?.clearVideoSurface()
    }



    interface UserAction {
        fun play()
        fun stop()
        fun fullscreen()
        fun exitFullscreen()
    }

    inner class PlayerListener : OnClickListener, Player.EventListener, SeekBar.OnSeekBarChangeListener {

        override fun onClick(v: View?) {
            when (v) {
                mImgBack -> {
                }
                mImgFullscreen -> {
                }
                mImgPlay -> {

                }
                mImgStop -> {
                }
            }
        }

        override fun onLoadingChanged(isLoading: Boolean) {

        }

        override fun onPlayerError(error: ExoPlaybackException?) {

        }

        override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {

        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

        }

        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (fromUser) {
                mPlayer?.seekTo(progress.toLong())
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            //nothing
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            //nothing
        }
    }
}