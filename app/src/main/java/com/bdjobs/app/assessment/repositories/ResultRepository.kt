package com.bdjobs.app.assessment.repositories

import android.content.Context
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.assessment.models.CertificateData
import com.bdjobs.app.assessment.models.Result
import com.bdjobs.app.assessment.network.AssessmentApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ResultRepository(context: Context, var certificateData: CertificateData){

    val bdjobsUserSession : BdjobsUserSession = BdjobsUserSession(context)

    suspend fun getResult() : Result{
        return withContext(Dispatchers.IO){
            AssessmentApi.retrofitService.getResultFromAPI(
                    userID = bdjobsUserSession.userId,
                    decodeID = bdjobsUserSession.decodId,
                    scheduleID = certificateData.scheduleId,
                    amcatJobId =certificateData.amcatJobId,
                    assessmentId = certificateData.assessmentId,
                    jobId = certificateData.jobId,
                    jobRoleId = certificateData.jobRoleId,
                    res = certificateData.res,
                    strExamType = certificateData.strExamType
            )
        }
    }

    suspend fun updateResult(actionType : String) : Result{
        return withContext(Dispatchers.IO){
            AssessmentApi.retrofitService.updateResult(
                    userID = bdjobsUserSession.userId,
                    decodeID = bdjobsUserSession.decodId,
                    scheduleID = certificateData.scheduleId,
                    assessmentId = certificateData.assessmentId,
                    jobRoleId = certificateData.jobRoleId,
                    actionType = actionType
            )
        }
    }
}