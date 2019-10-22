package com.bdjobs.app.Notification

import android.content.Intent
import android.util.Log
import androidx.annotation.UiThread
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Databases.Internal.Notification
import com.bdjobs.app.Notification.Models.CommonNotificationModel
import com.bdjobs.app.Notification.Models.InterviewInvitationNotificationModel
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.equalIgnoreCase
import com.bdjobs.app.Utilities.info
import com.bdjobs.app.Utilities.logException
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


    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        info("Refreshed token --> $p0")
        if (BdjobsUserSession(applicationContext).isLoggedIn!!) {
            Constants.sendDeviceInformation(p0, applicationContext)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)


        val gson = Gson()

        // Check if message contains a data payload.
        remoteMessage?.let {

            val payload = gson.toJson(it.data).toString()
            Log.d("rakib",payload)
            commonNotificationModel = gson.fromJson(payload, CommonNotificationModel::class.java)

            when(commonNotificationModel.type){
                Constants.NOTIFICATION_TYPE_INTERVIEW_INVITATION->{
                    insertNotificationInToDatabase(payload)
                    showNotification(payload)
                }
                Constants.NOTIFICATION_TYPE_CV_VIEWED->{

                }
                Constants.NOTIFICATION_TYPE_FORCE_LOGOUT->{
                    try {
                        bdjobsUserSession = BdjobsUserSession(applicationContext)
                        bdjobsUserSession.logoutUser(exitApp = true)
                    } catch (e: Exception) {
                        logException(e)
                    }
                }
                else->{
                }
            }
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d("rakib", "Message Notification Body: ${it.body}")
        }
    }

    private fun insertNotificationInToDatabase(data: String) {

        bdjobsUserSession = BdjobsUserSession(applicationContext)
        bdjobsInternalDB = BdjobsDB.getInstance(applicationContext)

        val date: Date? = Date()
        doAsync {
            bdjobsInternalDB.notificationDao().insertNotification(Notification(type = commonNotificationModel.type, serverId = commonNotificationModel.jobId, seen = false, arrivalTime = date, seenTime = date, payload = data, imageLink = commonNotificationModel.imageLink, link = commonNotificationModel.link, isDeleted = false, jobTitle = commonNotificationModel.jobTitle))
            bdjobsUserSession.updateNotificationCount(bdjobsUserSession.notificationCount!! + 1)
            uiThread {
                val intent = Intent(Constants.BROADCAST_DATABASE_UPDATE_JOB)
                intent.putExtra("job", "insertNotifications")
                applicationContext.sendBroadcast(intent)
            }
        }


    }

    private fun showNotification(payload: String?) {
        mNotificationHelper = NotificationHelper(applicationContext)

//        val commonNotificationModel = Gson().fromJson(payload, commonNotificationModel::class.java)

        mNotificationHelper.notify(Constants.BDJOBS_SAMPLE_NOTIFICATION, mNotificationHelper.getInterviewInvitationNotification(
                commonNotificationModel.title!!, commonNotificationModel.body!!, commonNotificationModel.jobId!!, commonNotificationModel.companyName!!, commonNotificationModel.jobTitle!!, commonNotificationModel.type!!))


    }

}