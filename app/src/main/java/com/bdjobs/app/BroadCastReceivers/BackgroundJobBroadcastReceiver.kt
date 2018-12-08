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
            }
        }

    }



    interface BackgroundJobListener {
        fun favSearchFilterSyncComplete()
        fun jobInvitationSyncComplete()
        fun certificationSyncComplete()
        fun followedEmployerSyncComplete()


    }

    companion object {
        var backgroundJobListener: BackgroundJobListener? = null
    }
}