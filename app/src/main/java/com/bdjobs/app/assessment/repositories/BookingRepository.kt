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

    suspend fun manageSchedule() : BookingResponse{
        return withContext(Dispatchers.IO){
            AssessmentApi.retrofitService.bookSchedule(
                    userID = bdjobsUserSession.userId,
                    decodeID = bdjobsUserSession.decodId,
                    actionType = booking.strActionType,
                    scID = booking.scId,
                    schID = booking.schId,
                    opID = booking.opId,
                    amount = booking.fltBdjAmount,
                    transactionDate = booking.strTransactionDate,
                    isFromHome = "1"
            )
        }
    }


}