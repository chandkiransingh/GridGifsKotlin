package com.ck.gridgifskotlin.MyClasses

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ImagesDataOffline::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun imgDataDao(): ImageDataDao?

    companion object {
        private var INSTANCE: AppDatabase? = null
        fun getDbInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "spyneDb"
                ).allowMainThreadQueries().build()
            }
            return INSTANCE
        }
    }
}