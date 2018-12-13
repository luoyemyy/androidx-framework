@file:Suppress("UNUSED_PARAMETER", "unused")

package com.github.luoyemyy.bus

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent


/**
 * bus 管理注册器
 * 注册后，此事件监听会绑定生命周期，不用手动去释放
 */
internal class BusObserver constructor(private val lifecycle: Lifecycle, private val mResult: BusResult, private var mEvent: String) : Bus.Callback, LifecycleObserver {

    private val pendingEvents: MutableList<EventInfo> = mutableListOf()

    init {
        lifecycle.addObserver(this)
    }

    fun register(replace: Boolean) {
        if (replace) {
            Bus.replaceRegister(this)
        } else {
            Bus.register(this)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(source: LifecycleOwner?) {
        Bus.unRegister(this)
        source?.lifecycle?.removeObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume(source: LifecycleOwner?) {
        pendingEvents.forEach {
            mResult.busResult(it.event, it.msg)
        }
        pendingEvents.clear()
    }

    override fun interceptEvent(): String = mEvent

    override fun busResult(event: String, msg: BusMsg) {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            mResult.busResult(event, msg)
        } else {
            pendingEvents.add(EventInfo(event, msg))
        }
    }

    data class EventInfo(val event: String, val msg: BusMsg)
}
