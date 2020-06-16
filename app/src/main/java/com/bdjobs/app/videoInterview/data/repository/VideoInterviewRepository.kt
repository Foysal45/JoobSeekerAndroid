package com.bdjobs.app.videoInterview.data.repository

import android.app.Application
import android.util.Log
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.videoInterview.data.models.*
import com.bdjobs.app.videoInterview.data.remote.VideoInterviewApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class VideoInterviewRepository(val application: Application) {

    val session = BdjobsUserSession(application)
    var videoManager: VideoManager? = null

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

    suspend fun getInterviewList(): VideoInterviewList {
        return withContext(Dispatchers.IO) {
            VideoInterviewApiService.create(application).getInterviewListFromAPI(session.userId, session.decodId)
        }

    }

    suspend fun postVideoStartedInformationToRemote(videoManager: VideoManager): CommonResponse {
        return withContext(Dispatchers.IO) {
            VideoInterviewApiService.create(application).sendVideoStartedInfo(
                    userID = session.userId,
                    decodeID = session.decodId,
                    applyId = videoManager.applyId,
                    deviceName = "Android"
            )
        }
    }

    suspend fun postVideoToRemote(): CommonResponse {
        val file: File? = Constants?.file?.absoluteFile
        Log.d("rakib", "$file")
        val userId = session.userId?.toRequestBody()
        val decodeId = session.decodId?.toRequestBody()
        val applyId = Constants.applyId?.toRequestBody()
        val jobId = Constants.jobId?.toRequestBody()
        val questionId = Constants.quesId?.toRequestBody()
        val questionSerialNo = Constants.quesSerialNo?.toRequestBody()
        val questionDuration = Constants.duration?.toRequestBody()
        val requestFileBody = file?.asRequestBody("file/*".toMediaType())
        val requestFile = MultipartBody.Part.createFormData("file", file!!.name, requestFileBody!!)

        return withContext(Dispatchers.IO) {
            VideoInterviewApiService.create(application, 1).uploadVideo(
                    userID = userId,
                    decodeID = decodeId,
                    applyId = applyId,
                    jobId = jobId,
                    duration = questionDuration,
                    quesId = questionId,
                    questionSerialNo = questionSerialNo,
                    file = requestFile
            )
        }
    }

    suspend fun submitAnswerToRemote(answerManager: AnswerManager?) : CommonResponse
    {
        return withContext(Dispatchers.IO){
            VideoInterviewApiService.create(application).submitAnswer(
                    userID = session.userId,
                    decodeID = session.decodId,
                    jobId = answerManager?.jobId,
                    applyId = answerManager?.applyId,
                    type = answerManager?.type,
                    totalAnswerCount = answerManager?.totalAnswerCount
            )
        }
    }

}