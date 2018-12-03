package com.github.luoyemyy.permission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

object PermissionHelper {

    const val REQUEST_CODE = 95

    fun withPass(pass: (() -> Unit)): Future {
        return Future().withPass(pass)
    }

    class Future internal constructor() : Observer<Array<String>> {

        private var mPresenter: PermissionPresenter? = null
        private var mPassRunnable: (() -> Unit)? = null
        private var mDeniedRunnable: ((future: Future, permissions: Array<String>) -> Unit)? = null

        override fun onChanged(deniedArray: Array<String>?) {
            if (deniedArray == null) {
                return
            }
            if (deniedArray.isEmpty()) {
                mPassRunnable?.invoke()
            } else {
                mDeniedRunnable?.invoke(this, deniedArray)
            }

            //clear
            mPresenter?.removeObserver(this)
            mPresenter = null
            mPassRunnable = null
            mDeniedRunnable = null
        }

        /**
         * 设置授权通过的回调
         */
        fun withPass(pass: (() -> Unit)): Future {
            mPassRunnable = pass
            return this
        }

        /**
         * 设置授权失败的回调
         * 可以使用 future.toSettings(activity,msg)，跳到应用详情页去授权
         */
        fun withDenied(denied: ((future: Future, permissions: Array<String>) -> Unit)): Future {
            mDeniedRunnable = denied
            return this
        }

        fun request(activity: FragmentActivity, permissions: Array<String>) {

            val requestPermission = filterPermissions(activity, permissions) ?: return

            mPresenter = ViewModelProviders.of(activity).get(PermissionPresenter::class.java)
            mPresenter?.addObserver(activity, this)

            PermissionFragment.startPermissionFragment(activity.supportFragmentManager, requestPermission)
        }

        /**
         * 返回未获得的权限列表
         */
        private fun filterPermissions(context: Context, permissions: Array<String>): Array<String>? {
            if (permissions.isEmpty()) {
                mPassRunnable?.invoke()
                return null
            }
            val requestPermission = permissions.filter { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_DENIED }.toTypedArray()
            if (requestPermission.isEmpty()) {
                mPassRunnable?.invoke()
                return null
            }
            return requestPermission
        }

        fun toSettings(activity: Activity, msg: String, cancel: String = "取消", sure: String = "去设置") {
            AlertDialog.Builder(activity).setMessage(msg).setNegativeButton(cancel, null).setPositiveButton(sure) { _, _ ->
                val intent = Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.parse("package:${activity.packageName}")
                }
                if (intent.resolveActivity(activity.packageManager) != null) {
                    activity.startActivity(intent)
                }
            }.show()
        }

    }


}