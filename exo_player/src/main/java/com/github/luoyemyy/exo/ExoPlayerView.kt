package com.github.luoyemyy.exo

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.exoplayer2.SimpleExoPlayer

class ExoPlayerView constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : FrameLayout(context, attributeSet, defStyleAttr), View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context) : this(context, null, 0)

    private lateinit var mPlayerLayout: ViewGroup
    private lateinit var mSurfaceView: SurfaceView
    private lateinit var mImgPlaceHolder: ImageView

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

    private var mControlListener: ControlListener? = null

    init {
        initView()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.exo_player_simple_player_view, this)
        mPlayerLayout = findViewById(R.id.playerLayout)
        mSurfaceView = findViewById(R.id.surfaceView)
        mImgPlaceHolder = findViewById(R.id.imgPlaceHolder)
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

        mImgBack.setOnClickListener(this)
        mImgFullscreen.setOnClickListener(this)
        mImgPlay.setOnClickListener(this)
        mImgStop.setOnClickListener(this)
        mSeekBar.setOnSeekBarChangeListener(this)
    }

//    fun initPlayer(url: String, player: SimpleExoPlayer) {
//        val surfaceView = surfaceView
//        player.addListener(playerListener)
//        player.videoComponent?.apply {
//            clearVideoSurface()
//            when (surfaceView) {
//                is SurfaceView -> setVideoSurfaceView(surfaceView)
//                is TextureView -> setVideoTextureView(surfaceView)
//            }
//        }
//        val dataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context, context.packageName))
//        val videoSource = ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(url))
//        player.prepare(videoSource)
//    }
//
//    fun clearPlayer(player: SimpleExoPlayer) {
//        player.removeListener(playerListener)
//        player.videoComponent?.clearVideoSurface()
//    }

    fun attachToPlayerView(player: SimpleExoPlayer, controlListener: ControlListener) {
        mControlListener = controlListener
        player.videoComponent?.apply {
            setVideoSurfaceView(mSurfaceView)
        }
    }

    private fun fullScreen() {
        mControlListener?.fullScreen()
    }

    private fun exitFullScreen() {
        mControlListener?.exitFullScreen()
    }

    private fun play() {
        mControlListener?.play()
    }

    private fun stop() {
        mControlListener?.stop()
    }

    private fun seekTo(position: Int) {
        mControlListener?.seekTo(position)
    }

    interface ControlListener {
        fun fullScreen()
        fun exitFullScreen()
        fun play()
        fun stop()
        fun seekTo(position: Int)
    }

    override fun onClick(v: View?) {
        when (v) {
            mImgBack -> {
                exitFullScreen()
            }
            mImgFullscreen -> {
                fullScreen()
            }
            mImgPlay -> {
                play()
            }
            mImgStop -> {
                stop()
            }
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            seekTo(progress)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        //nothing
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        //nothing
    }
}