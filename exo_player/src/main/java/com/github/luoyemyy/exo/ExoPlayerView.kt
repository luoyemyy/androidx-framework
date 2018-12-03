package com.github.luoyemyy.exo

import android.content.Context
import android.util.AttributeSet
import com.google.android.exoplayer2.ui.PlayerView

class ExoPlayerView constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : PlayerView(context, attributeSet, defStyleAttr) {

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context) : this(context, null, 0)


}