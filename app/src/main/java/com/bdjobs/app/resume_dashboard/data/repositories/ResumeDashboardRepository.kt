package com.bdjobs.app.resume_dashboard.data.repositories

import android.app.Application
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.resume_dashboard.data.models.ManageResumeDetailsStat
import com.bdjobs.app.resume_dashboard.data.models.ManageResumeStats
import com.bdjobs.app.resume_dashboard.data.models.ResumePrivacyStatus
import com.bdjobs.app.videoResume.data.models.VideoResumeQuestionList
import com.bdjobs.app.videoResume.data.remote.VideoResumeApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//
// Created by Soumik on 6/20/2021.
// piyal.developer@gmail.com
// Copyright (c) 2021 Bdjobs.com Ltd. All rights reserved.
//

class ResumeDashboardRepository(val application: Application) {

    private val bdjobsUserSession = BdjobsUserSession(application.applicationContext)

    suspend fun manageResumeStats(): ManageResumeStats {
        return withContext(Dispatchers.IO) {
            ApiServiceMyBdjobs.create().manageResumeStats(
                bdjobsUserSession.userId, bdjobsUserSession.decodId, "1"
            )
        }
    }

    suspend fun resumePrivacyStatus(): ResumePrivacyStatus {
        return withContext(Dispatchers.IO) {
            ApiServiceMyBdjobs.create()
                .resumePrivacyStatus(bdjobsUserSession.userId, bdjobsUserSession.decodId)
        }
    }

    suspend fun manageResumeDetailsStat(): ManageResumeDetailsStat {
        return withContext(Dispatchers.IO) {
            ApiServiceMyBdjobs.create().manageResumeDetailsStat(
                bdjobsUserSession.userId,
                bdjobsUserSession.decodId,
                bdjobsUserSession.isCvPosted
            )
        }
    }

    suspend fun getQuestionListFromRemote(): VideoResumeQuestionList {
        return withContext(Dispatchers.IO) {
            VideoResumeApiService.create(application).getVideoResumeQuestionList(
                    userID = bdjobsUserSession.userId,
                    decodeID = bdjobsUserSession.decodId,
                    appId = Constants.APP_ID,
                    lang = "EN"
            )
        }
    }
}