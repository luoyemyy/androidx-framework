package com.github.luoyemyy.bus

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle

/**
 *
 */

object Bus {

    private val mCallbacks = mutableListOf<Callback>()
    private val mHandler = Handler(Looper.getMainLooper())

    private val mDebugListeners = mutableListOf<DebugListener>()

    interface Callback : BusResult {
        fun interceptEvent(): String
    }

    //debug
    interface DebugListener {
        fun onRegister(currentCallback: Callback, allCallbacks: List<Callback>)
        fun onUnRegister(currentCallback: Callback, allCallbacks: List<Callback>)
        fun onPost(event: String, callbacks: List<Callback>)
    }

    //debug
    fun addDebugListener(debugListener: DebugListener) {
        mDebugListeners.add(debugListener)
    }

    //debug
    private fun debugOnRegister(callback: Callback) {
        mDebugListeners.forEach {
            it.onRegister(callback, mCallbacks)
        }
    }

    //debug
    private fun debugOnUnRegister(callback: Callback) {
        mDebugListeners.forEach {
            it.onUnRegister(callback, mCallbacks)
        }
    }

    //debug
    private fun debugOnPost(event: String, callbacks: List<Callback>) {
        mDebugListeners.forEach {
            it.onPost(event, callbacks)
        }
    }

    /**
     * 注册观察者，如果当前callback已经被注册过，则忽略
     *
     * @param callback
     */
    @MainThread
    fun register(callback: Callback) {
        if (mCallbacks.none { it == callback }) {
            mCallbacks.add(callback)
            //debug
            debugOnRegister(callback)
        }
    }

    /**
     * 一个事件只能有一个观察者
     * 注册观察者,如果此事件有观察者，先移除已存在的，然后设置新的
     */
    @MainThread
    fun replaceRegister(callback: Callback) {
        mCallbacks.removeAll { it.interceptEvent() == callback.interceptEvent() }
        mCallbacks.add(callback)
        //debug
        debugOnRegister(callback)
    }

    /**
     * 注销观察者
     *
     * @param callback
     */
    @MainThread
    fun unRegister(callback: Callback) {
        mCallbacks.remove(callback)
        //debug
        debugOnUnRegister(callback)
    }

    /**
     * 派发消息
     */
    fun post(event: String, intValue: Int = 0, longValue: Long = 0L, boolValue: Boolean = false, stringValue: String? = null, extra: Bundle? = null) {
        mHandler.post {
            val msg = BusMsg(event, intValue, longValue, boolValue, stringValue, extra)
            mCallbacks.filter { it.interceptEvent() == event }.apply {
                debugOnPost(event, this)
            }.forEach { it.busResult(msg.event, msg) }
        }
    }

    /**
     * 添加观察者，同一事件可以被多个观察者监听
     * lifecycle#destroy时会注销此观察者
     */
    fun addCallback(lifecycle: Lifecycle, result: BusResult, vararg events: String) {
        addCallback(false, lifecycle, result, *events)
    }

    /**
     * 添加观察者，同一事件只能被一个观察者监听，后设置的会覆盖之前的
     * lifecycle#destroy时会注销此观察者
     */
    fun setCallback(lifecycle: Lifecycle, result: BusResult, vararg events: String) {
        addCallback(true, lifecycle, result, *events)
    }

    private fun addCallback(replace: Boolean, lifecycle: Lifecycle, result: BusResult, vararg events: String) {
        events.forEach {
            BusObserver(lifecycle, result, it).register(replace)
        }
    }
}
