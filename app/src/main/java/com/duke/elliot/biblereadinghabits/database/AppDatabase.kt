package com.duke.elliot.biblereadinghabits.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

const val DATABASE_NAME = "com.duke.elliot.bible_reading_habits.app_data_base.v1.0.0-debug12"

@Database(entities = [BibleVerse::class, PopularBibleVerse::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun bibleVerseDao(): BibleVerseDao
    abstract fun popularBibleVerseDao(): PopularBibleVerseDao

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
                        DATABASE_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    this.instance = instance
                }
                return instance
            }
        }

        fun getInstanceFromAsset(context: Context, databaseFilePath: String): AppDatabase {
            synchronized(this) {
                var instance = instance

                if (instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DATABASE_NAME)
                        .createFromAsset(databaseFilePath)
                        .fallbackToDestructiveMigration()
                        .build()
                    this.instance = instance
                }

                return instance
            }
        }
    }
}