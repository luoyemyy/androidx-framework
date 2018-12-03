//package com.github.luoyemyy.framework.test.paging
//
//import android.arch.paging.PagedListAdapter
//import android.content.Context
//import android.support.v7.util.DiffUtil
//import android.support.v7.widget.RecyclerView
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import com.github.luoyemyy.framework.test.databinding.ActivityPagingRecyclerBinding
//
//class PagingAdapter(val context: Context) : PagedListAdapter<Paging, PagingAdapter.VH>(DIFF_CALLBACK) {
//    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): VH {
//        return VH(ActivityPagingRecyclerBinding.inflate(LayoutInflater.from(context), p0, false))
//    }
//
//    override fun onBindViewHolder(p0: VH, p1: Int) {
//        val paging = getItem(p1)
//        p0.binding.name = paging?.i?.toString() ?: (-1).toString()
//    }
//
//    companion object {
//        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Paging>() {
//            override fun areItemsTheSame(p0: Paging, p1: Paging): Boolean = false
//            override fun areContentsTheSame(p0: Paging, p1: Paging): Boolean = false
//        }
//    }
//
//    inner class VH(var binding: ActivityPagingRecyclerBinding) : RecyclerView.ViewHolder(binding.root)
//}