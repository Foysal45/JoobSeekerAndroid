package com.bdjobs.app.Notification

import android.util.Log
import androidx.annotation.UiThread
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Databases.Internal.Notification
import com.bdjobs.app.Notification.Models.InterviewInvitationNotificationModel
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.equalIgnoreCase
import com.bdjobs.app.Utilities.info
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import org.jetbrains.anko.doAsync
import java.util.*
import kotlin.system.exitProcess

class BdjobsFirebaseMessagingService : FirebaseMessagingService() {


    private lateinit var mNotificationHelper: NotificationHelper
    private lateinit var bdjobsUserSession: BdjobsUserSession
    private lateinit var bdjobsInternalDB: BdjobsDB
    private lateinit var interviewInvitaionModel : InterviewInvitationNotificationModel


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
            interviewInvitaionModel = gson.fromJson(payload,InterviewInvitationNotificationModel::class.java)

            if (interviewInvitaionModel.type!!.equalIgnoreCase("fl")){
                try {
                    bdjobsUserSession = BdjobsUserSession(applicationContext)
                    bdjobsUserSession.logoutUser(exitApp = true)
//                    exitProcess(1)
                } catch (e: Exception) {
                }

            } else {
                insertNotificationInToDatabase(payload)
                showNotification(payload)
            }


        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d("rakib", "Message Notification Body: ${it.body}")
        }
    }

    private fun insertNotificationInToDatabase(data : String) {

         bdjobsUserSession = BdjobsUserSession(applicationContext)
         bdjobsInternalDB = BdjobsDB.getInstance(applicationContext)

            val date: Date? = Date()
            doAsync {
                bdjobsInternalDB.notificationDao().insertNotification(Notification(type = interviewInvitaionModel.type, serverId = interviewInvitaionModel.jobid , seen = false, arrivalTime = date, seenTime = date, payload = data))
                bdjobsUserSession.updateNotificationCount(bdjobsUserSession.notificationCount!! + 1)

            }


    }

    private fun showNotification(payload : String?) {
        mNotificationHelper = NotificationHelper(applicationContext)

        val notification = Gson().fromJson(payload,InterviewInvitationNotificationModel::class.java)

        mNotificationHelper.notify(Constants.BDJOBS_SAMPLE_NOTIFICATION, mNotificationHelper.getInterviewInvitationNotification(
                notification.title!!, notification.body!!,notification.jobid!!,notification.companyName!!,notification.jobTitle!!,notification.type!!))


    }

}