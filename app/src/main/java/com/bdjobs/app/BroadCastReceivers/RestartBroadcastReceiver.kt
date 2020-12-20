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

        Timber.d("called broadcast")

        context = ctx

        if (intent?.action == "android.intent.action.BOOT_COMPLETED" || intent?.action == "android.intent.action.LOCKED_BOOT_COMPLETED") {

            if (session.isLoggedIn!!){

            }
                //scheduleNotification()

//            Toast.makeText(context, "Enjoy", Toast.LENGTH_LONG).show()
//            Toast.makeText(context, "Enjoy", Toast.LENGTH_LONG).show()
//            Toast.makeText(context, "Enjoy", Toast.LENGTH_LONG).show()
//            Toast.makeText(context, "Enjoy", Toast.LENGTH_LONG).show()
//            Toast.makeText(context, "Enjoy", Toast.LENGTH_LONG).show()
//            Toast.makeText(context, "Enjoy", Toast.LENGTH_LONG).show()
//            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            val notification: Notification? = intent.getParcelableExtra(NOTIFICATION)
//            val id = intent.getIntExtra(NOTIFICATION_ID, 0)
//            notificationManager.notify(id,notification)
        } else {
            Timber.d("called broadcast else")
            Toast.makeText(context, "Enjoy More", Toast.LENGTH_LONG).show()
//            NotificationHelper(context).mNotificationManager.notify(
//            )
        }
    }

//    @RequiresApi(Build.VERSION_CODES.M)
//    private fun scheduleNotification() {
//        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val alarmIntent = Intent(context, TestBroadcastReceiver::class.java).apply {
//            putExtra(TestBroadcastReceiver.serial, TestBroadcastReceiver.value.plus(1))
//        }.let {
//            PendingIntent.getBroadcast(context, 0, it, PendingIntent.FLAG_ONE_SHOT)
//        }
//
//        val calendar: Calendar = Calendar.getInstance().apply {
//            timeInMillis = System.currentTimeMillis()
//            set(Calendar.HOUR_OF_DAY, 7)
//            set(Calendar.MINUTE, 30)
//        }
//
//        alarmManager?.setRepeating(
//                AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                calendar.timeInMillis,
//                SystemClock.elapsedRealtime() + 60 * 1000,
//                alarmIntent
//        )
//    }

}