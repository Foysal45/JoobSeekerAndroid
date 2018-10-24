package com.bdjobs.app.Notification

import android.util.Log
import com.bdjobs.app.Utilities.info
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class BdjobsFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage?) {
        info("message: ${message?.data?.get("body")}")

    }

    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        info( "Refreshed token: $token")
    }
}