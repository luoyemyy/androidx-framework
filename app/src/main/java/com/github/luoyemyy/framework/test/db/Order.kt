package com.github.luoyemyy.framework.test.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Order(
        @PrimaryKey var id: Int = 0,
        @ColumnInfo var name: String? = null
)