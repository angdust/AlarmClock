package com.example.alarmclock.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    entities = [AlarmEntitiy::class],
    version = 1,
    exportSchema = false
)
abstract class AlarmDatabase : RoomDatabase() {

    abstract val alarmDao: AlarmDao


    companion object {
        private lateinit var db: AlarmDatabase

        fun getDB(context: Context): AlarmDatabase {
            synchronized(AlarmDatabase::class.java) {
                if (!Companion::db.isInitialized) {
                    db = Room.databaseBuilder(
                        context,
                        AlarmDatabase::class.java,
                        "main_db"
                    ).build()
                }
            }
            return db
        }
    }
}