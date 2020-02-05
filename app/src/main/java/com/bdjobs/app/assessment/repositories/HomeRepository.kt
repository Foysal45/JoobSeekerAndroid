package com.bdjobs.app.assessment.repositories

import android.content.Context
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.assessment.models.Home
import com.bdjobs.app.assessment.models.Schedule
import com.bdjobs.app.assessment.network.AssessmentApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeRepository(context: Context) {

    val bdjobsUserSession : BdjobsUserSession = BdjobsUserSession(context)

    suspend fun getHomeData() : Home {
        return withContext(Dispatchers.IO){
            AssessmentApi.retrofitService.getHomeDetailsFromAPI(
                    userID = bdjobsUserSession.userId,
                    decodeID = bdjobsUserSession.decodId,
                    postingDate = bdjobsUserSession.AppsDate
            )
        }
    }

}