package com.bdjobs.app.BroadCastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bdjobs.app.utilities.Constants.Companion.BROADCAST_DATABASE_UPDATE_JOB


class BackgroundJobBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (BROADCAST_DATABASE_UPDATE_JOB == action) {

            //Log.d("rakib", "Broadcast $action")

            val job = intent.getStringExtra("job")
            val notification = intent.getStringExtra("notification")

            //Log.d("rakib", "Broadcast $job $notification")

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
                if(job=="insertVideoInvitation"){
                    backgroundJobListener!!.videoInvitationSyncComplete()
                }
                if(job=="insertCertificationList"){
                    backgroundJobListener!!.certificationSyncComplete()
                }
                if (job == "insertLiveInvitation"){
                    backgroundJobListener!!.liveInvitationSyncComplete()
                }
//                if (job == "insertNotifications"){
//                    backgroundJobListener!!.onUpdateNotification()
//                }
            }

            if (notificationUpdateListener != null){
                if (notification == "insertOrUpdateNotification"){
                    //Log.d("rakib", "inside broadcast")
                    notificationUpdateListener!!.onUpdateNotification()
                }
            }
        }

    }



    interface BackgroundJobListener {
        fun favSearchFilterSyncComplete()
        fun jobInvitationSyncComplete()
        fun videoInvitationSyncComplete()
        fun liveInvitationSyncComplete()
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