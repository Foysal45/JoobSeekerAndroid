package com.bdjobs.app.Notification

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.iid.FirebaseInstanceId



class MyFirebaseInstanceIDService : FirebaseInstanceIdService() {
    override fun onTokenRefresh() {
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d("Firebase Token", "Refreshed token: " + refreshedToken!!)
        //sendRegistrationToServer(refreshedToken)
    }
}