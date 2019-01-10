@file:Suppress("unused")

package com.github.luoyemyy.config

import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper

/**
 * spf
 */

fun Context.spf(): SharedPreferences {
    return this.getSharedPreferences(AppInfo.appInfo, 0)
}

fun Context.editor(): SharedPreferences.Editor {
    return spf().edit()
}

fun Context.spfBool(key: String): Boolean {
    return spf().getBoolean(key, false)
}

fun Context.spfBool(key: String, value: Boolean) {
    editor().putBoolean(key, value).apply()
}

fun Context.spfInt(key: String): Int {
    return spf().getInt(key, 0)
}

fun Context.spfInt(key: String, value: Int) {
    editor().putInt(key, value).apply()
}

fun Context.spfLong(key: String): Long {
    return spf().getLong(key, 0)
}

fun Context.spfLong(key: String, value: Long) {
    editor().putLong(key, value).apply()
}

fun Context.spfString(key: String): String? {
    return spf().getString(key, null)
}

fun Context.spfString(key: String, value: String) {
    editor().putString(key, value).apply()
}

fun Context.spfRemove(key: String) {
    editor().remove(key).apply()
}

fun Context.spfClear() {
    editor().clear().apply()
}

/**
 * thread run
 */
fun runOnWorker(run: () -> Unit) = AsyncTask.THREAD_POOL_EXECUTOR.execute(run)

fun runOnMain(run: () -> Unit) = Handler(Looper.getMainLooper()).post(run)
