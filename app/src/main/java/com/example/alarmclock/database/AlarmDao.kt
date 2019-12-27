package com.example.alarmclock.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AlarmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(alarm: AlarmEntitiy)

    @Update
    fun update(alarm: AlarmEntitiy)

    @Query("SELECT * FROM AlarmEntitiy WHERE id = :key")
    fun getById(key: Long): AlarmEntitiy?

    @Delete
    fun delete(alarms: List<AlarmEntitiy>)


    @Query("SELECT * FROM AlarmEntitiy ORDER BY id ASC")
    fun getAlarms(): LiveData<List<AlarmEntitiy>>
}