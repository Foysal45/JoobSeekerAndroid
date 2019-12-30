package com.bdjobs.app.Notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.bdjobs.app.Employers.EmployersBaseActivity
import com.bdjobs.app.InterviewInvitation.InterviewInvitationBaseActivity
import com.bdjobs.app.Jobs.JobBaseActivity
import com.bdjobs.app.LoggedInUserLanding.MainLandingActivity
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.Constants
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception

class NotificationHelper(val context: Context) : ContextWrapper(context) {

    companion object {
        const val TAG = "NotificationHelper"
        const val GENERAL_CHANNEL = "general"
        const val INTERVIEW_INVITATION_CHANNEL = "interview_invitation"
        const val CV_VIEWED_CHANNEL = "cv_viewed"
        const val MATCHED_JOB_CHANNEL = "matched_job"
        const val MESSAGE_CHANNEL = "message"
    }

    val mNotificationManager: NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(context)
    }

    init {

        // Create general notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val generalChannel = NotificationChannel(
                    GENERAL_CHANNEL,
                    getString(R.string.notification_channel_general),
                    NotificationManager.IMPORTANCE_HIGH)

            // Configure the channel's initial settings
            generalChannel.lightColor = Color.GREEN
            generalChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 500, 200, 400, 500)

            // Submit the notification channel object to the notification manager
            mNotificationManager.createNotificationChannel(generalChannel)

            // Create interview invitation notification channel
            val interviewInvitationChannel = NotificationChannel(
                    INTERVIEW_INVITATION_CHANNEL,
                    getString(R.string.notification_channel_interview_invitation),
                    NotificationManager.IMPORTANCE_HIGH)

            // Configure the channel's initial settings
            generalChannel.lightColor = Color.GREEN
            generalChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 500, 200, 400, 500)

            // Submit the notification channel object to the notification manager
            mNotificationManager.createNotificationChannel(interviewInvitationChannel)

            // Create cv viewed notification channel
            val cvViewedChannel = NotificationChannel(
                    CV_VIEWED_CHANNEL,
                    getString(R.string.notification_channel_cv_viewed),
                    NotificationManager.IMPORTANCE_HIGH)

            // Configure the channel's initial settings
            generalChannel.lightColor = Color.GREEN
            generalChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 500, 200, 400, 500)

            // Submit the notification channel object to the notification manager
            mNotificationManager.createNotificationChannel(cvViewedChannel)

            // Create message notification channel
            val messageChannel = NotificationChannel(
                    MESSAGE_CHANNEL,
                    getString(R.string.notification_channel_message),
                    NotificationManager.IMPORTANCE_HIGH)

            // Configure the channel's initial settings
            generalChannel.lightColor = Color.GREEN
            generalChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 500, 200, 400, 500)

            // Submit the notification channel object to the notification manager
            mNotificationManager.createNotificationChannel(messageChannel)

            // Create matched job notification channel
            val matchedJobChannel = NotificationChannel(
                    MATCHED_JOB_CHANNEL,
                    getString(R.string.notification_channel_matched_job),
                    NotificationManager.IMPORTANCE_HIGH)

            // Configure the channel's initial settings
            generalChannel.lightColor = Color.GREEN
            generalChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 500, 200, 400, 500)

            // Submit the notification channel object to the notification manager
            mNotificationManager.createNotificationChannel(matchedJobChannel)

        }

    }


    fun prepareNotification(title: String?, body: String?, jobid: String?, companyName: String?, jobTitle: String?, type: String?, link: String?, imageLink: String?, nId: String?, lanType:String?, deadlineDB : String?): NotificationCompat.Builder {

        //Log.d("rakib noti helper", "$jobTitle $jobid $companyName")

        when (type) {

            Constants.NOTIFICATION_TYPE_INTERVIEW_INVITATION -> {

                val intent = Intent(this, InterviewInvitationBaseActivity::class.java)?.apply {
                    putExtra("from", "notification")
                    putExtra("jobid", jobid)
                    putExtra("companyname", companyName)
                    putExtra("jobtitle", jobTitle)
                    putExtra("type", type)
                    putExtra("nid", nId)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }

                val interviewInvitationPendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                return NotificationCompat.Builder(applicationContext, INTERVIEW_INVITATION_CHANNEL)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setSmallIcon(smallIcon)
                        .setAutoCancel(true)
                        .setStyle(NotificationCompat.BigTextStyle()
                                .bigText(body))
                        .setContentIntent(interviewInvitationPendingIntent)
                        .setColor(ContextCompat.getColor(context,R.color.colorBdjobsMajenta))
            }

            Constants.NOTIFICATION_TYPE_CV_VIEWED -> {

                val intent = Intent(this, EmployersBaseActivity::class.java)?.apply {
                    putExtra("from", "notification")
                    putExtra("jobId", jobid)
                    putExtra("nid", nId)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }

                val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                return NotificationCompat.Builder(applicationContext, CV_VIEWED_CHANNEL)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setSmallIcon(smallIcon)
                        .setAutoCancel(true)
                        .setStyle(NotificationCompat.BigTextStyle()
                                .bigText(body))
                        .setContentIntent(pendingIntent)
                        .setColor(ContextCompat.getColor(context,R.color.colorBdjobsMajenta))

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

                val intent = Intent(this, JobBaseActivity::class.java)?.apply {
                    putExtra("from", "notification")
                    putExtra("jobids", jobids)
                    putExtra("lns", lns)
                    putExtra("position", 0)
                    putExtra("deadline", deadline)
                    putExtra("nid", nId)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }

                val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                return NotificationCompat.Builder(applicationContext, MATCHED_JOB_CHANNEL)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setSmallIcon(smallIcon)
                        .setAutoCancel(true)
                        .setStyle(NotificationCompat.BigTextStyle()
                                .bigText(body))
                        .setContentIntent(pendingIntent)
                        .setColor(ContextCompat.getColor(context,R.color.colorBdjobsMajenta))

            }


            Constants.NOTIFICATION_TYPE_PROMOTIONAL_MESSAGE -> {

                val intent = Intent(this, NotificationBaseActivity::class.java)?.apply {
                    putExtra("from", "notification")
                    putExtra("id", jobid)
                    putExtra("nid", nId)
                    putExtra("seen", true)

                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }


                val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

//                var imageBitmap: Bitmap? = null
//
//                var target = object : Target {
//                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
//                    }
//
//                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
//                    }
//
//                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
//                        imageBitmap = bitmap
//                        //Log.d("rakib inside", "$bitmap")
//                    }
//
//                }
//
//                Picasso.get().load(imageLink).into(target)

//                //Log.d("rakib outside", "$imageBitmap $imageLink")

                return NotificationCompat.Builder(applicationContext, MESSAGE_CHANNEL)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setSmallIcon(smallIcon)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setStyle(NotificationCompat.BigTextStyle()
                                .bigText(body))
                        .setColor(ContextCompat.getColor(context,R.color.colorBdjobsMajenta))

            }

            else -> {

                val intent = Intent(this, MainLandingActivity::class.java)?.apply {
                }

                val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                val stackBuilder = TaskStackBuilder.create(this)
                stackBuilder.addParentStack(InterviewInvitationBaseActivity::class.java)
                stackBuilder.addNextIntent(intent)
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT)

                return NotificationCompat.Builder(applicationContext, GENERAL_CHANNEL)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setSmallIcon(smallIcon)
                        .setAutoCancel(true)
                        .setStyle(NotificationCompat.BigTextStyle()
                                .bigText(body))
                        .setContentIntent(pendingIntent)
                        .setColor(ContextCompat.getColor(context,R.color.colorBdjobsMajenta))

            }
        }


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

    private val smallIcon: Int
        get() = R.drawable.bdjobs_app_logo

}