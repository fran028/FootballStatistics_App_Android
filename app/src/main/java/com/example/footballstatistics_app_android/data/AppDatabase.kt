package com.example.footballstatistics_app_android.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(
    entities = [User::class, Match::class, Location::class],
    version = 6,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun matchDao(): MatchDao
    abstract fun locationDao(): LocationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        lateinit var context: Context

        fun getDatabase(context: Context): AppDatabase {
            //Use the applicationContext
            val appContext = context.applicationContext
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    appContext, // Use application context
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}