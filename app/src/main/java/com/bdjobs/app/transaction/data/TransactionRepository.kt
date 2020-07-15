package com.bdjobs.app.transaction.data

import android.app.Application
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.transaction.data.model.Transaction
import com.bdjobs.app.videoInterview.data.models.VideoInterviewDetails
import com.bdjobs.app.videoInterview.data.models.VideoManager
import com.bdjobs.app.videoInterview.data.remote.VideoInterviewApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call

class TransactionRepository(val application: Application) {

    val session = BdjobsUserSession(application)
    var videoManager: VideoManager? = null

    suspend fun getTransactionList(jobId: String?): Call<Transaction> {
        return withContext(Dispatchers.IO) {
            ApiServiceMyBdjobs.create(application).get_transaction_list(
                   session.userId,
                    session.decodId,
                     jobId,
                    "","","",""
            )
        }
    }

}