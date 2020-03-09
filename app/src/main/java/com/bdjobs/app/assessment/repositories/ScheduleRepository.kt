package com.bdjobs.app.assessment.repositories

import android.content.Context
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.assessment.models.Schedule
import com.bdjobs.app.assessment.models.ScheduleRequest
import com.bdjobs.app.assessment.network.AssessmentApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScheduleRepository(context: Context, var scheduleRequest: ScheduleRequest) {

    val bdjobsUserSession : BdjobsUserSession = BdjobsUserSession(context)

    suspend fun getScheduleList() : Schedule{
        return withContext(Dispatchers.IO){
            AssessmentApi.retrofitService.getScheduleFromAPI(
                    userID = bdjobsUserSession.userId,
                    decodeID = bdjobsUserSession.decodId,
                    pageNo = scheduleRequest.pageNo,
                    pageSize = scheduleRequest.pageSize,
                    fromDate = scheduleRequest.fromDate,
                    toDate = scheduleRequest.toDate,
                    venue = scheduleRequest.venue
            )
        }
    }

}