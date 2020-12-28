package com.bdjobs.app.liveInterview.data.repository

import android.app.Application
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.databases.internal.BdjobsDB
import com.bdjobs.app.databases.internal.LiveInvitation
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.liveInterview.data.models.LiveInterviewDetails
import com.bdjobs.app.liveInterview.data.models.LiveInterviewList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class LiveInterviewRepository(val application: Application)  {

    val session = BdjobsUserSession(application)
    val bdjobsDB = BdjobsDB.getInstance(application.applicationContext)

    suspend fun getLiveInterviewDetailsFromRemote(jobId: String?): LiveInterviewDetails {
        return withContext(Dispatchers.IO) {
            ApiServiceMyBdjobs.create().getLiveInterviewDetails(
                    userID = session.userId,
                    decodeID = session.decodId,
                    jobId = jobId
            )
        }
    }

    suspend fun getLiveInterviewListFromRemote(activity : String = "0"): LiveInterviewList {
        return withContext(Dispatchers.IO) {
            ApiServiceMyBdjobs.create().getLiveInterviewList(
                    userId  = session.userId,
                    decodeId = session.decodId,
                    isActivityDate = activity,
                    pageNumber = "1",
                    itemsPerPage = "50"
            )
        }
    }

    suspend fun getAllTimeLiveInterviewListFromDatabase() : List<LiveInvitation>{
        return withContext(Dispatchers.IO){
            bdjobsDB.liveInvitationDao().getAllLiveInvitation()
        }
    }

    suspend fun getThisMonthLiveInterviewListFromDatabase() : List<LiveInvitation>{
        return withContext(Dispatchers.IO){
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            val firstDateOfMonth = calendar.time
            bdjobsDB.liveInvitationDao().getAllLiveInvitationByDate(firstDateOfMonth)
        }
    }

    suspend fun sendLiveInterviewConfirmation(
            applyId : String,
            activity: String,
            cancelReason : String = "",
            invitationId : String,
            otherComment : String = "",
            rescheduleComment : String= ""
    ) : LiveInterviewDetails{
        return withContext(Dispatchers.IO) {
            ApiServiceMyBdjobs.create().sendInterviewConfirmationLive(
                    userID = session.userId,
                    decodeID = session.decodId,
                    applyId = applyId,
                    activity = activity,
                    cancleReason = cancelReason,
                    invitationId = invitationId,
                    otherComment = otherComment,
                    rescheduleComment = rescheduleComment
            )
        }
    }
}