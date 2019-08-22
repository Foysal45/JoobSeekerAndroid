package com.bdjobs.app.Notification

import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.info
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class BdjobsFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        info( "Refreshed token --> $p0")
        if(BdjobsUserSession(applicationContext).isLoggedIn!!) {
            Constants.sendDeviceInformation(p0, applicationContext)
        }
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        info("message --> ${p0?.data?.get("body")}")
    }

}