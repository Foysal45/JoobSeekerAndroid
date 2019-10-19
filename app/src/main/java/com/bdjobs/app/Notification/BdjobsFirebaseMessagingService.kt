package com.bdjobs.app.Notification

import android.util.Log
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Databases.Internal.Notification
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.info
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.jetbrains.anko.doAsync
import java.util.*

class BdjobsFirebaseMessagingService : FirebaseMessagingService() {


    private lateinit var mNotificationHelper: NotificationHelper
    private lateinit var bdjobsUserSession: BdjobsUserSession
    private lateinit var bdjobsInternalDB: BdjobsDB


    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        info("Refreshed token --> $p0")
        if (BdjobsUserSession(applicationContext).isLoggedIn!!) {
            Constants.sendDeviceInformation(p0, applicationContext)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)


        // Check if message contains a data payload.
        remoteMessage.data.isNotEmpty()?.let {
            //Log.d("rakib", "Message data payload: " + remoteMessage.data.toString())
            insertNotificationInToDatabase(remoteMessage.data.toString())
            showNotification(remoteMessage.data["body"])

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
                bdjobsInternalDB.notificationDao().insertNotification(Notification(type = "n", seen = false, arrivalTime = date, seenTime = date, payload = data))
                bdjobsUserSession.updateNotificationCount(bdjobsUserSession.notificationCount!! + 1)
            }


    }

    private fun showNotification(body : String?) {
        mNotificationHelper = NotificationHelper(applicationContext)
        mNotificationHelper.notify(Constants.BDJOBS_SAMPLE_NOTIFICATION, mNotificationHelper.getExpandableNotification(
                "Bdjobs", body!!))


    }

}