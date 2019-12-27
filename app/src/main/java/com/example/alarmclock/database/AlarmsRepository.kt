package com.example.alarmclock.database

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AlarmsRepository(context: Context) {

    private val db = AlarmDatabase.getDB(context)

    val alarms = db.alarmDao.getAlarms()

    suspend fun insert(alarmEntitiy: AlarmEntitiy) {
        withContext(Dispatchers.IO) {
            db.alarmDao.insert(alarmEntitiy)
        }
    }

    suspend fun delete(alarmEntitiy: AlarmEntitiy) {
        withContext(Dispatchers.IO) {
            db.alarmDao.delete(listOf(alarmEntitiy))
        }
    }

    suspend fun getById(key: Long) =
        withContext(Dispatchers.IO) {
            return@withContext db.alarmDao.getById(key)
        }

    suspend fun update(alarmEntitiy: AlarmEntitiy) {
        withContext(Dispatchers.IO) {
            db.alarmDao.update(alarmEntitiy)
        }
    }

}


