package com.bdjobs.app.BroadCastReceivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.bdjobs.app.InterviewInvitation.InterviewInvitationBaseActivity
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.utilities.Constants
import com.bdjobs.app.utilities.Constants.Companion.NOTIFICATION_TYPE_INTERVIEW_INVITATION
import com.bdjobs.app.databases.internal.*
import com.bdjobs.app.liveInterview.LiveInterviewActivity
import com.bdjobs.app.videoInterview.VideoInterviewActivity
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class MorningNotificationReceiver : BroadcastReceiver() {

    lateinit var ctx: Context
    lateinit var bdjobsUserSession: BdjobsUserSession
    lateinit var type: String

    override fun onReceive(context: Context, intent: Intent?) {

        Timber.d("called morning receiver")

        ctx = context

        type = intent?.getStringExtra("type")!!

        Timber.d("called $type")

        bdjobsUserSession = BdjobsUserSession(ctx)

        if (bdjobsUserSession.isLoggedIn!!) {
            createNotificationChannel()
            showMorningNotificationForLiveInterview()
            showMorningNotificationForGeneralInterview()
            showMorningNotificationForVideoInterview()
        }
    }

    private fun showMorningNotificationForLiveInterview() {
        val cal = Calendar.getInstance()
        val today = cal.time

        cal.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrow = cal.time

        doAsync {
            val totalInvitations = BdjobsDB.getInstance(ctx).liveInvitationDao().getTodaysInvitation(today, tomorrow)
            uiThread {
                Timber.d("morning live ${totalInvitations.size}")

                for (i in 0..totalInvitations.size.minus(1)) {

                    val intent = Intent(ctx, LiveInterviewActivity::class.java).apply {
                        putExtra("from", "notification")
                        putExtra("jobId", totalInvitations[i].jobId)
                        putExtra("jobTitle", totalInvitations[i].jobTitle)
                    }

                    val pendingIntent: PendingIntent = PendingIntent.getActivity(ctx, i.plus(100), intent, PendingIntent.FLAG_ONE_SHOT)

                    var builder = NotificationCompat.Builder(ctx, "CHANNEL_ID")
                            .setSmallIcon(R.drawable.bdjobs_app_logo)
                            .setContentTitle("Live Interview")
                            .setContentIntent(pendingIntent)
                            .setGroup("500")
                            .setAutoCancel(true)
                            .setStyle(NotificationCompat.BigTextStyle().bigText("Today you have a Live Interview with ${totalInvitations[i].companyName} at ${getTimeAsAMPM(totalInvitations[i].liveInterviewTime.toString())}"))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setColor(ContextCompat.getColor(ctx, R.color.colorBdjobsMajenta))
                    with(NotificationManagerCompat.from(ctx)) {
                        notify(i.plus(100), builder.build())
                    }
                    insertNotificationInToDatabase(totalInvitations[i])
                }
            }
        }
    }

    private fun showMorningNotificationForGeneralInterview() {
        val cal = Calendar.getInstance()
        val today = cal.time

        cal.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrow = cal.time

        Timber.d("tomorrow $tomorrow")

        doAsync {
            val totalInvitations = BdjobsDB.getInstance(ctx).jobInvitationDao().getTodaysInvitation(today, tomorrow)
            uiThread {
                Timber.d("morning general ${totalInvitations.size}")

                for (i in 0..totalInvitations.size.minus(1)) {

                    val intent = Intent(ctx, InterviewInvitationBaseActivity::class.java).apply {

                        putExtra("from", "notification")
                        putExtra("jobid", totalInvitations[i].jobId)
                        putExtra("nid", totalInvitations[i].jobId)
                        putExtra("companyname", totalInvitations[i].companyName)
                        putExtra("jobtitle", totalInvitations[i].jobTitle)
                        putExtra("type", NOTIFICATION_TYPE_INTERVIEW_INVITATION)
                    }

                    val pendingIntent: PendingIntent = PendingIntent.getActivity(ctx, i.plus(200), intent, PendingIntent.FLAG_ONE_SHOT)

                    var builder = NotificationCompat.Builder(ctx, "CHANNEL_ID")
                            .setSmallIcon(R.drawable.bdjobs_app_logo)
                            .setContentTitle("General Interview")
                            .setContentIntent(pendingIntent)
                            .setGroup("500")
                            .setAutoCancel(true)
                            .setStyle(NotificationCompat.BigTextStyle().bigText("Today you have an interview with ${totalInvitations[i].companyName} at ${getTimeAsAMPM(totalInvitations[i].interviewTimeString.toString())}"))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setColor(ContextCompat.getColor(ctx, R.color.colorBdjobsMajenta))
                    with(NotificationManagerCompat.from(ctx)) {
                        notify(i.plus(200), builder.build())
                    }
                    insertNotificationInToDatabase(totalInvitations[i])
                }
            }
        }
    }

    private fun showMorningNotificationForVideoInterview() {
        val cal = Calendar.getInstance()
        val today = cal.time

        cal.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrow = cal.time

        doAsync {
            val totalInvitations = BdjobsDB.getInstance(ctx).videoInvitationDao().getTodaysInvitation(today, tomorrow)
            uiThread {
                Timber.d("morning video ${totalInvitations.size}")

                for (i in 0..totalInvitations.size.minus(1)) {

                    val intent = Intent(ctx, VideoInterviewActivity::class.java).apply {
                        putExtra("from", "notification")
                        putExtra("jobId", totalInvitations[i].jobId)
                        putExtra("jobTitle", totalInvitations[i].jobTitle)
                    }

                    val pendingIntent: PendingIntent = PendingIntent.getActivity(ctx, i.plus(300), intent, PendingIntent.FLAG_ONE_SHOT)

                    var builder = NotificationCompat.Builder(ctx, "CHANNEL_ID")
                            .setSmallIcon(R.drawable.bdjobs_app_logo)
                            .setContentTitle("Video Interview")
                            .setContentIntent(pendingIntent)
                            .setGroup("500")
                            .setStyle(NotificationCompat.BigTextStyle().bigText("Submit your recorded Video Interview by before 12 at night"))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setColor(ContextCompat.getColor(ctx, R.color.colorBdjobsMajenta))
                    with(NotificationManagerCompat.from(ctx)) {
                        notify(i.plus(300), builder.build())
                    }
                    insertNotificationInToDatabase(totalInvitations[i])
//                }
                }
            }
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = ("Local channel")
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("CHANNEL_ID", name, importance)
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun getTimeAsAMPM(time: String): String {
        if (time != "") {
            try {
                val dateFormatter = SimpleDateFormat("HH:mm:ss")
                val date: Date = dateFormatter.parse(time)

                // Get time from date
                val timeFormatter = SimpleDateFormat("h:mm a")
                return timeFormatter.format(date)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return time
    }

    private fun insertNotificationInToDatabase(data: Any) {
        val bdjobsInternalDB = BdjobsDB.getInstance(ctx)
        val date: Date? = Date()

        when (data) {
            is LiveInvitation -> {
                val notificationText = "Today you have a Live Interview with ${data.companyName} at ${getTimeAsAMPM(data.liveInterviewTime.toString())}"
                doAsync {
                    bdjobsInternalDB.notificationDao().insertNotification(Notification(type = "li", serverId = data.jobId, seen = false, arrivalTime = date, seenTime = date, payload = "", imageLink = "", link = "", isDeleted = false, jobTitle = data.jobTitle, title = "", body = notificationText, companyName = data.companyName, notificationId = "", lanType = "", deadline = data.liveInterviewDateString))
                    uiThread {
                        val intent = Intent(Constants.BROADCAST_DATABASE_UPDATE_JOB)
                        intent.putExtra("notification", "insertOrUpdateNotification")
                        ctx.sendBroadcast(intent)
                    }
                }
            }
            is JobInvitation -> {
                val notificationText = "Today you have an interview with ${data.companyName} at ${getTimeAsAMPM(data.interviewTimeString.toString())}"
                doAsync {
                    bdjobsInternalDB.notificationDao().insertNotification(Notification(type = "ii", serverId = data.jobId, seen = false, arrivalTime = date, seenTime = date, payload = "", imageLink = "", link = "", isDeleted = false, jobTitle = data.jobTitle, title = "", body = notificationText, companyName = data.companyName, notificationId = "", lanType = "", deadline = data.interviewDateString))
                    uiThread {
                        val intent = Intent(Constants.BROADCAST_DATABASE_UPDATE_JOB)
                        intent.putExtra("notification", "insertOrUpdateNotification")
                        ctx.sendBroadcast(intent)
                    }
                }
            }
            is VideoInvitation -> {
                try {
                    val simpleDateFormat = SimpleDateFormat("d MMM yyyy", Locale.ENGLISH)
                    val deadlineString = simpleDateFormat.format(data.deadline)
                    val notificationText = "Submit your recorded Video Interview by before 12 at night, $deadlineString"
                    doAsync {
                        bdjobsInternalDB.notificationDao().insertNotification(Notification(type = "vi", serverId = data.jobId, seen = false, arrivalTime = date, seenTime = date, payload = "", imageLink = "", link = "", isDeleted = false, jobTitle = data.jobTitle, title = "", body = notificationText, companyName = data.companyName, notificationId = "", lanType = "", deadline = data.dateStringForSubmission.toString()))
                        uiThread {
                            val intent = Intent(Constants.BROADCAST_DATABASE_UPDATE_JOB)
                            intent.putExtra("notification", "insertOrUpdateNotification")
                            ctx.sendBroadcast(intent)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}