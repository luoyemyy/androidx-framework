package com.github.luoyemyy.framework.test.transition

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.ChangeTransform
import androidx.transition.Fade
import androidx.transition.TransitionSet
import com.github.luoyemyy.mvp.recycler.setLinearManager
import com.github.luoyemyy.framework.test.R
import com.github.luoyemyy.framework.test.databinding.FragmentTransition1RecyclerBinding

class TransitionFragment1 : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_transition1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.setLinearManager()
        recyclerView.adapter = Adapter()
    }

    inner class Adapter : RecyclerView.Adapter<VH>() {

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): VH {
            return VH(FragmentTransition1RecyclerBinding.inflate(layoutInflater, p0, false))
        }

        override fun getItemCount(): Int {
            return 10
        }

        override fun onBindViewHolder(p0: VH, p1: Int) {

        }
    }


    inner class VH(binding: FragmentTransition1RecyclerBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.imageView.setOnClickListener {

                val shareName = "share_$adapterPosition"
                it.transitionName = shareName
                val transition = TransitionSet()
//                        .addTransition(ChangeImageTransform().apply {
//                            epicenterCallback = object : Transition.EpicenterCallback() {
//                                override fun onGetEpicenter(p0: Transition): Rect {
//                                    val rect = Rect()
//                                    it.getGlobalVisibleRect(rect)
//                                    return rect
//                                }
//                            }
//                        })
                        .addTransition(ChangeTransform()).addTransition(ChangeBounds())
                val f = TransitionFragment2().apply {
                    sharedElementEnterTransition = transition
                    sharedElementReturnTransition = transition
                    enterTransition = Fade(Fade.IN)
                    exitTransition = Fade(Fade.OUT)

                    arguments = Bundle().apply {
                        putString("share", shareName)
                    }
                }
                requireFragmentManager().beginTransaction().add(R.id.container, f).addSharedElement(it, shareName).addToBackStack(null).commit()
            }
        }
    }
}