package com.bdjobs.app.videoInterview.data.repository

import android.app.Application
import android.util.Log
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.videoInterview.data.models.VideoInterviewListModel
import com.bdjobs.app.videoInterview.data.remote.VideoInterviewApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VideoInterviewRepository(val application: Application)  {

    val session = BdjobsUserSession(application)
}

class InterviewListRepository(val application: Application)  {

    val bdjobsUserSession : BdjobsUserSession = BdjobsUserSession(application)

    suspend fun getInterviewList() : VideoInterviewListModel {



          return withContext(Dispatchers.IO){

              VideoInterviewApiService.create(application).getInterviewListFromAPI("","")


          }

    }
}