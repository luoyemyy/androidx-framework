package com.github.luoyemyy.framework.test.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDao {
    @Insert
    fun saveUser(user: User): Long

    @Insert
    fun saveUsers(vararg user: User)

    @Insert
    fun saveUsers(user: User, users: List<User>)

    @Update
    fun updateUser(vararg user: User)

    @Delete
    fun deleteUser(vararg user: User)

    @Query("select * from user")
    fun selectUser(): LiveData<List<User>>

    @Query("select loginName from user limit 1")
    fun selectUsername(): LiveData<String>
}