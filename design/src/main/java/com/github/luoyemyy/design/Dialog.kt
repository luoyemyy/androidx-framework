package com.github.luoyemyy.design

import android.app.Application
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProviders

class Dialog : DialogFragment() {

    private var mTitle: String? = null
    private var mMessage: String? = null
    private var mBtnPositive: String? = null
    private var mBtnNegative: String? = null
    private var mBtnNeutral: String? = null
    private lateinit var mPresenter: Presenter


    companion object {
        const val STYLE_ALERT_DIALOG = 1
        const val STYLE_CONFIRM = 2

        const val KEY_TITLE = "title"
        const val KEY_MESSAGE = "message"
        const val KEY_BUTTON_POSITIVE = "positive"
        const val KEY_BUTTON_NEGATIVE = "negative"
        const val KEY_BUTTON_NEUTRAL = "neutral"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        mPresenter = ViewModelProviders.of(this).get(Presenter::class.java)

        val builder = AlertDialog.Builder(requireContext())
        return builder.create()
    }

    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)
    }

    class Presenter(app: Application) : AndroidViewModel(app) {
        fun onDissmiss(callback: () -> Unit) {}
        fun onCancel(callback: () -> Unit) {}
    }
}