package com.github.luoyemyy.framework.test.db

import androidx.room.TypeConverter
import java.util.*

class Converters {

    @TypeConverter
    fun toDate(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun toLong(date: Date?): Long? {
        return date?.time
    }
}