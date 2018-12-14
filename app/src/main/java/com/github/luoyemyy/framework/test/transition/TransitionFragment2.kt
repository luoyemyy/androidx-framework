package com.github.luoyemyy.framework.test.transition

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.luoyemyy.framework.test.R

class TransitionFragment2 : Fragment() {

    private lateinit var mTextView1: TextView
    private lateinit var mTextView2: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_transition2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        mTextView1 = view.findViewById(R.id.textView1)
        mTextView2 = view.findViewById(R.id.textView2)

        mTextView1.setOnClickListener {
            animator(mTextView1, mTextView2)
        }

        mTextView2.setOnClickListener {
            animator(mTextView2, mTextView1)
        }

    }

    fun animator(toLarge: TextView, toSmall: TextView) {
        //#111111 17  #444444 68  51
        val valueAnimator = ValueAnimator.ofFloat(0f, 5f)
        valueAnimator.addUpdateListener { a ->
            val value = a.animatedValue as Float
            toSmall.textSize = 20f - value
            toLarge.textSize = 15f + value
            val value1 = 17 + (51 * value / 5).toInt()
            val value2 = 68 - (51 * value / 5).toInt()
            toSmall.setTextColor(Color.argb(255, value1, value1, value1))
            toLarge.setTextColor(Color.argb(255, value2, value2, value2))
        }
        valueAnimator.duration = 300
        valueAnimator.start()
    }

}