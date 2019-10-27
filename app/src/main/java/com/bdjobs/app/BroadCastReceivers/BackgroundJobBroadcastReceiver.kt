package com.bdjobs.app.BroadCastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bdjobs.app.Utilities.Constants.Companion.BROADCAST_DATABASE_UPDATE_JOB


class BackgroundJobBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (BROADCAST_DATABASE_UPDATE_JOB == action) {
            val job = intent?.getStringExtra("job")
            val notification = intent?.getStringExtra("notification")
            if (backgroundJobListener != null) {
                if(job=="insertFavouriteSearchFilter"){
                    backgroundJobListener!!.favSearchFilterSyncComplete()
                }
                if(job=="insertFollowedEmployers"){
                    backgroundJobListener!!.followedEmployerSyncComplete()
                }
                if(job=="insertJobInvitation"){
                    backgroundJobListener!!.jobInvitationSyncComplete()
                }
                if(job=="insertCertificationList"){
                    backgroundJobListener!!.certificationSyncComplete()
                }
//                if (job == "insertNotifications"){
//                    backgroundJobListener!!.onUpdateNotification()
//                }
            }
            if (notificationUpdateListener != null){
                if (notification == "insertOrUpdateNotification")
                notificationUpdateListener!!.onUpdateNotification()
            }
        }

    }



    interface BackgroundJobListener {
        fun favSearchFilterSyncComplete()
        fun jobInvitationSyncComplete()
        fun certificationSyncComplete()
        fun followedEmployerSyncComplete()

    }

    interface NotificationUpdateListener{
        fun onUpdateNotification()
    }

    companion object {
        var backgroundJobListener: BackgroundJobListener? = null
        var notificationUpdateListener : NotificationUpdateListener? = null
    }
}