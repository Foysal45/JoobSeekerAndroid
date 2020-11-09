package com.bdjobs.app.BroadCastReceivers

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bdjobs.app.R
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class TestBroadcastReceiver : BroadcastReceiver() {

    companion object {
        var serial = "serial"
        var value = 0
    }

    lateinit var ctx: Context

    override fun onReceive(context: Context, intent: Intent?) {
        Timber.d("called test receiver")
        ctx = context

        val data = intent?.getIntExtra(serial, value)

        createNotificationChannel()
        showNotification(data)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            scheduleNotification()
    }

    private fun showNotification(data: Int?) {
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
        val test: String = sdf.format(cal.time)
        var builder = NotificationCompat.Builder(ctx, "CHANNEL_ID")
                .setSmallIcon(R.drawable.bdjobs_app_logo)
                .setContentTitle("Test")
                .setContentText("$test serial $data")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(ctx)) {
            notify(100, builder.build())
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = ("Test channel")
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("CHANNEL_ID", name, importance)
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun scheduleNotification() {
        val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(ctx, TestBroadcastReceiver::class.java).apply {
            putExtra(serial, value.plus(1))
        }.let {
            PendingIntent.getBroadcast(ctx, 1, it, PendingIntent.FLAG_ONE_SHOT)
        }

        alarmManager?.setExactAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 60 * 1000,
                alarmIntent
        )
    }


}