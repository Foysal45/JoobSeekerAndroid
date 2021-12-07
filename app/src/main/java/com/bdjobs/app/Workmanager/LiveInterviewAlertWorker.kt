package com.bdjobs.app.Workmanager

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bdjobs.app.Notification.NotificationHelper
import com.bdjobs.app.utilities.Constants
import java.util.*
import java.util.concurrent.TimeUnit

class LiveInterviewAlertWorker(val appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
//        doAsync {
//            val bdjobsDB = BdjobsDB.getInstance(appContext)
//            val calendar = Calendar.getInstance()
//            val live = bdjobsDB.liveInvitationDao().getAllLiveInvitationByDate(Date())
//            val all = bdjobsDB.liveInvitationDao().getAllLiveInvitation()
//            Timber.tag("rakib").d("live $live")
//            Timber.tag("rakib").d("all $all")
//            uiThread {
//                val currentDate = Calendar.getInstance()
//                val dueDate = Calendar.getInstance()
//                dueDate.add(Calendar.MINUTE, 5)
//            }
//        }
        showNotification("live test", "Now")

        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance()
        dueDate.add(Calendar.MINUTE, 3)

        val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis

        val dailyWorkRequest = OneTimeWorkRequestBuilder<LiveInterviewAlertWorker>()
                .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .addTag("live")
                .build()

        WorkManager.getInstance(applicationContext)
                .enqueue(dailyWorkRequest)

        return Result.success()
    }

    private fun showNotification(title: String, body: String) {
        val notificationHelper = NotificationHelper(appContext)
        notificationHelper.notify(
                Constants.NOTIFICATION_ALERT,
                notificationHelper.prepareNotification(
                        title,
                        body,
                        type = Constants.NOTIFICATION_TYPE_ALERT_NOTIFICATION
                )
        )
    }
}