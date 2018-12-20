package com.github.luoyemyy.framework.test.db

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface CarDao {
    @Insert
    fun add(car: Car)
}