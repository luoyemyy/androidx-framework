package com.github.luoyemyy.framework.test.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Car {
    @PrimaryKey
    var id: Int = 0
    var name: String? = null
}