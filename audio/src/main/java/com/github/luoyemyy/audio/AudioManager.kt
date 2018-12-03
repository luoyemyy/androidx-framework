package com.github.luoyemyy.audio

import android.support.v4.media.MediaBrowserCompat

object AudioManager {
    private val mSources = mutableMapOf<String, MutableList<MediaBrowserCompat.MediaItem>>()

    fun getMedias(key: String): MutableList<MediaBrowserCompat.MediaItem> {
        return mSources[key] ?: mutableListOf()
    }
}