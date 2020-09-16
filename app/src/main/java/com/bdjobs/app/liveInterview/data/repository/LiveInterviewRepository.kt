package com.bdjobs.app.liveInterview.data.repository

import android.app.Application
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.liveInterview.data.models.LiveInterviewDetails
import com.bdjobs.app.liveInterview.data.models.LiveInterviewList
import com.bdjobs.app.videoInterview.data.models.VideoInterviewDetails
import com.bdjobs.app.videoInterview.data.models.VideoInterviewList
import com.bdjobs.app.videoInterview.data.remote.VideoInterviewApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class LiveInterviewRepository(val application: Application)  {

    val session = BdjobsUserSession(application)

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