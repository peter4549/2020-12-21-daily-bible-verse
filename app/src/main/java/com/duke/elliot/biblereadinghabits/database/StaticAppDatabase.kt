package com.duke.elliot.biblereadinghabits.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

const val STATIC_APP_DATABASE_NAME = "com.duke.elliot.bible_reading_habits.static_app_data_base.v1.0.1-debug3"

@Database(entities = [BibleVerse::class, PopularBibleVerse::class], version = 1)
abstract class StaticAppDatabase: RoomDatabase() {
    abstract fun bibleVerseDao(): BibleVerseDao
    abstract fun popularBibleVerseDao(): PopularBibleVerseDao

    companion object {
        @Volatile
        private var instance: StaticAppDatabase? = null

        fun getInstance(context: Context): StaticAppDatabase {
            synchronized(this) {
                var instance = instance

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        StaticAppDatabase::class.java,
                        STATIC_APP_DATABASE_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    this.instance = instance
                }
                return instance
            }
        }

        fun getInstanceFromAsset(context: Context, databaseFilePath: String): StaticAppDatabase {
            synchronized(this) {
                var instance = instance

                if (instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext, StaticAppDatabase::class.java, STATIC_APP_DATABASE_NAME)
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