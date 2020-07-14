package com.bdjobs.app.sms.data.repository

import android.app.Application
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.sms.data.model.SMSSettings
import com.bdjobs.app.videoInterview.data.models.CommonResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SMSRepository(private val application: Application) {

    val session = BdjobsUserSession(application)

    fun getFullName(): String? {
        return session.fullName
    }

    suspend fun getSMSSettings(): SMSSettings {
        return withContext(Dispatchers.IO) {
            ApiServiceMyBdjobs.create().getSMSSetting(
                    userID = session.userId,
                    decodeID = session.decodId,
                    appId = Constants.APP_ID
            )
        }
    }

    suspend fun updateSMSSettings(dailyLimit: Int?, alertOn: Int?): CommonResponse {
        return withContext(Dispatchers.IO) {
            ApiServiceMyBdjobs.create().updateSMSSettings(
                    userID = session.userId,
                    decodeID = session.decodId,
                    appId = Constants.APP_ID,
                    dailySMSLimit = dailyLimit,
                    alertOn = alertOn
            )
        }
    }
}