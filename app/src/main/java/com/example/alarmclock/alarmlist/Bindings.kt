package com.example.alarmclock.alarmlist

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.alarmclock.database.AlarmEntitiy


@BindingAdapter("setTime")
fun TextView.time(alarm: AlarmEntitiy) {
    text = String.format("%02d:%02d", alarm.hours, alarm.minutes)
}


@BindingAdapter("setProb")
fun TextView.prob(alarm: AlarmEntitiy) {
    text = alarm.probability.toString()+ "%"
}
