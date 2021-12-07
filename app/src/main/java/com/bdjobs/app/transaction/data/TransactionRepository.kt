package com.bdjobs.app.transaction.data

import android.app.Application
import android.util.Log
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.utilities.Constants
import com.bdjobs.app.transaction.data.model.TransactionList
import com.bdjobs.app.videoInterview.data.models.VideoManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TransactionRepository(val application: Application) {

    val session = BdjobsUserSession(application)
    var videoManager: VideoManager? = null

    suspend fun getTransactionList(startDate: String, endDate: String, type: String): TransactionList {
        Log.d("saklnflsan"," Repository $startDate,endDate $endDate,type $type")

        return withContext(Dispatchers.IO) {
            ApiServiceMyBdjobs.create().getTransactionList(
                    userId = session.userId,
                    decodeId = session.decodId,
                    appId = Constants.APP_ID ,
                    startDate = startDate,
                    endDate = endDate,
                    packageTypeId = type,
                    pageNumber = "1",
                    itemsPerPage = "20"
            )
        }
    }

}