package com.github.luoyemyy.framework.test

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import com.google.android.exoplayer2.ui.PlayerView

class PlayerVideoView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : PlayerView(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null, 0)

    private lateinit var mFullScreenButton: Button

    init {

    }

}