@file:Suppress("unused")

package com.github.luoyemyy.ext

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
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

/**
 * 关闭键盘
 */
fun Activity.hideKeyboard() {
    val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val focusView = currentFocus
    if (manager.isActive && focusView != null && focusView.windowToken != null) {
        manager.hideSoftInputFromWindow(focusView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}

/**
 * 判断坐标是否在 View 上
 */
fun View.pointInView(x: Int, y: Int): Boolean {
    val location = intArrayOf(0, 0)
    getLocationOnScreen(location)
    val rect = Rect(location[0], location[1], location[0] + width, location[1] + height)
    return rect.contains(x, y)
}

/**
 * 判断坐标是否在 ViewGroup 中的 EditText 上
 */
fun ViewGroup.pointInEditText(x: Int, y: Int): Boolean {
    (0 until childCount).forEach {
        val view = getChildAt(it)
        if (view.pointInView(x, y)) {
            return when (view) {
                is EditText -> true
                is ViewGroup -> view.pointInEditText(x, y)
                else -> false
            }
        }
    }
    return false
}
