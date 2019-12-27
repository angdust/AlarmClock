package com.example.alarmclock.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class AlarmEntitiy(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    var label: String,
    var hours: Int = 12,
    var minutes: Int = 0,
    var enabled: Boolean = true,
    var tone: String = "Default",
    var probability: Int = 100
)