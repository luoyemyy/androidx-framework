package com.github.luoyemyy.exo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager


class ExoPlayerHelper private constructor(){

    companion object {

        const val NETWORK_EVENT = ""

    }

    val networkReceiver= object :BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {

        }
    }

    fun s(context: Context){
        context.registerReceiver(networkReceiver, IntentFilter().apply {
            addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        })
    }

    fun hasWifi() {

    }

}