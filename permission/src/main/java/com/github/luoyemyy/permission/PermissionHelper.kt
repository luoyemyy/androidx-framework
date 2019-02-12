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
import androidx.lifecycle.Lifecycle
import com.github.luoyemyy.bus.Bus
import com.github.luoyemyy.bus.BusMsg
import com.github.luoyemyy.bus.BusResult

object PermissionHelper {

    internal const val PERMISSION_EVENT = "permission_event"
    internal const val PERMISSIONS = "permissions"

    fun build(activity: FragmentActivity, vararg permissions: String): PermissionRequest {
        return PermissionRequest(activity, permissions)
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


    class PermissionRequest(private val mActivity: FragmentActivity, private val mPermissions: Array<out String>) : BusResult {

        private var mPassRunnable: (() -> Unit)? = null
        private var mDeniedRunnable: ((permissions: Array<String>) -> Unit)? = null
        private val mEvent = "${javaClass.name}.${System.currentTimeMillis()}"

        fun pass(pass: () -> Unit): PermissionRequest {
            mPassRunnable = pass
            return this
        }

        fun denied(denied: (permissions: Array<String>) -> Unit): PermissionRequest {
            mDeniedRunnable = denied
            return this
        }

        fun request() {
            Bus.addCallback(mActivity.lifecycle, this, mEvent)
            val requestPermissions = filterPermissions(mActivity, mPermissions) ?: return
            PermissionFragment.startPermissionFragment(mActivity.supportFragmentManager, mEvent, requestPermissions)
        }

        /**
         * fragment中调用此方法
         */
        fun request(fragmentLifecycle: Lifecycle) {
            Bus.addCallback(fragmentLifecycle, this, mEvent)
            val requestPermissions = filterPermissions(mActivity, mPermissions) ?: return
            PermissionFragment.startPermissionFragment(mActivity.supportFragmentManager, mEvent, requestPermissions)
        }

        override fun busResult(event: String, msg: BusMsg) {
            if (event == mEvent) {
                msg.extra?.getStringArray(PERMISSIONS)?.apply {
                    if (isEmpty()) {
                        mPassRunnable?.invoke()
                    } else {
                        mDeniedRunnable?.invoke(this)
                    }
                }
            }
        }

        /**
         * 返回未获得的权限列表
         */
        private fun filterPermissions(context: Context, permissions: Array<out String>): Array<String>? {
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
    }
}