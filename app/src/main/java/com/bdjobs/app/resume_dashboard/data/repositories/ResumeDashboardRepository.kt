package com.bdjobs.app.resume_dashboard.data.repositories

import android.app.Application
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.UploadResume
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.utilities.Constants
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

    private val bdJobsUserSession = BdjobsUserSession(application.applicationContext)

    suspend fun manageResumeStats(): ManageResumeStats {
        return withContext(Dispatchers.IO) {
            ApiServiceMyBdjobs.create().manageResumeStats(
                bdJobsUserSession.userId, bdJobsUserSession.decodId, "1"
            )
        }
    }

    suspend fun resumePrivacyStatus(): ResumePrivacyStatus {
        return withContext(Dispatchers.IO) {
            ApiServiceMyBdjobs.create()
                .resumePrivacyStatus(bdJobsUserSession.userId, bdJobsUserSession.decodId)
        }
    }

    suspend fun manageResumeDetailsStat(isCVPosted:String): ManageResumeDetailsStat {
        return withContext(Dispatchers.IO) {
            ApiServiceMyBdjobs.create().manageResumeDetailsStat(
                bdJobsUserSession.userId,
                bdJobsUserSession.decodId,
                isCVPosted
            )
        }
    }

    suspend fun getQuestionListFromRemote(): VideoResumeQuestionList {
        return withContext(Dispatchers.IO) {
            VideoResumeApiService.create(application).getVideoResumeQuestionList(
                    userID = bdJobsUserSession.userId,
                    decodeID = bdJobsUserSession.decodId,
                    appId = Constants.APP_ID,
                    lang = "EN"
            )
        }
    }

    suspend fun downloadCV(status:String) : UploadResume {
        return  withContext(Dispatchers.IO) {
            ApiServiceMyBdjobs.create().downloadCv(
                bdJobsUserSession.userId,bdJobsUserSession.decodId,status
            )
        }
    }
 }