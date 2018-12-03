package com.github.luoyemyy.framework.test.transition

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.luoyemyy.framework.test.R

class TransitionFragment2 : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_transition2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        view.findViewById<View>(R.id.imageView).apply {
            transitionName = arguments?.getString("share")
        }.setOnClickListener {
            requireFragmentManager().popBackStack()
        }
    }

}