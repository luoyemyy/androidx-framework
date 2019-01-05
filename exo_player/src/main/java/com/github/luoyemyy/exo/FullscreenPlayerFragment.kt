package com.github.luoyemyy.exo

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.exoplayer2.ui.PlayerView

class FullscreenPlayerFragment : DialogFragment() {

    private lateinit var mPlayerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, 0)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.player_view_fragment_fullscreen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mPlayerView = view.findViewById(R.id.playerView)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ExoPlayerHelper.getInstance().playCurrent(mPlayerView)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        ExoPlayerHelper.getInstance().exitFullScreen(false)
    }
}