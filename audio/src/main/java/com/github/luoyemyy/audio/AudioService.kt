package com.github.luoyemyy.audio

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.MediaBrowserServiceCompat

class AudioService : MediaBrowserServiceCompat() {

    private var mMediaSession: MediaSessionCompat? = null
    private lateinit var mPlaybackState: PlaybackStateCompat.Builder

    override fun onCreate() {
        super.onCreate()
        mMediaSession = MediaSessionCompat(baseContext, "AudioService").apply {
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
            mPlaybackState = PlaybackStateCompat.Builder()
                    .setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PLAY_PAUSE)
            setPlaybackState(mPlaybackState.build())

        }
    }


    override fun onLoadChildren(parentMediaId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.sendResult(AudioManager.getMedias(parentMediaId))
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        return BrowserRoot("play", null)
    }
}