package com.github.luoyemyy.mvp.recycler

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class VH<BIND : ViewDataBinding>(var binding: BIND?, view: View) : RecyclerView.ViewHolder(view)
