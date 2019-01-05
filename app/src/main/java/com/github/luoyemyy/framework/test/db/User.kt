package com.github.luoyemyy.framework.test.db

import androidx.room.*
import java.util.*

@Entity(indices = [Index(value = ["mobile"])])
data class User(
        @PrimaryKey(autoGenerate = true) var id: Int = 0,
        var mobile: String? = null,
        @ColumnInfo(name = "loginName") var username: String? = null,
        @ColumnInfo var password: String? = null,
        @Ignore var createDate: Date? = null,
        @Embedded var address: Address? = null
)

