package com.github.luoyemyy.framework.test.transition

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.github.luoyemyy.framework.test.R

class TransitionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(FrameLayout(this).apply {
            id = R.id.container
        }, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        supportFragmentManager.beginTransaction().add(R.id.container, TransitionFragment2()).commit()

    }

}