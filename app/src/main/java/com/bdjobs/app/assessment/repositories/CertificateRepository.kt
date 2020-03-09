package com.bdjobs.app.assessment.repositories

import android.content.Context
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.assessment.models.Certificate
import com.bdjobs.app.assessment.network.AssessmentApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CertificateRepository(context: Context){

    val bdjobsUserSession : BdjobsUserSession = BdjobsUserSession(context)

    suspend fun getCertificateList() : Certificate{
        return withContext(Dispatchers.IO){
           AssessmentApi.retrofitService.getCertificatesFromAPI(userID = bdjobsUserSession.userId, decodeID = bdjobsUserSession.decodId)
        }
    }
}