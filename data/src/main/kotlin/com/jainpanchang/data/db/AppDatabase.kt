package com.jainpanchang.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        SavedCityEntity::class,
        MyEventEntity::class,
        NiyamRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedCityDao(): SavedCityDao
    abstract fun myEventDao(): MyEventDao
    abstract fun niyamRecordDao(): NiyamRecordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "jain_panchang.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
