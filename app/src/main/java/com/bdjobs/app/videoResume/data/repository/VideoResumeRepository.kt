package com.bdjobs.app.videoResume.data.repository

import android.app.Application
import android.util.Log
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.videoResume.data.models.*
import com.bdjobs.app.videoResume.data.remote.VideoResumeApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class VideoResumeRepository(private val application: Application) {

    val session = BdjobsUserSession(application)

    fun getAllQuestionsFromDB() : List<Question> {
        return listOf(
                Question(1,"Introduce Yourself.","00:30", isNew = false, isSubmitted = false),
                Question(2,"Tell us about your academic qualification and trainings (if any).","00:30", isNew = false, isSubmitted = false),
                Question(3,"Discuss about your skills (educational/job).","00:60", isNew = false, isSubmitted = false),
                Question(4,"What are your strengths and  weaknesses ?","00:30", isNew = false, isSubmitted = false),
                Question(5,"Describe about your career goals ?","00:30", isNew = false, isSubmitted = false),
        )
    }

    suspend fun getStatisticsFromRemote(): VideoResumeStatistics {
        return withContext(Dispatchers.IO) {
            VideoResumeApiService.create(application).getVideoResumeStatistics(
                    userID = session.userId,
                    decodeID = session.decodId,
                    appId = Constants.APP_ID,
                    lang = "EN"
            )
        }
    }

    suspend fun getQuestionListFromRemote(): VideoResumeQuestionList {
        return withContext(Dispatchers.IO) {
            VideoResumeApiService.create(application).getVideoResumeQuestionList(
                    userID = session.userId,
                    decodeID = session.decodId,
                    appId = Constants.APP_ID,
                    lang = "EN"
            )
        }
    }

    suspend fun submitStatusVisibility(isVisible : String?): CommonResponse {
        return withContext(Dispatchers.IO) {
            VideoResumeApiService.create(application).submitStatusVisibility(
                    userID = session.userId,
                    decodeID = session.decodId,
                    appId = Constants.APP_ID,
                    lang = "EN",
                    statusVisibility = isVisible
            )
        }
    }

    suspend fun deleteSingleVideoOfResume(videoResumeManager: VideoResumeManager): CommonResponse {
        return withContext(Dispatchers.IO) {
            VideoResumeApiService.create(application,1).deleteSingleVideoOfResume(
                    userID = session.userId,
                    decodeID = session.decodId,
                    appId = Constants.APP_ID,
                    lang = "EN",
                    aID = videoResumeManager.aID,
                    questionId = videoResumeManager.questionId
            )
        }
    }

    suspend fun postVideoResumeToRemote(): CommonResponse {
        val file: File? = Constants?.file?.absoluteFile
        Log.d("salvin-resume", "$file")
        val userId = session.userId?.toRequestBody()
        val decodeId = session.decodId?.toRequestBody()
        val questionId = Constants.quesId?.toRequestBody()
        val questionSerialNo = Constants.quesSerialNo?.toRequestBody()
        val questionDuration = Constants.duration?.toRequestBody()
        val deviceType = "Android".toRequestBody()
        val browserInfo = "".toRequestBody()
        val appId = Constants.APP_ID.toRequestBody()
        val lang = "EN".toRequestBody()


        val requestFileBody = file?.asRequestBody("file/*".toMediaType())
        val requestFile = MultipartBody.Part.createFormData("file", file!!.name, requestFileBody!!)

        return withContext(Dispatchers.IO) {
            VideoResumeApiService.create(application, 1).uploadVideo(
                    userID = userId,
                    decodeID = decodeId,
                    duration = questionDuration,
                    quesId = questionId,
                    questionSerialNo = questionSerialNo,
                    deviceType = deviceType,
                    browserInfo = browserInfo,
                    appId = appId,
                    lang = lang,
                    file = requestFile
            )
        }
    }
}