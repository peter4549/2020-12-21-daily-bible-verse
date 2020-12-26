package com.duke.elliot.biblereadinghabits.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

const val APP_DATABASE_NAME = "com.duke.elliot.bible_reading_habits.app_data_base.v1.0.0-debug1"

@Database(entities = [FavoriteBibleVerse::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun favoriteBibleVerseDao(): FavoriteBibleVerseDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = instance

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        APP_DATABASE_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    this.instance = instance
                }
                return instance
            }
        }
    }
}