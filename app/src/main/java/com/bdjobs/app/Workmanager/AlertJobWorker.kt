package com.bdjobs.app.Workmanager

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Notification.NotificationHelper
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AlertJobWorker(val appContext: Context, workerParams: WorkerParameters): Worker(appContext,workerParams) {
    override fun doWork(): Result {

        val bdjobsUserSession = BdjobsUserSession(appContext)

        try {
            val shortlistedDate = bdjobsUserSession.shortListedDate
            Timber.tag("rakib").d("$shortlistedDate")
            val c = Calendar.getInstance().time
            val df = SimpleDateFormat("dd-MMM-yyyy")
            val dtcrnt = df.format(c)
            Timber.tag("rakib").d("$dtcrnt")
            //Log.d("formattedDate", "dtprev: $shortlistedDate  dtcrnt: $dtcrnt")
            if (shortlistedDate != dtcrnt) {

                val bdjobsDB = BdjobsDB.getInstance(appContext)

                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, 2)
                val deadlineNext2Days = calendar.time

                doAsync {
                    val shortlistedjobs = bdjobsDB.shortListedJobDao().getShortListedJobsBYDeadline(deadlineNext2Days)
                    Timber.tag("rakib").d("all $shortlistedjobs size ${shortlistedjobs.size}")
                    uiThread {
                        //Log.d("ShortListedJobPopup", "Job found: ${shortlistedjobs.size}")
                        try {
                            if (shortlistedjobs.isNotEmpty()) {
                                if (shortlistedjobs.size == 1) {
                                    showNotification("title", "You have 1 shortlested job")
                                } else {
                                    showNotification("title", "You have ${shortlistedjobs.size} shortlisted jobs")

                                }
                            }
                        } catch (e: Exception) {
                            //logException(e)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            //logException(e)
        }
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