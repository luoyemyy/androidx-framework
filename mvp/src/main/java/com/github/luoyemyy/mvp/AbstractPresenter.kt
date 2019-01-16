package com.github.luoyemyy.mvp

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

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
abstract class AbstractPresenter<T>(app: Application) : AndroidViewModel(app) {

    val data = MutableLiveData<T>()

    private var mInitialized = false

    fun isInitialized(): Boolean = mInitialized

    fun setInitialized() {
        mInitialized = true
    }

    open fun load(bundle: Bundle? = null) {}

}