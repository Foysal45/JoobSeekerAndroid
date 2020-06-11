package com.bdjobs.app.videoInterview.data.repository

import android.app.Application
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.videoInterview.data.models.*
import com.bdjobs.app.videoInterview.data.remote.VideoInterviewApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VideoInterviewRepository(val application: Application) {

    val session = BdjobsUserSession(application)

    suspend fun getVideoInterviewDetailsFromRemote(jobId: String?): VideoInterviewDetails {
        return withContext(Dispatchers.IO) {
            VideoInterviewApiService.create(application).getVideoInterviewDetails(
                    userID = session.userId,
                    decodeID = session.decodId,
                    jobId = jobId
            )
        }
    }

    suspend fun getQuestionListFromRemote(jobId: String?, applyId: String?): VideoInterviewQuestionList {
        return withContext(Dispatchers.IO) {
            VideoInterviewApiService.create(application).getVideoInterviewQuestionList(
                    userID = session.userId,
                    decodeID = session.decodId,
                    jobId = jobId,
                    applyId = applyId
            )
        }
    }

    suspend fun getInterviewList(): VideoInterviewListModel {
        return withContext(Dispatchers.IO) {
            VideoInterviewApiService.create(application).getInterviewListFromAPI(session.userId, session.decodId)
        }

    }

    suspend fun postVideoStartedInformationToRemote(videoManager: VideoManager) : CommonResponse{
        return withContext(Dispatchers.IO){
            VideoInterviewApiService.create(application).sendVideoStartedInfo(
                    userID = session.userId,
                    decodeID = session.decodId,
                    applyId = videoManager.applyId,
                    deviceName = "Android"
            )
        }
    }

//    suspend fun postVideoToRemote(videoManager: VideoManager){
//        return withContext(Dispatchers.IO){
//            VideoInterviewApiService.create(application,1).uploadVideo(
//                    userID = session.userId
//            )
//        }
//    }
}