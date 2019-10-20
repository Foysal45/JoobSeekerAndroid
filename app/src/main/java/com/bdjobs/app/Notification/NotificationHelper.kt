package com.bdjobs.app.Notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bdjobs.app.InterviewInvitation.InterviewInvitationBaseActivity
import com.bdjobs.app.R

class NotificationHelper(context: Context) : ContextWrapper(context) {

    companion object {
        const val BDJOBS_CHANNEL = "bdjobs"
    }

    private val mNotificationManager: NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(context)
    }

    init {

        // Create the channel object with the unique ID FOLLOWERS_CHANNEL
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val followersChannel = NotificationChannel(
                    BDJOBS_CHANNEL,
                    getString(R.string.notification_channel_bdjobs),
                    NotificationManager.IMPORTANCE_HIGH)

            // Configure the channel's initial settings
            followersChannel.lightColor = Color.GREEN
            followersChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 500, 200, 400, 500)

            // Submit the notification channel object to the notification manager
            mNotificationManager.createNotificationChannel(followersChannel)
        }
    }

    fun getInterviewInvitationNotification(title: String, body: String, jobid: String, companyName: String, jobTitle: String, type : String): NotificationCompat.Builder {

        Log.d("rakib noti helper", "$jobTitle $jobid $companyName")

        val intent = Intent(this, InterviewInvitationBaseActivity::class.java)?.apply {
            putExtra("from", "notification")
            putExtra("jobid", jobid)
            putExtra("companyname", companyName)
            putExtra("jobtitle", jobTitle)
            putExtra("type", type)

        }
        val interviewInvitationPendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addParentStack(InterviewInvitationBaseActivity::class.java)
        stackBuilder.addNextIntent(intent)
        stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT)


        return NotificationCompat.Builder(applicationContext, BDJOBS_CHANNEL)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(smallIcon)
                .setAutoCancel(true)
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(body))
                .setContentIntent(interviewInvitationPendingIntent)

    }

    fun getSimpleNotification(title: String, body: String): NotificationCompat.Builder {
        return NotificationCompat.Builder(applicationContext, BDJOBS_CHANNEL)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(smallIcon)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
    }


    private val pendingIntent: PendingIntent
        get() {
            val intent = Intent(this, NotificationBaseActivity::class.java)
            val stackBuilder = TaskStackBuilder.create(this)
            stackBuilder.addParentStack(NotificationBaseActivity::class.java)
            stackBuilder.addNextIntent(intent)
            return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT)
        }

//    private val interviewInvitationPendingIntent: PendingIntent
//        get() {
//
//        }


    fun notify(id: Int, notification: NotificationCompat.Builder) {
        mNotificationManager.notify(id, notification.build())
    }

    private val smallIcon: Int
        get() = R.drawable.bdjobs_app_logo

}