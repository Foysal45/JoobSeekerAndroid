package com.bdjobs.app.assessment.repositories

import android.content.Context
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.assessment.models.Booking
import com.bdjobs.app.assessment.models.BookingResponse
import com.bdjobs.app.assessment.network.AssessmentApi
import com.bdjobs.app.assessment.network.AssessmentApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BookingRepository(context: Context,var booking: Booking) {

    val bdjobsUserSession : BdjobsUserSession = BdjobsUserSession(context)

    suspend fun bookSchedule() : BookingResponse{
        return withContext(Dispatchers.IO){
            AssessmentApi.retrofitService.bookSchedule(
                    userID = bdjobsUserSession.userId,
                    decodeID = bdjobsUserSession.decodId,
                    actionType = "I",
                    scID = "0",
                    schID = "15576",
                    opID = booking.opId,
                    amount = booking.fltBdjAmount,
                    transactionDate = booking.strTransactionDate,
                    isFromHome = "1"
            )
        }
    }


}