package com.example.alarmclock.alarmservice

import android.app.IntentService
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.alarmclock.AlarmActivity
import com.example.alarmclock.R
import com.example.alarmclock.database.AlarmDatabase
import com.example.alarmclock.database.AlarmEntitiy


const val CHANNEL_ID = "ALARM_SERVICE_CHANNEL"
const val debug = "DEBUG"

class AlarmService : IntentService("alarm_service_for_al_clock") {
    override fun onHandleIntent(intent: Intent?) {
        val random = (1..100).random()
        val prob = intent?.getIntExtra(PROB_EXTRA, 100) ?: 100
        val id = intent?.getLongExtra(ID_EXTRA, 0L)!!
        val entitiy = AlarmDatabase.getDB(this).alarmDao.getById(id)
        Log.d(debug, "on handle")
        if (entitiy?.enabled == true) {
            Log.d(debug, "enabled")
            if (random <= prob) {
                Log.d(debug, "prob check")
                val fullScreenIntent = Intent(this, AlarmActivity::class.java)
                val fullScreenPendingIntent = PendingIntent.getActivity(
                    this, 0,
                    fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT
                )
                Log.d(debug, "Builder")
                val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_ok)
                    .setContentTitle("Alarm!!!")
                    .setContentText(intent.getStringExtra(LABEL_EXTRA) ?: "ALAAAARM")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setFullScreenIntent(fullScreenPendingIntent, true)
                Log.d(debug, "show")
                with(NotificationManagerCompat.from(this)) {
                    notify(intent.getLongExtra(ID_EXTRA, 0).toInt(), builder.build())
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d(debug, "destroy")
    }

}

private fun createAlarmIntent(context: Context?, alarmEntitiy: AlarmEntitiy) =
    Intent(context, AlarmService::class.java).apply {
        putExtra(ID_EXTRA, alarmEntitiy.id)
        putExtra(HOURS_EXTRA, alarmEntitiy.hours)
        putExtra(MINUTES_EXTRA, alarmEntitiy.minutes)
        putExtra(PROB_EXTRA, alarmEntitiy.probability)
        putExtra(LABEL_EXTRA, alarmEntitiy.label)
    }

fun makePending(context: Context?, alarmEntitiy: AlarmEntitiy): PendingIntent {
    val intent = createAlarmIntent(context, alarmEntitiy)
    return PendingIntent.getService(
        context,
        alarmEntitiy.id.toInt(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
}
