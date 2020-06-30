package com.exodia.gdsk.gdsflicker.data.search

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [UserQuery::class], version = 5, exportSchema = false)
abstract class FlickrRoomDatabase : RoomDatabase() {

    abstract fun queryDao (): QueryDao

    companion object {
        @Volatile
        private var INSTANCE: FlickrRoomDatabase? = null

        fun getDatabase(context: Context): FlickrRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FlickrRoomDatabase::class.java,
                    "flickr_database")
                        .fallbackToDestructiveMigration()
                        .build()
                INSTANCE = instance
                instance
            }
        }
    }
}