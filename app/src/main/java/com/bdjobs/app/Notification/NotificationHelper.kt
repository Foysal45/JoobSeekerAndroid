package com.bdjobs.app.Notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES.O
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.bdjobs.app.Employers.EmployersBaseActivity
import com.bdjobs.app.InterviewInvitation.InterviewInvitationBaseActivity
import com.bdjobs.app.Jobs.JobBaseActivity
import com.bdjobs.app.LoggedInUserLanding.MainLandingActivity
import com.bdjobs.app.R
import com.bdjobs.app.utilities.Constants
import com.bdjobs.app.liveInterview.LiveInterviewActivity
import com.bdjobs.app.sms.SmsBaseActivity
import com.bdjobs.app.videoInterview.VideoInterviewActivity
import java.io.IOException
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class NotificationHelper(val context: Context) : ContextWrapper(context) {

    companion object {
        const val TAG = "NotificationHelper"
        const val GENERAL_CHANNEL = "general"
        const val INTERVIEW_INVITATION_CHANNEL = "interview_invitation"
        const val VIDEO_INTERVIEW_CHANNEL = "video_interview"
        const val LIVE_INTERVIEW_CHANNEL = "live_interview"
        const val CV_VIEWED_CHANNEL = "cv_viewed"
        const val MATCHED_JOB_CHANNEL = "matched_job"
        const val MESSAGE_CHANNEL = "message"
        const val SMS_CHANNEL = "sms"
        const val ALERT_CHANNEL = "alert"
    }

    val mNotificationManager: NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(context)
    }

    init {

        // Create general notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createChannel(GENERAL_CHANNEL, getString(R.string.notification_channel_general))
                createChannel(
                    INTERVIEW_INVITATION_CHANNEL,
                    getString(R.string.notification_channel_interview_invitation)
                )
                createChannel(
                    VIDEO_INTERVIEW_CHANNEL,
                    getString(R.string.notification_channel_video_interview)
                )
                createChannel(
                    LIVE_INTERVIEW_CHANNEL,
                    getString(R.string.notification_channel_live_interview)
                )
                createChannel(CV_VIEWED_CHANNEL, getString(R.string.notification_channel_cv_viewed))
                createChannel(
                    MATCHED_JOB_CHANNEL,
                    getString(R.string.notification_channel_matched_job)
                )
                createChannel(MESSAGE_CHANNEL, getString(R.string.notification_channel_message))
                createChannel(SMS_CHANNEL, getString(R.string.notification_channel_sms))
                createChannel(ALERT_CHANNEL, getString(R.string.notification_channel_alert))
            }
        }
    }


    fun prepareNotification(
        title: String? = "",
        body: String? = "",
        jobid: String? = "",
        companyName: String? = "",
        jobTitle: String? = "",
        type: String? = "",
        link: String? = "",
        imageLink: String? = "",
        nId: String? = "",
        lanType: String? = "",
        deadlineDB: String? = "",
        activityName: String? = ""
    ): NotificationCompat.Builder {

        //Log.d("rakib noti helper", "$jobTitle $jobid $companyName")

        when (type) {

            Constants.NOTIFICATION_TYPE_INTERVIEW_INVITATION -> {

                val intent = Intent(this, InterviewInvitationBaseActivity::class.java).apply {
                    putExtra("from", "notification")
                    putExtra("jobid", jobid)
                    putExtra("companyname", companyName)
                    putExtra("jobtitle", jobTitle)
                    putExtra("type", type)
                    putExtra("nid", nId)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }

                val interviewInvitationPendingIntent: PendingIntent =
                    PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                return NotificationCompat.Builder(applicationContext, INTERVIEW_INVITATION_CHANNEL)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(smallIcon)
                    .setAutoCancel(true)
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText(body)
                    )
                    .setContentIntent(interviewInvitationPendingIntent)
                    .setColor(ContextCompat.getColor(context, R.color.colorBdjobsMajenta))
            }

            Constants.NOTIFICATION_TYPE_VIDEO_INTERVIEW -> {

//                val intent = Intent(this, InterviewInvitationBaseActivity::class.java)?.apply {
//                    putExtra("from", "notification")
//                    putExtra("jobid", jobid)
//                    putExtra("companyname", companyName)
//                    putExtra("jobtitle", jobTitle)
//                    putExtra("type", type)
//                    putExtra("nid", nId)
//                    putExtra("videoUrl",link)
//                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                }

                val intent = Intent(this, VideoInterviewActivity::class.java)

                val videoInterviewPendingIntent: PendingIntent =
                    PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                return NotificationCompat.Builder(applicationContext, VIDEO_INTERVIEW_CHANNEL)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(smallIcon)
                    .setAutoCancel(true)
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText(body)
                    )
                    .setContentIntent(videoInterviewPendingIntent)
                    .setColor(ContextCompat.getColor(context, R.color.colorBdjobsMajenta))
            }

            Constants.NOTIFICATION_TYPE_LIVE_INTERVIEW -> {

                val intent = Intent(this, LiveInterviewActivity::class.java).apply {
                    putExtra("from", "notification")
                    putExtra("jobId", jobid)
                    putExtra("jobTitle", jobTitle)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }

                val liveInterviewInvitationPendingIntent: PendingIntent =
                    PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                return NotificationCompat.Builder(applicationContext, LIVE_INTERVIEW_CHANNEL)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(smallIcon)
                    .setAutoCancel(true)
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText(body)
                    )
                    .setContentIntent(liveInterviewInvitationPendingIntent)
                    .setColor(ContextCompat.getColor(context, R.color.colorBdjobsMajenta))
            }

            Constants.NOTIFICATION_TYPE_CV_VIEWED -> {

                val intent = Intent(this, EmployersBaseActivity::class.java).apply {
                    putExtra("from", "notification")
                    putExtra("jobId", jobid)
                    putExtra("nid", nId)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }

                val pendingIntent: PendingIntent =
                    PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                return NotificationCompat.Builder(applicationContext, CV_VIEWED_CHANNEL)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(smallIcon)
                    .setAutoCancel(true)
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText(body)
                    )
                    .setContentIntent(pendingIntent)
                    .setColor(ContextCompat.getColor(context, R.color.colorBdjobsMajenta))

            }

            Constants.NOTIFICATION_TYPE_MATCHED_JOB -> {

                val jobids = ArrayList<String>()
                val lns = ArrayList<String>()
                val deadline = ArrayList<String>()

                try {
                    deadline.add("")
                    jobids.add(jobid!!)
                    lns.add(lanType!!)

                    //Log.d("rakib mateched job", "${deadline[0]} ${lns[0]} ${jobids[0]}")

                } catch (e: Exception) {
                }

                val intent = Intent(this, JobBaseActivity::class.java).apply {
                    putExtra("from", "notification")
                    putExtra("jobids", jobids)
                    putExtra("lns", lns)
                    putExtra("position", 0)
                    putExtra("deadline", deadline)
                    putExtra("nid", nId)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }

                val pendingIntent: PendingIntent =
                    PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                return NotificationCompat.Builder(applicationContext, MATCHED_JOB_CHANNEL)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(smallIcon)
                    .setAutoCancel(true)
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText(body)
                    )
                    .setContentIntent(pendingIntent)
                    .setColor(ContextCompat.getColor(context, R.color.colorBdjobsMajenta))

            }

            Constants.NOTIFICATION_TYPE_SMS -> {
                val intent = Intent(this, SmsBaseActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }

                val smsPendingIntent: PendingIntent =
                    PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                return NotificationCompat.Builder(applicationContext, SMS_CHANNEL)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(smallIcon)
                    .setAutoCancel(true)
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText(body)
                    )
                    .setContentIntent(smsPendingIntent)
                    .setColor(ContextCompat.getColor(context, R.color.colorBdjobsMajenta))
            }

            Constants.NOTIFICATION_TYPE_PROMOTIONAL_MESSAGE -> {

                var intent = Intent()

                try {
                    val className = Class.forName(activityName!!)
                    intent = Intent(this, className)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                } catch (e: Exception) {
                    if (!link.isNullOrBlank()) {
                        try {
                            val formattedUrl =
                                if (!link.startsWith("http://") && !link.startsWith("https://")) {
                                    "http://$link"
                                } else link
                            intent = Intent(Intent.ACTION_VIEW, Uri.parse(formattedUrl))
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        } catch (e: Exception) {
                        }
                    }
                }


                val pendingIntent: PendingIntent =
                    PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)


                return if (!imageLink.isNullOrEmpty()) {
                    showNotificationWithBigPicture(imageLink, body, title, pendingIntent)
                } else {
                    showBigTextNotification(body, title, pendingIntent)
                }
            }

            Constants.NOTIFICATION_TYPE_ALERT_NOTIFICATION -> {

                val intent = Intent(this, MainLandingActivity::class.java).apply {
                    putExtra("from", "notification")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }


                val pendingIntent: PendingIntent =
                    PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)


                return NotificationCompat.Builder(applicationContext, ALERT_CHANNEL)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(smallIcon)
                    .setAutoCancel(false)
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText(body)
                    )
                    .setContentIntent(pendingIntent)

            }

            else -> {

                val intent = Intent(this, MainLandingActivity::class.java).apply {
                }

                val pendingIntent: PendingIntent =
                    PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                val stackBuilder = TaskStackBuilder.create(this)
                stackBuilder.addParentStack(InterviewInvitationBaseActivity::class.java)
                stackBuilder.addNextIntent(intent)
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT)

                return NotificationCompat.Builder(applicationContext, GENERAL_CHANNEL)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(smallIcon)
                    .setAutoCancel(true)
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText(body)
                    )
                    .setContentIntent(pendingIntent)
                    .setColor(ContextCompat.getColor(context, R.color.colorBdjobsMajenta))
            }
        }
    }

    private fun showBigTextNotification(
        body: String?,
        title: String?,
        pendingIntent: PendingIntent
    ): NotificationCompat.Builder {

        return NotificationCompat.Builder(applicationContext, MESSAGE_CHANNEL)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(smallIcon)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(body)
            )
            .setColor(ContextCompat.getColor(context, R.color.colorBdjobsMajenta))

    }

    private fun showNotificationWithBigPicture(
        imageLink: String,
        body: String?,
        title: String?,
        pendingIntent: PendingIntent
    ): NotificationCompat.Builder {
        val bigPictureStyle = NotificationCompat.BigPictureStyle()
        bigPictureStyle.setBigContentTitle(title)
        bigPictureStyle.setSummaryText(body)
        bigPictureStyle.bigPicture(getBitmapFromURL(imageLink))

        return NotificationCompat.Builder(applicationContext, MESSAGE_CHANNEL)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(smallIcon)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setStyle(bigPictureStyle)
            .setColor(ContextCompat.getColor(context, R.color.colorBdjobsMajenta))

    }

    @RequiresApi(O)
    private fun createChannel(id: String, name: CharSequence) {
        val channel = NotificationChannel(
            id,
            name,
            NotificationManager.IMPORTANCE_HIGH
        )
        // Configure the channel's initial settings
        channel.apply {
            lightColor = Color.GREEN
            vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 500, 200, 400, 500)
        }
        // Submit the notification channel object to the notification manager
        mNotificationManager.createNotificationChannel(channel)
    }

    private val pendingIntent: PendingIntent
        get() {
            val intent = Intent(this, NotificationBaseActivity::class.java)
            val stackBuilder = TaskStackBuilder.create(this)
            stackBuilder.addParentStack(NotificationBaseActivity::class.java)
            stackBuilder.addNextIntent(intent)
            return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT)
        }

    fun notify(id: Int, notification: NotificationCompat.Builder) {
        mNotificationManager.notify(id, notification.build())
    }


    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     */
    private fun getBitmapFromURL(strURL: String): Bitmap? {
        return try {
            val url = URL(strURL)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }

    }

    private val smallIcon: Int
        get() = R.drawable.bdjobs_app_logo
}