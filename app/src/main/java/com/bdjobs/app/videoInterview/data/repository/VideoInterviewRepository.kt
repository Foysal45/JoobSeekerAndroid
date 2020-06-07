package com.bdjobs.app.videoInterview.data.repository

import android.app.Application
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.videoInterview.data.models.VideoInterviewDetails
import com.bdjobs.app.videoInterview.data.remote.VideoInterviewApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VideoInterviewRepository(val application: Application) {

    val session = BdjobsUserSession(application)

    suspend fun getVideoInterviewDetailsFromRemote(jobId: String?) : VideoInterviewDetails{
        return withContext(Dispatchers.IO) {
            VideoInterviewApiService.create(application).getVideoInterviewDetails(
                    userID = session.userId,
                    decodeID = session.decodId,
                    jobId = jobId
            )
        }
    }
}