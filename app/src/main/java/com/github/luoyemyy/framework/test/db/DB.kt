package com.github.luoyemyy.framework.test.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [User::class], version = 1)
@TypeConverters(Converters::class)
abstract class DB : RoomDatabase() {

    abstract fun getUserDao(): UserDao

    companion object {

        private var instance: DB? = null

//        private val MIGRATION_1_2 = object : Migration(1, 2) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("CREATE TABLE IF NOT EXISTS `Car` (`id` INTEGER NOT NULL, `name` TEXT, PRIMARY KEY(`id`))")
////                database.execSQL("ALTER TABLE Car ADD COLUMN pub_year INTEGER")
//            }
//        }
//
//        private val MIGRATION_2_3 = object : Migration(2, 3) {
//            override fun migrate(database: SupportSQLiteDatabase) {
////                database.execSQL("CREATE TABLE IF NOT EXISTS `Car` (`id` INTEGER NOT NULL, `name` TEXT, PRIMARY KEY(`id`))")
////                database.execSQL("ALTER TABLE Car ADD COLUMN pub_year INTEGER")
//                database.execSQL("DROP TABLE IF EXISTS Car")
//            }
//        }

        fun init(appContext: Context) {
            instance = Room.databaseBuilder(appContext, DB::class.java, "database")
//                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .fallbackToDestructiveMigration()
                    .build()
        }

        @JvmStatic
        fun getInstance(): DB {
            return instance
                    ?: throw NullPointerException("call DB.init(appContext) in Application.onCreate()")
        }
    }
}

fun userDao(): UserDao = DB.getInstance().getUserDao()