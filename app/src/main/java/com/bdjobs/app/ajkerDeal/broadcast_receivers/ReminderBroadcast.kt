package com.bdjobs.app.ajkerDeal.broadcast_receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.bdjobs.app.R
import com.bdjobs.app.ajkerDeal.ui.home.HomeActivity
import com.bdjobs.app.ajkerDeal.utilities.DigitConverter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class ReminderBroadcast : BroadcastReceiver() {

    private val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US)

    override fun onReceive(context: Context?, intent: Intent?) {

        Timber.d("ReminderBroadcastDebug onReceive called")
        if (context == null || intent == null) return
        Timber.d("ReminderBroadcastDebug onReceive context,intent not null")

        val liveId = intent.getIntExtra("liveId", 0)
        val liveDate = intent.getStringExtra("liveDate") ?: ""
        val fromTime = intent.getStringExtra("fromTime")
        val videoTitle = intent.getStringExtra("videoTitle") ?: "Live"
        val coverPhoto = intent.getStringExtra("coverPhoto")


        // Create intent, put data
        val activityIntent = Intent(context, HomeActivity::class.java).apply {
            putExtra("liveId", liveId)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, System.currentTimeMillis().toInt(), activityIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val title = videoTitle
        var body = "লাইভ শুরু হচ্ছে "
        try {
            val formatDate = "$liveDate $fromTime"
            val date = sdf.parse(formatDate)
            if (date != null) {
                body += DigitConverter.relativeWeekday(date)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val icon: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) R.drawable.ic_notification else R.mipmap.ic_launcher
        val notificationBuilder = NotificationCompat.Builder(context, context.getString(R.string.notification_channel_id_reminder))
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setColor(ContextCompat.getColor(context, R.color.aDheaderColor))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Create Notification Channel
        // Create Notification Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            val name: CharSequence = context.getString(R.string.notification_channel_name_reminder)
            val description: String = context.getString(R.string.notification_channel_description_reminder)
            val channelID: String = context.getString(R.string.notification_channel_id_reminder)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
            val channel = NotificationChannel(channelID, name, importance)
            channel.description = description
            channel.setShowBadge(true)
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.setSound(soundUri, audioAttributes)
            // Register the channel_PUSH with the system
            notificationManager.createNotificationChannel(channel)
        }
        val notificationId = 20211

        Glide.with(context)
            .asBitmap()
            .load(coverPhoto)
            .listener(object : RequestListener<Bitmap?> {
                override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Bitmap?>, isFirstResource: Boolean): Boolean {
                    notificationManager.notify(notificationId, notificationBuilder.build())
                    return false
                }

                override fun onResourceReady(resource: Bitmap?, model: Any, target: Target<Bitmap?>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                    notificationBuilder.setLargeIcon(resource)
                    notificationManager.notify(notificationId, notificationBuilder.build())
                    return false
                }
            })
            .submit(256, 256)

        //notificationManager.notify(notificationId, notificationBuilder.build())
    }
}