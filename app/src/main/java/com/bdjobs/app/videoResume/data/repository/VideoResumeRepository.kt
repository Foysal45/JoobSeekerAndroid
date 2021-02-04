package com.bdjobs.app.videoResume.data.repository

import android.app.Application
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.videoResume.data.models.CommonResponse
import com.bdjobs.app.videoResume.data.models.Question
import com.bdjobs.app.videoResume.data.models.VideoResumeQuestionList
import com.bdjobs.app.videoResume.data.models.VideoResumeStatistics
import com.bdjobs.app.videoResume.data.remote.VideoResumeApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VideoResumeRepository(private val application: Application) {

    val session = BdjobsUserSession(application)

    fun getAllQuestionsFromDB() : List<Question> {
        return listOf(
                Question(1,"Lorem ipsum dolor sit amet, consectetur elit. Maecenas venenatis euismod ante. non egestas. Nulla ornare rhoncus felis vitae dictum. Curabitur in euismod ligula.","00:60", isNew = false, isSubmitted = false),
                Question(2,"Lorem ipsum dolor sit amet, consectetur elit. Maecenas venenatis euismod ante. non egestas. Nulla ornare rhoncus felis vitae dictum. Curabitur in euismod ligula.","00:50", isNew = false, isSubmitted = false),
                Question(3,"Lorem ipsum dolor sit amet, consectetur elit. Maecenas venenatis euismod ante. non egestas. Nulla ornare rhoncus felis vitae dictum. Curabitur in euismod ligula.","00:40", isNew = false, isSubmitted = false),
                Question(4,"Lorem ipsum dolor sit amet, consectetur elit. Maecenas venenatis euismod ante. non egestas. Nulla ornare rhoncus felis vitae dictum. Curabitur in euismod ligula.","00:30", isNew = false, isSubmitted = false),
                Question(5,"Lorem ipsum dolor sit amet, consectetur elit. Maecenas venenatis euismod ante. non egestas. Nulla ornare rhoncus felis vitae dictum. Curabitur in euismod ligula.","00:20", isNew = true, isSubmitted = false),
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
}