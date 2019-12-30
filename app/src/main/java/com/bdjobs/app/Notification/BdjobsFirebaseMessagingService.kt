package com.bdjobs.app.Notification

import android.content.Intent
import android.util.Log
import androidx.annotation.UiThread
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
//import com.bdjobs.app.BackgroundJob.DatabaseUpdateJob
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Databases.Internal.Notification
import com.bdjobs.app.Notification.Models.CommonNotificationModel
import com.bdjobs.app.Notification.Models.InterviewInvitationNotificationModel
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Workmanager.DatabaseUpdateWorker
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*
import kotlin.system.exitProcess

class BdjobsFirebaseMessagingService : FirebaseMessagingService() {


    private lateinit var mNotificationHelper: NotificationHelper
    private lateinit var bdjobsUserSession: BdjobsUserSession
    private lateinit var bdjobsInternalDB: BdjobsDB
    //    private lateinit var interviewInvitaionModel : InterviewInvitationNotificationModel
    private lateinit var commonNotificationModel: CommonNotificationModel
    lateinit var arrivalTime: Date


    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        info("Refreshed token --> $p0")
        if (BdjobsUserSession(applicationContext).isLoggedIn!!) {
            Constants.sendDeviceInformation(p0, applicationContext)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

//        Log.d("rakib received", remoteMessage.data.toString())

        val gson = Gson()

        // Check if message contains a data payload.
        remoteMessage?.let {

            //Log.d("rakib received" ,"hhh")


            bdjobsUserSession = BdjobsUserSession(applicationContext)

            arrivalTime = Date()

            val payload = gson.toJson(it.data).replace("\\n", "\n")

            try {
                commonNotificationModel = gson.fromJson(payload, CommonNotificationModel::class.java)

            } catch (e: Exception) {
            }

            if (bdjobsUserSession.isLoggedIn!! && commonNotificationModel.pId.trim() == bdjobsUserSession.userId) {

                //Log.d("rakib", payload)

                logAnalyticsForUnseenNotification(commonNotificationModel.type!!, applicationContext, commonNotificationModel.jobId!!, commonNotificationModel.notificationId!!)


                when (commonNotificationModel.type) {

                    Constants.NOTIFICATION_TYPE_INTERVIEW_INVITATION -> {
                        try {
                            insertNotificationInToDatabase(payload)
                            showNotification(commonNotificationModel)
                        } catch (e: Exception) {
                            logException(e)
                        }
                        try {
//                            DatabaseUpdateJob.runJobImmediately()

                            val constraints = Constraints.Builder()
                                    .setRequiredNetworkType(NetworkType.CONNECTED)
                                    .build()

                            val databaseUpdateRequest = OneTimeWorkRequestBuilder<DatabaseUpdateWorker>()
                                    .setConstraints(constraints)
                                    .build()

                            WorkManager.getInstance(applicationContext).enqueue(databaseUpdateRequest)

                        } catch (e: Exception) {
                        }
                    }

                    Constants.NOTIFICATION_TYPE_CV_VIEWED -> {
                        try {
                            insertNotificationInToDatabase(payload)
                            showNotification(commonNotificationModel)
                        } catch (e: Exception) {
                            logException(e)
                        }
                        try {
//                            DatabaseUpdateJob.runJobImmediately()

                            val constraints = Constraints.Builder()
                                    .setRequiredNetworkType(NetworkType.CONNECTED)
                                    .build()

                            val databaseUpdateRequest = OneTimeWorkRequestBuilder<DatabaseUpdateWorker>()
                                    .setConstraints(constraints)
                                    .build()

                            WorkManager.getInstance(applicationContext).enqueue(databaseUpdateRequest)

                        } catch (e: Exception) {
                        }
                    }

                    Constants.NOTIFICATION_TYPE_MATCHED_JOB -> {
                        try {
                            insertNotificationInToDatabase(payload)
                            showNotification(commonNotificationModel)
                        } catch (e: Exception) {
                            logException(e)
                        }
                        try {
//                            DatabaseUpdateJob.runJobImmediately()

                            val constraints = Constraints.Builder()
                                    .setRequiredNetworkType(NetworkType.CONNECTED)
                                    .build()

                            val databaseUpdateRequest = OneTimeWorkRequestBuilder<DatabaseUpdateWorker>()
                                    .setConstraints(constraints)
                                    .build()

                            WorkManager.getInstance(applicationContext).enqueue(databaseUpdateRequest)

                        } catch (e: Exception) {
                        }
                    }

                    Constants.NOTIFICATION_TYPE_GENERAL -> {
                        //insertNotificationInToDatabase(payload)
                        //showNotification(commonNotificationModel)
                    }

                    Constants.NOTIFICATION_TYPE_REMOVE_NOTIFICATION -> {
                        try {
                            removeNotificationFromDatabase(commonNotificationModel)
                        } catch (e: Exception) {
                        }
                    }

                    Constants.NOTIFICATION_TYPE_REMOVE_MESSAGE -> {

                    }

                    Constants.NOTIFICATION_TYPE_FORCE_LOGOUT -> {
                        try {
                            removeShortcut(applicationContext)
                            bdjobsUserSession = BdjobsUserSession(applicationContext)
                            bdjobsUserSession.logoutUser(exitApp = true)
                        } catch (e: Exception) {
                            logException(e)
                        }
                    }

                    Constants.NOTIFICATION_TYPE_PROMOTIONAL_MESSAGE -> {
                        //Log.d("rakib", "came here")
                        try {
                            insertNotificationInToDatabase(payload)
                            showNotification(commonNotificationModel)
                        } catch (e: Exception) {
                        }
                    }
                    else -> {
                    }
                }
            }
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            //Log.d("rakib", "Message Notification Body: ${it.body}")
        }
    }

    private fun removeNotificationFromDatabase(commonNotificationModel: CommonNotificationModel?) {

        try {
            bdjobsInternalDB = BdjobsDB.getInstance(applicationContext)
            doAsync {
                bdjobsInternalDB.notificationDao().deleteNotificationBecauseServerToldMe(commonNotificationModel?.jobId!!, commonNotificationModel?.deleteType!!)
                bdjobsUserSession = BdjobsUserSession(applicationContext)
                bdjobsUserSession.updateNotificationCount(bdjobsInternalDB.notificationDao().getNotificationCount())
            }

        } catch (e: Exception) {
        }

    }

    private fun insertNotificationInToDatabase(data: String) {
        bdjobsUserSession = BdjobsUserSession(applicationContext)
        bdjobsInternalDB = BdjobsDB.getInstance(applicationContext)

        val date: Date? = Date()

        if (commonNotificationModel.type != "pm") {
            doAsync {
                bdjobsInternalDB.notificationDao().insertNotification(Notification(type = commonNotificationModel.type, serverId = commonNotificationModel.jobId, seen = false, arrivalTime = date, seenTime = date, payload = data, imageLink = commonNotificationModel.imageLink, link = commonNotificationModel.link, isDeleted = false, jobTitle = commonNotificationModel.jobTitle, title = commonNotificationModel.title, body = commonNotificationModel.body, companyName = commonNotificationModel.companyName, notificationId = commonNotificationModel.notificationId,lanType = commonNotificationModel.lanType, deadline = commonNotificationModel.deadlineDB))
                bdjobsUserSession.updateNotificationCount(bdjobsUserSession.notificationCount!! + 1)
                uiThread {
                    val intent = Intent(Constants.BROADCAST_DATABASE_UPDATE_JOB)
                    intent.putExtra("notification", "insertOrUpdateNotification")
                    applicationContext.sendBroadcast(intent)
                }
            }
        } else if (commonNotificationModel.type == "pm") {
            doAsync {
                bdjobsInternalDB.notificationDao().insertNotification(Notification(type = commonNotificationModel.type, serverId = commonNotificationModel.jobId, seen = false, arrivalTime = date, seenTime = date, payload = data, imageLink = commonNotificationModel.imageLink, link = commonNotificationModel.link, isDeleted = false, jobTitle = commonNotificationModel.jobTitle, title = commonNotificationModel.title, body = commonNotificationModel.body, companyName = commonNotificationModel.companyName, notificationId = commonNotificationModel.notificationId,lanType = commonNotificationModel.lanType, deadline = commonNotificationModel.deadlineDB))
                uiThread {
                    val intent = Intent(Constants.BROADCAST_DATABASE_UPDATE_JOB)
                    intent.putExtra("notification", "insertOrUpdateNotification")
                    applicationContext.sendBroadcast(intent)
                }
            }
        }
    }

    private fun showNotification(commonNotificationModel: CommonNotificationModel) {
        //Log.d("rakib" ,"outside noti")

        mNotificationHelper = NotificationHelper(applicationContext)

        when (commonNotificationModel.type) {
            Constants.NOTIFICATION_TYPE_INTERVIEW_INVITATION -> {
                try {
                    mNotificationHelper.notify(Constants.NOTIFICATION_INTERVIEW_INVITATTION, mNotificationHelper.prepareNotification(
                            commonNotificationModel.title!!, commonNotificationModel.body!!, commonNotificationModel.jobId!!, commonNotificationModel.companyName!!, commonNotificationModel.jobTitle!!, commonNotificationModel.type!!, commonNotificationModel.link, commonNotificationModel.imageLink, commonNotificationModel.notificationId,commonNotificationModel.lanType,commonNotificationModel.deadlineDB))
                } catch (e: Exception) {
                }
            }
            Constants.NOTIFICATION_TYPE_CV_VIEWED -> {
                try {
                    mNotificationHelper.notify(Constants.NOTIFICATION_CV_VIEWED, mNotificationHelper.prepareNotification(commonNotificationModel.title!!, commonNotificationModel.body!!, commonNotificationModel.jobId!!, commonNotificationModel.companyName!!, commonNotificationModel.jobTitle!!, commonNotificationModel.type!!, commonNotificationModel.link, commonNotificationModel.imageLink, commonNotificationModel.notificationId,commonNotificationModel.lanType,commonNotificationModel.deadlineDB))
                } catch (e: Exception) {
                }
            }
            Constants.NOTIFICATION_TYPE_PROMOTIONAL_MESSAGE -> {
                //Log.d("rakib" ,"insdie noti")
                try {
                    mNotificationHelper.notify(Constants.NOTIFICATION_PROMOTIONAL_MESSAGE, mNotificationHelper.prepareNotification(
                            commonNotificationModel.title!!, commonNotificationModel.body!!, commonNotificationModel.jobId!!, commonNotificationModel.companyName!!, commonNotificationModel.jobTitle!!, commonNotificationModel.type!!, commonNotificationModel.link, commonNotificationModel.imageLink, commonNotificationModel.notificationId,commonNotificationModel.lanType,commonNotificationModel.deadlineDB))
                } catch (e: Exception) {
                }
            }
            Constants.NOTIFICATION_TYPE_GENERAL -> {

            }
        }

    }

}