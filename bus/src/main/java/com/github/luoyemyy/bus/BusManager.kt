package com.github.luoyemyy.bus

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle

/**
 *
 */
object BusManager {

    private val mCallbacks = mutableListOf<Callback>()
    private val mHandler = Handler(Looper.getMainLooper())

    interface Callback : BusResult {
        fun interceptEvent(): String
    }

    /**
     * 注册回调
     *
     * @param callback
     */
    @MainThread
    fun register(callback: Callback) {
        mCallbacks.add(callback)
    }

    @MainThread
    fun replaceRegister(callback: Callback) {
        mCallbacks.removeAll { it.interceptEvent() == callback.interceptEvent() }
        mCallbacks.add(callback)
    }

    /**
     * 反注册回调
     *
     * @param callback
     */
    @MainThread
    fun unRegister(callback: Callback) {
        mCallbacks.remove(callback)
    }

    /**
     * 派发消息
     */
    fun post(event: String, intValue: Int = 0, longValue: Long = 0L, boolValue: Boolean = false, stringValue: String? = null, extra: Bundle? = null) {
        mHandler.post {
            val msg = BusMsg(event, intValue, longValue, boolValue, stringValue, extra)
            mCallbacks.filter { it.interceptEvent() == event }.forEach { it.busResult(msg.event, msg) }
        }
    }

    fun setCallback(lifecycle: Lifecycle, result: BusResult, vararg events: String) {
        events.forEach {
            BusRegistry(lifecycle, result, it).register(false)
        }
    }


    fun replaceCallback(lifecycle: Lifecycle, result: BusResult, vararg events: String) {
        events.forEach {
            BusRegistry(lifecycle, result, it).register(true)
        }
    }
}
