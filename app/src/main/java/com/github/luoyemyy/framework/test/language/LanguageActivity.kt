package com.github.luoyemyy.framework.test.language

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.github.luoyemyy.config.Language
import com.github.luoyemyy.framework.test.R
import com.github.luoyemyy.framework.test.databinding.ActivityLanguageBinding

class LanguageActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mBinding: ActivityLanguageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_language)


        val key = Language.getLanguageKey(this)
        if (key != Language.AUTO) mBinding.txtAUTO.setCompoundDrawables(null, null, null, null)
        if (key != Language.CN) mBinding.txtCN.setCompoundDrawables(null, null, null, null)
        if (key != Language.CN_HK) mBinding.txtCNHK.setCompoundDrawables(null, null, null, null)
        if (key != Language.CN_TW) mBinding.txtCNTW.setCompoundDrawables(null, null, null, null)
        if (key != Language.EN_US) mBinding.txtENUS.setCompoundDrawables(null, null, null, null)

        mBinding.apply {
            txtAUTO.setOnClickListener(this@LanguageActivity)
            txtCN.setOnClickListener(this@LanguageActivity)
            txtCNHK.setOnClickListener(this@LanguageActivity)
            txtCNTW.setOnClickListener(this@LanguageActivity)
            txtENUS.setOnClickListener(this@LanguageActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            mBinding.txtAUTO -> Language.changeLanguage(this, Language.AUTO)
            mBinding.txtCN -> Language.changeLanguage(this, Language.CN)
            mBinding.txtCNHK -> Language.changeLanguage(this, Language.CN_HK)
            mBinding.txtCNTW -> Language.changeLanguage(this, Language.CN_TW)
            mBinding.txtENUS -> Language.changeLanguage(this, Language.EN_US)
        }
        recreate()
    }

}