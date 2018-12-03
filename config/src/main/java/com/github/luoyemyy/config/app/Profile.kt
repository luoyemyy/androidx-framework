package com.github.luoyemyy.config.app

interface Profile {

    fun type(): Int

    fun isDev() = type() == DEV

    fun isTest() = type() == TEST

    fun isDemo() = type() == DEMO

    fun isPro() = type() == PRO

    companion object {
        const val DEV = 1
        const val TEST = 2
        const val DEMO = 3
        const val PRO = 4
    }
}