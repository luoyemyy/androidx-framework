@file:Suppress("unused")

package com.github.luoyemyy.ext

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog

fun Activity.alert(messageId: Int = 0, message: String = "alert message", okButtonId: Int = 0, okButton: String = "ok") = AlertDialog.Builder(this).setMessage(if (messageId > 0) getString(messageId) else message).setPositiveButton(if (okButtonId > 0) getString(okButtonId) else okButton, null).create().show()

fun Activity.confirm(messageId: Int = 0, message: String = "confirm message", cancelButtonId: Int = 0, cancelButton: String = "cancel", cancelCallback: () -> Unit = {}, okButtonId: Int = 0, okButton: String = "ok", okCallback: () -> Unit = {}) = AlertDialog.Builder(this).setMessage(if (messageId > 0) getString(messageId) else message).setNegativeButton(if (cancelButtonId > 0) getString(cancelButtonId) else cancelButton) { _, _ -> cancelCallback() }.setPositiveButton(if (okButtonId > 0) getString(okButtonId) else okButton) { _, _ -> okCallback() }.create().show()

/**
 * 浸入式状态栏
 * 使用drawerLayout时可以在NavigationView中增加app:insetForeground="@android:color/transparent"，侧滑栏适配浸入式
 */
fun Activity.immerse() {
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN and View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = Color.TRANSPARENT
}

/**
 * 状态栏黑色字体
 */
fun Activity.lightStatusBar() {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
}