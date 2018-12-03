@file:Suppress("unused")

package com.github.luoyemyy.config.ext

import android.os.AsyncTask
import android.os.Handler
import android.os.Looper

/**
 * thread run
 */
fun runOnWorker(run: () -> Unit) = AsyncTask.THREAD_POOL_EXECUTOR.execute(run)

fun runOnMain(run: () -> Unit) = Handler(Looper.getMainLooper()).post(run)
