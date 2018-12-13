package com.github.luoyemyy.framework.test.navigation

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.github.luoyemyy.framework.test.R
import com.github.luoyemyy.framework.test.databinding.FragmentBlankBinding
import com.github.luoyemyy.framework.test.databinding.FragmentBlankRecyclerBinding
import com.github.luoyemyy.mvp.getPresenter
import com.github.luoyemyy.mvp.getRecyclerPresenter
import com.github.luoyemyy.mvp.recycler.*


class BlankFragment : Fragment() {

    private lateinit var mBinding: FragmentBlankBinding
    private lateinit var mPresenter: Presenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.e("BlankFragment", "onCreateView:  ")
        return FragmentBlankBinding.inflate(inflater, container, false).apply {
            mBinding = this
        }.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("BlankFragment", "onViewCreated:  ")
        mBinding.recyclerView.setLinearManager()
        val reload = savedInstanceState != null || this::mPresenter.isInitialized
        mPresenter = getRecyclerPresenter(this, Adapter())
        mPresenter.loadInit(reload,arguments)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("BlankFragment", "onCreate:  ")
    }

    inner class Adapter : AbstractSingleRecyclerAdapter<Any, FragmentBlankRecyclerBinding>(mBinding.recyclerView) {

        override fun bindContentViewHolder(binding: FragmentBlankRecyclerBinding, content: Any, position: Int) {
            binding.name = content.toString()
            binding.executePendingBindings()
        }

        override fun createContentView(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): FragmentBlankRecyclerBinding {
            return FragmentBlankRecyclerBinding.inflate(inflater, parent, false)
        }

        override fun onItemClickListener(vh: VH<FragmentBlankRecyclerBinding>, view: View?) {
            NavHostFragment.findNavController(this@BlankFragment).navigate(R.id.action_blankFragment_to_blankFragment2)
        }

    }

    class Presenter(var app: Application) : AbstractRecyclerPresenter<Any>(app) {
        override fun loadData(loadType: LoadType, paging: Paging, bundle: Bundle?, search: String?): List<Any>? {
            return listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 0)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.e("BlankFragment", "onActivityCreated:  ")
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Log.e("BlankFragment", "onAttach:  ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("BlankFragment", "onDestroy:  ")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e("BlankFragment", "onDestroyView:  ")
    }

    override fun onDetach() {
        super.onDetach()
        Log.e("BlankFragment", "onDetach:  ")
    }

    override fun onResume() {
        super.onResume()
        Log.e("BlankFragment", "onResume:  ")
    }

    override fun onStart() {
        super.onStart()
        Log.e("BlankFragment", "onStart:  ")
    }


    override fun onPause() {
        super.onPause()
        Log.e("BlankFragment", "onPause:  ")
    }

    override fun onStop() {
        super.onStop()
        Log.e("BlankFragment", "onStop:  ")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.e("BlankFragment", "onSaveInstanceState:  ")
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        Log.e("BlankFragment", "onHiddenChanged:  ")
    }
}
