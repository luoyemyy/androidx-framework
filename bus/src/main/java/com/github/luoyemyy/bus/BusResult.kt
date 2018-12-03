package com.github.luoyemyy.bus

import androidx.annotation.MainThread


interface BusResult {
    @MainThread
    fun busResult(event: String, msg: BusMsg)
}