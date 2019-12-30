package com.bdjobs.app.Utilities

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.google.firebase.analytics.FirebaseAnalytics
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*
import kotlin.math.log

fun logDataForAnalytics(type: String, context: Context, jobID: String, nId: String) {

    try {
        val bdjobsUserSession = BdjobsUserSession(context)
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val bdjobsDB = BdjobsDB.getInstance(context)
        firebaseAnalytics.setUserId(bdjobsUserSession.userId)


        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, jobID)
        when (type) {
            Constants.NOTIFICATION_TYPE_INTERVIEW_INVITATION -> bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Interview_Invitation")
            Constants.NOTIFICATION_TYPE_CV_VIEWED -> bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "CV_Viewed")
            Constants.NOTIFICATION_TYPE_PROMOTIONAL_MESSAGE -> bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Promotional_Message")
            Constants.NOTIFICATION_TYPE_MATCHED_JOB -> bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Matched_Job")
        }
        bundle.putString(FirebaseAnalytics.Param.END_DATE, Date().toString())
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "notification_opened")
        bundle.putString("NID", nId)
        bundle.putString("UserID", bdjobsUserSession.userId)


        context.doAsync {
            try {
                val arrivalTime = bdjobsDB.notificationDao().getNotificationArrivalTime(type, nId)
                //Log.d("rakib time", arrivalTime.toString())
                uiThread {
                    bundle.putString(FirebaseAnalytics.Param.START_DATE, arrivalTime.toString())
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
                }
            } catch (e: Exception) {
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun logAnalyticsForUnseenNotification(type: String, context: Context, jobID: String, nId: String){

    //Log.d("rakib", "sent unseen notification to analytics")

    try {
        val bdjobsUserSession = BdjobsUserSession(context)
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val bdjobsDB = BdjobsDB.getInstance(context)
        firebaseAnalytics.setUserId(bdjobsUserSession.userId)


        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, jobID)
        when (type) {
            Constants.NOTIFICATION_TYPE_INTERVIEW_INVITATION -> bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Interview_Invitation")
            Constants.NOTIFICATION_TYPE_CV_VIEWED -> bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "CV_Viewed")
            Constants.NOTIFICATION_TYPE_PROMOTIONAL_MESSAGE -> bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Promotional_Message")
            Constants.NOTIFICATION_TYPE_MATCHED_JOB -> bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Matched_Job")

        }
        bundle.putString(FirebaseAnalytics.Param.START_DATE, Date().toString())
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "notification_received")
        bundle.putString("NID", nId)
        bundle.putString("UserID", bdjobsUserSession.userId)

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

//        context.doAsync {
//            val arrivalTime = bdjobsDB.notificationDao().getNotificationArrivalTime(type, jobID)
//            uiThread {
//                bundle.putString(FirebaseAnalytics.Param.START_DATE, arrivalTime.toString())
//                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//            }
//        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

}
