package com.github.luoyemyy.mvp

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

/**
 *
 *  example:
 *
 *  class MainActivity : AppCompatActivity() {
 *
 *      private lateinit var mBinding: ActivityMainBinding
 *
 *      override fun onCreate(savedInstanceState: Bundle?) {
 *          super.onCreate(savedInstanceState)
 *          mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
 *          mBinding.also {
 *              it.setLifecycleOwner(this)
 *              it.presenter = getPresenter(this)
 *          }
 *
 *          mBinding.presenter.load()
 *      }
 *
 *      class Presenter(app: Application) : AbstractPresenter<String>(app) {
 *
 *          override fun load(bundle: Bundle?) {
 *              data.value = "123"
 *          }
 *      }
 *  }
 *
 */
abstract class AbstractPresenter<T>(app: Application) : BasePresenter(app) {

    protected val data: MutableLiveData<T> by lazy { MutableLiveData<T>() }
    protected val list: MutableLiveData<List<T>> by lazy { MutableLiveData<List<T>>() }

    private var mInitialized = false

    fun setDataObserver(owner: LifecycleOwner, observer: Observer<T>) {
        data.observe(owner, observer)
    }

    fun setListObserver(owner: LifecycleOwner, observer: Observer<List<T>>) {
        list.observe(owner, observer)
    }

    fun isInitialized(): Boolean = mInitialized

    fun setInitialized() {
        mInitialized = true
    }

    open fun load(bundle: Bundle? = null) {}

}