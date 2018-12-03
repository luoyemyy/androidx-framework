/*
 * Copyright 2018 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.luoyemyy.audio

import android.annotation.TargetApi
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.media.AudioAttributesCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer


class AudioFocusExoPlayerDecorator(context: Context, private val player: SimpleExoPlayer) : AudioManager.OnAudioFocusChangeListener, ExoPlayer by player {

    companion object {
        private const val TAG = "AFExoPlayerDecorator"
        private const val MEDIA_VOLUME_DEFAULT = 1.0f
        private const val MEDIA_VOLUME_DUCK = 0.2f
    }

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val audioAttributes = AudioAttributesCompat.Builder()
            .setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributesCompat.USAGE_MEDIA)
            .build()

    /**
     * Similar to [Player.getPlayWhenReady], but reflects the intent to play.
     */
    private var shouldPlayWhenReady = false

    override fun onAudioFocusChange(focusChange: Int) {

        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (shouldPlayWhenReady || player.playWhenReady) {
                    player.playWhenReady = true
                    player.volume = MEDIA_VOLUME_DEFAULT
                }
                shouldPlayWhenReady = false
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                if (player.playWhenReady) {
                    player.volume = MEDIA_VOLUME_DUCK
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                // Save the current state of playback so the _intention_ to play can be properly
                // reported to the app.
                shouldPlayWhenReady = player.playWhenReady
                player.playWhenReady = false
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                // This will chain through to abandonAudioFocus().
                playWhenReady = false
            }
        }
    }

    @get:RequiresApi(Build.VERSION_CODES.O)
    private val audioFocusRequest by lazy { buildFocusRequest() }

    override fun setPlayWhenReady(playWhenReady: Boolean) {
        if (playWhenReady) {
            requestAudioFocus()
        } else {
            if (shouldPlayWhenReady) {
                shouldPlayWhenReady = false
            }
            player.playWhenReady = false
            abandonAudioFocus()
        }
    }

    /**
     * @see [Player.getPlayWhenReady]
     * @return `true` when the underlying player's `playWhenReady` is true OR when
     * it would be true, but [AudioManager.AUDIOFOCUS_LOSS_TRANSIENT] has forced a temporary
     * pause in playback.
     */
    override fun getPlayWhenReady(): Boolean = player.playWhenReady || shouldPlayWhenReady

    private fun requestAudioFocus() {
        val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requestAudioFocusOreo()
        } else {
            audioManager.requestAudioFocus(this, audioAttributes.legacyStreamType, AudioManager.AUDIOFOCUS_GAIN)
        }

        // Call the listener whenever focus is granted - even the first time!
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            shouldPlayWhenReady = true
            this.onAudioFocusChange(AudioManager.AUDIOFOCUS_GAIN)
        } else {
            Log.i(TAG, "Playback not started: Audio focus request denied")
        }
    }

    private fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            abandonAudioFocusOreo()
        } else {
            @Suppress("deprecation")
            audioManager.abandonAudioFocus(this)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestAudioFocusOreo(): Int = audioManager.requestAudioFocus(audioFocusRequest)

    @RequiresApi(Build.VERSION_CODES.O)
    private fun abandonAudioFocusOreo() = audioManager.abandonAudioFocusRequest(audioFocusRequest)

    @TargetApi(Build.VERSION_CODES.O)
    private fun buildFocusRequest(): AudioFocusRequest {
        return AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAttributes.unwrap() as AudioAttributes)
                .setOnAudioFocusChangeListener(this)
                .build()
    }

}


