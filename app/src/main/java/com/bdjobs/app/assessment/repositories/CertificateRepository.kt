package com.bdjobs.app.assessment.repositories

import android.content.Context
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.assessment.models.Certificate
import com.bdjobs.app.assessment.models.Post
import com.bdjobs.app.assessment.network.AssessmentApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CertificateRepository(context: Context){

    val bdjobsUserSession : BdjobsUserSession = BdjobsUserSession(context)

    suspend fun getCertificateList() : Certificate{
        return withContext(Dispatchers.IO){
           AssessmentApi.retrofitService.getCertificates(userID = "241028", decodeID = "T8B8Rx")
        }
    }

    suspend fun getPosts() : List<Post>{
        return withContext(Dispatchers.IO){
            AssessmentApi.retrofitService.getPosts()
        }
    }
}