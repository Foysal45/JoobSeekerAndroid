package com.bdjobs.app.training.data.repository

import android.app.Application
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.training.data.models.TrainingList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class TrainingRepository(application: Application) {

    private val bdJobsUserSession = BdjobsUserSession(application.applicationContext)

    suspend fun fetchTrainingList(trainingId: String): Response<TrainingList> {
        return withContext(Dispatchers.IO) {
            ApiServiceMyBdjobs.create()
                .getTrainingList(bdJobsUserSession.userId, bdJobsUserSession.decodId, trainingId)
        }
    }

}