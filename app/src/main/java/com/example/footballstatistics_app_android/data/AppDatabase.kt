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
//    autoMigrations = [
//      AutoMigration(from = 3, to = 4),
//   ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun matchDao(): MatchDao
    abstract fun locationDao(): LocationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        lateinit var context: Context

        val MIGRATION_3_TO_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add the new column
                db.execSQL("ALTER TABLE Matchs ADD COLUMN new_column TEXT")
            }
        }

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
                    //.addMigrations(MIGRATION_3_TO_4) // Add your migrations here
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}