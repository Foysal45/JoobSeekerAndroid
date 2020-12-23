package com.bdjobs.app.BroadCastReceivers

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import com.bdjobs.app.Notification.NotificationHelper
import com.bdjobs.app.SessionManger.BdjobsUserSession
import timber.log.Timber
import java.util.*

class RestartBroadcastReceiver : BroadcastReceiver() {

    var NOTIFICATION_ID = "notification-id"
    var NOTIFICATION = "notification"

    lateinit var context: Context

    override fun onReceive(ctx: Context, intent: Intent?) {

        val session : BdjobsUserSession = BdjobsUserSession(ctx)

        context = ctx

        if (intent?.action == "android.intent.action.BOOT_COMPLETED" || intent?.action == "android.intent.action.LOCKED_BOOT_COMPLETED") {

            if (session.isLoggedIn!!){
                scheduleMorningNotification()
                scheduleNightNotification()
            }
        } else {
            Timber.d("called broadcast else")
        }
    }

    private fun scheduleMorningNotification() {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(context, MorningNotificationReceiver::class.java).apply {
                putExtra("type","morning")
            }.let {
                PendingIntent.getBroadcast(context, 0, it, 0)
            }

            val calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 8)
                set(Calendar.MINUTE, 0)
            }

            if (calendar.timeInMillis < System.currentTimeMillis()){
                calendar.add(Calendar.DATE,1)
            }

            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    1000 * 60 * 60 * 24,
                    alarmIntent
            )
        } catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun scheduleNightNotification() {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(context, NightNotificationReceiver::class.java).apply {
                putExtra("type","night")
            }.let {
                PendingIntent.getBroadcast(context, 1, it, 0)
            }

            val calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 20)
                set(Calendar.MINUTE, 0)
            }

            if (calendar.timeInMillis < System.currentTimeMillis()){
                calendar.add(Calendar.DATE,1)
            }

            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    1000 * 60 * 60 * 24,
                    alarmIntent
            )
        } catch (e:Exception){
            e.printStackTrace()
        }
    }
}