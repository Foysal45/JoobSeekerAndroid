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
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.databases.internal.BdjobsDB
import com.bdjobs.app.databases.internal.LiveInvitation
import com.bdjobs.app.databases.internal.Notification
import com.bdjobs.app.liveInterview.LiveInterviewActivity
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class LiveInterviewNightReceiver : BroadcastReceiver() {
    lateinit var ctx: Context
    lateinit var bdjobsUserSession: BdjobsUserSession
    lateinit var type : String

    override fun onReceive(context: Context, intent: Intent?) {

        Timber.d("called test receiver")

        ctx = context

        type = intent?.getStringExtra("type")!!

        Timber.d("called $type")

        bdjobsUserSession = BdjobsUserSession(ctx)

        if (bdjobsUserSession.isLoggedIn!!){
            createNotificationChannel()
//            showMorningNotification()
            showNightNotification()
        }
    }

//    private fun showMorningNotification() {
//        val cal = Calendar.getInstance()
//        val sdf = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
//        val test: String = sdf.format(cal.time)
//
//        val today = cal.time
//
//        cal.add(Calendar.DAY_OF_YEAR,1)
//
//        val tomorrow = cal.time
//
//        doAsync {
//            val totalInvitations = BdjobsDB.getInstance(ctx).liveInvitationDao().getTodaysInvitation(today,tomorrow)
//            uiThread {
//                Timber.d("$type ${totalInvitations.size}")
//
//                for (i in 0..totalInvitations.size.minus(1)) {
//
//                    val intent = Intent(ctx, LiveInterviewActivity::class.java).apply {
//                        putExtra("from", "notification")
//                        putExtra("jobId", totalInvitations[i].jobId)
//                        putExtra("jobTitle", totalInvitations[i].jobTitle)
//                    }
//
//                    val pendingIntent: PendingIntent = PendingIntent.getActivity(ctx, i, intent, PendingIntent.FLAG_ONE_SHOT)
//
//                    var builder = NotificationCompat.Builder(ctx, "CHANNEL_ID")
//                            .setSmallIcon(R.drawable.bdjobs_app_logo)
//                            .setContentTitle("Live Interview")
//                            .setContentIntent(pendingIntent)
//                            .setGroup("500")
//                            .setStyle(NotificationCompat.BigTextStyle().bigText("You have a Live Interview with ${totalInvitations[i].companyName} at ${getTimeAsAMPM(totalInvitations[i].liveInterviewTime.toString())}"))
//                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                    with(NotificationManagerCompat.from(ctx)) {
////                        Timber.d("value of i = $i")
////                        Timber.d("value of i = ${totalInvitations[i].companyName}")
//                        notify(i.plus(100), builder.build())
//                    }
//                    insertNotificationInToDatabase(totalInvitations[i])
//                }
//            }
//        }
//    }

    private fun showNightNotification() {
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
        val test: String = sdf.format(cal.time)

        val today = cal.time

        cal.add(Calendar.DAY_OF_YEAR,2)

        val tomorrow = cal.time

        doAsync {
            val totalInvitations = BdjobsDB.getInstance(ctx).liveInvitationDao().getTodaysInvitation(today,tomorrow)
            uiThread {
                Timber.d("$type ${totalInvitations.size}")

                for (i in 0..totalInvitations.size.minus(1)) {

                    val intent = Intent(ctx, LiveInterviewActivity::class.java).apply {
                        putExtra("from", "notification")
                        putExtra("jobId", totalInvitations[i].jobId)
                        putExtra("jobTitle", totalInvitations[i].jobTitle)
                    }

                    val pendingIntent: PendingIntent = PendingIntent.getActivity(ctx, i, intent, PendingIntent.FLAG_ONE_SHOT)

                    var builder = NotificationCompat.Builder(ctx, "CHANNEL_ID")
                            .setSmallIcon(R.drawable.bdjobs_app_logo)
                            .setContentTitle("Live Interview")
                            .setContentIntent(pendingIntent)
                            .setGroup("500")
                            .setStyle(NotificationCompat.BigTextStyle().bigText("You have a Live Interview with ${totalInvitations[i].companyName} at ${getTimeAsAMPM(totalInvitations[i].liveInterviewTime.toString())}"))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    with(NotificationManagerCompat.from(ctx)) {
//                        Timber.d("value of i = $i")
//                        Timber.d("value of i = ${totalInvitations[i].companyName}")
                        notify(i.plus(100), builder.build())
                    }
                    insertNotificationInToDatabase(totalInvitations[i])
                }
            }
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

//    @RequiresApi(Build.VERSION_CODES.M)
//    private fun scheduleNotification() {
//        val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val alarmIntent = Intent(ctx, TestBroadcastReceiver::class.java).apply {
//            putExtra(serial, value.plus(1))
//        }.let {
//            PendingIntent.getBroadcast(ctx, 1, it, PendingIntent.FLAG_ONE_SHOT)
//        }
//
//        val calendar: Calendar = Calendar.getInstance().apply {
//            timeInMillis = System.currentTimeMillis()
//            set(Calendar.HOUR_OF_DAY, 17)
//            set(Calendar.MINUTE, 10)
//        }
//
//        alarmManager?.setExactAndAllowWhileIdle(
//                AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                calendar.timeInMillis,
//                alarmIntent
//        )
//    }

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

    private fun insertNotificationInToDatabase(data: LiveInvitation) {
        val bdjobsInternalDB = BdjobsDB.getInstance(ctx)
        val date: Date? = Date()
        val notificationText = "You have a Live Interview with ${data.companyName} at ${getTimeAsAMPM(data.liveInterviewTime.toString())}"

        doAsync {
            bdjobsInternalDB.notificationDao().insertNotification(Notification(type = "li", serverId = data.jobId, seen = false, arrivalTime = date, seenTime = date, payload = "", imageLink = "", link = "", isDeleted = false, jobTitle = data.jobTitle, title = "", body = notificationText, companyName = data.companyName, notificationId = "", lanType = "", deadline = data.liveInterviewDateString))
            uiThread {
                val intent = Intent(Constants.BROADCAST_DATABASE_UPDATE_JOB)
                intent.putExtra("notification", "insertOrUpdateNotification")
                ctx.sendBroadcast(intent)
            }
        }
    }
}

//class LiveInterviewReceiver : BroadcastReceiver() {
//
//    lateinit var ctx: Context
//    lateinit var bdjobsUserSession: BdjobsUserSession
//    lateinit var type : String
//
//    override fun onReceive(context: Context, intent: Intent?) {
//
//        Timber.d("called test receiver")
//
//        ctx = context
//
//        type = intent?.getStringExtra("type")!!
//
//        Timber.d("called $type")
//
//        bdjobsUserSession = BdjobsUserSession(ctx)
//
//        if (bdjobsUserSession.isLoggedIn!!){
//            createNotificationChannel()
//            showMorningNotification()
//            showNightNotification()
//        }
//    }
//
//    private fun showMorningNotification() {
//        val cal = Calendar.getInstance()
//        val sdf = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
//        val test: String = sdf.format(cal.time)
//
//        val today = cal.time
//
//        cal.add(Calendar.DAY_OF_YEAR,1)
//
//        val tomorrow = cal.time
//
//        doAsync {
//            val totalInvitations = BdjobsDB.getInstance(ctx).liveInvitationDao().getTodaysInvitation(today,tomorrow)
//            uiThread {
//                Timber.d("$type ${totalInvitations.size}")
//
//                for (i in 0..totalInvitations.size.minus(1)) {
//
//                    val intent = Intent(ctx, LiveInterviewActivity::class.java).apply {
//                        putExtra("from", "notification")
//                        putExtra("jobId", totalInvitations[i].jobId)
//                        putExtra("jobTitle", totalInvitations[i].jobTitle)
//                    }
//
//                    val pendingIntent: PendingIntent = PendingIntent.getActivity(ctx, i, intent, PendingIntent.FLAG_ONE_SHOT)
//
//                    var builder = NotificationCompat.Builder(ctx, "CHANNEL_ID")
//                            .setSmallIcon(R.drawable.bdjobs_app_logo)
//                            .setContentTitle("Live Interview")
//                            .setContentIntent(pendingIntent)
//                            .setGroup("500")
//                            .setStyle(NotificationCompat.BigTextStyle().bigText("You have a Live Interview with ${totalInvitations[i].companyName} at ${getTimeAsAMPM(totalInvitations[i].liveInterviewTime.toString())}"))
//                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                    with(NotificationManagerCompat.from(ctx)) {
////                        Timber.d("value of i = $i")
////                        Timber.d("value of i = ${totalInvitations[i].companyName}")
//                        notify(i.plus(100), builder.build())
//                    }
//                    insertNotificationInToDatabase(totalInvitations[i])
//                }
//            }
//        }
//    }
//
//    private fun showNightNotification() {
//        val cal = Calendar.getInstance()
//        val sdf = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
//        val test: String = sdf.format(cal.time)
//
//        val today = cal.time
//
//        cal.add(Calendar.DAY_OF_YEAR,2)
//
//        val tomorrow = cal.time
//
//        doAsync {
//            val totalInvitations = BdjobsDB.getInstance(ctx).liveInvitationDao().getTodaysInvitation(today,tomorrow)
//            uiThread {
//                Timber.d("$type ${totalInvitations.size}")
//
//                for (i in 0..totalInvitations.size.minus(1)) {
//
//                    val intent = Intent(ctx, LiveInterviewActivity::class.java).apply {
//                        putExtra("from", "notification")
//                        putExtra("jobId", totalInvitations[i].jobId)
//                        putExtra("jobTitle", totalInvitations[i].jobTitle)
//                    }
//
//                    val pendingIntent: PendingIntent = PendingIntent.getActivity(ctx, i, intent, PendingIntent.FLAG_ONE_SHOT)
//
//                    var builder = NotificationCompat.Builder(ctx, "CHANNEL_ID")
//                            .setSmallIcon(R.drawable.bdjobs_app_logo)
//                            .setContentTitle("Live Interview")
//                            .setContentIntent(pendingIntent)
//                            .setGroup("500")
//                            .setStyle(NotificationCompat.BigTextStyle().bigText("You have a Live Interview with ${totalInvitations[i].companyName} at ${getTimeAsAMPM(totalInvitations[i].liveInterviewTime.toString())}"))
//                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                    with(NotificationManagerCompat.from(ctx)) {
////                        Timber.d("value of i = $i")
////                        Timber.d("value of i = ${totalInvitations[i].companyName}")
//                        notify(i.plus(100), builder.build())
//                    }
//                    insertNotificationInToDatabase(totalInvitations[i])
//                }
//            }
//        }
//    }
//
//
//    private fun createNotificationChannel() {
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = ("Test channel")
//            val importance = NotificationManager.IMPORTANCE_DEFAULT
//            val channel = NotificationChannel("CHANNEL_ID", name, importance)
//            // Register the channel with the system
//            val notificationManager: NotificationManager =
//                    ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//    }
//
////    @RequiresApi(Build.VERSION_CODES.M)
////    private fun scheduleNotification() {
////        val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
////        val alarmIntent = Intent(ctx, TestBroadcastReceiver::class.java).apply {
////            putExtra(serial, value.plus(1))
////        }.let {
////            PendingIntent.getBroadcast(ctx, 1, it, PendingIntent.FLAG_ONE_SHOT)
////        }
////
////        val calendar: Calendar = Calendar.getInstance().apply {
////            timeInMillis = System.currentTimeMillis()
////            set(Calendar.HOUR_OF_DAY, 17)
////            set(Calendar.MINUTE, 10)
////        }
////
////        alarmManager?.setExactAndAllowWhileIdle(
////                AlarmManager.ELAPSED_REALTIME_WAKEUP,
////                calendar.timeInMillis,
////                alarmIntent
////        )
////    }
//
//    fun getTimeAsAMPM(time: String): String {
//        if (time != "") {
//            try {
//                val dateFormatter = SimpleDateFormat("HH:mm:ss")
//                val date: Date = dateFormatter.parse(time)
//
//                // Get time from date
//                val timeFormatter = SimpleDateFormat("h:mm a")
//                return timeFormatter.format(date)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//        return time
//    }
//
//    private fun insertNotificationInToDatabase(data: LiveInvitation) {
//        val bdjobsInternalDB = BdjobsDB.getInstance(ctx)
//        val date: Date? = Date()
//        val notificationText = "You have a Live Interview with ${data.companyName} at ${getTimeAsAMPM(data.liveInterviewTime.toString())}"
//
//        doAsync {
//            bdjobsInternalDB.notificationDao().insertNotification(Notification(type = "li", serverId = data.jobId, seen = false, arrivalTime = date, seenTime = date, payload = "", imageLink = "", link = "", isDeleted = false, jobTitle = data.jobTitle, title = "", body = notificationText, companyName = data.companyName, notificationId = "", lanType = "", deadline = data.liveInterviewDateString))
//            uiThread {
//                val intent = Intent(Constants.BROADCAST_DATABASE_UPDATE_JOB)
//                intent.putExtra("notification", "insertOrUpdateNotification")
//                ctx.sendBroadcast(intent)
//            }
//        }
//    }
//}