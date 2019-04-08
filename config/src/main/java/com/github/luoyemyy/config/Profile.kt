package com.github.luoyemyy.config

class Profile private constructor() {

    private var type: Int = DEV

    companion object {

        const val DEV = 1
        const val TEST = 2
        const val DEMO = 3
        const val PRO = 4

        private val single: Profile = Profile()

        fun setType(type: Int) {
            single.type = type
        }

        fun currentType():Int{
            return single.type
        }

        fun currentTypeInfo():String{
            return when(currentType()){
                PRO -> "PRO"
                DEMO -> "DEMO"
                TEST -> "TEST"
                DEV -> "DEV"
                else -> "DEV"
            }
        }

    }

    class Value<T>(private val dev: T, private val test: T, private val demo: T, private val pro: T) {
        fun value(): T = when (single.type) {
            PRO -> pro
            DEMO -> demo
            TEST -> test
            DEV -> dev
            else -> dev
        }
    }

}

/**
 * dev=test=demo=pro
 */
fun <T> allPro(pro: T): Profile.Value<T> = Profile.Value(pro, pro, pro, pro)

/**
 * dev=test=demo,pro
 */
fun <T> devDevDevPro(dev: T, pro: T): Profile.Value<T> = Profile.Value(dev, dev, dev, pro)

/**
 * dev,test,demo=pro
 */
fun <T> devTestProPro(dev: T, test: T, pro: T): Profile.Value<T> = Profile.Value(dev, test, pro, pro)

/**
 * dev,test,demo,pro
 */
fun <T> eachValue(dev: T, test: T, demo: T, pro: T): Profile.Value<T> = Profile.Value(dev, test, demo, pro)