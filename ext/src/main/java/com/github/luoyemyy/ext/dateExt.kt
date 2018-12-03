@file:Suppress("unused")

package com.github.luoyemyy.ext

import java.text.SimpleDateFormat
import java.util.*

/**
 * date
 */

fun Date?.formatDate(sdf: SimpleDateFormat = DateExt.sdfYMD()): String? = if (this == null) null else sdf.format(this)

fun Date?.formatDateTime(): String? = this.formatDate(DateExt.sdfYMDHMS())

fun String?.parseDate(sdf: SimpleDateFormat = DateExt.sdfYMD()): Date? = if (this == null || this.isEmpty()) null else sdf.parse(this)

fun String?.parseDateTime(): Date? = this.parseDate(DateExt.sdfYMDHMS())

fun Date?.clearTime(): Calendar? {
    return if (this == null) {
        return null
    } else Calendar.getInstance().also {
        it.time = this
        it.set(Calendar.HOUR_OF_DAY, 0)
        it.set(Calendar.MINUTE, 0)
        it.set(Calendar.SECOND, 0)
        it.set(Calendar.MILLISECOND, 0)
    }
}

object DateExt {

    const val YMDHMS = "yyyy-MM-dd HH:mm:ss"
    const val YMD = "yyyy-MM-dd"

    fun sdfYMD() = SimpleDateFormat(YMD, Locale.getDefault())
    fun sdfYMDHMS() = SimpleDateFormat(YMDHMS, Locale.getDefault())

}