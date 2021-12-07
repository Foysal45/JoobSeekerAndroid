package com.bdjobs.app.sms.data.repository

import android.app.Application
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.utilities.Constants
import com.bdjobs.app.sms.data.model.PaymentInfoAfterGateway
import com.bdjobs.app.sms.data.model.PaymentInfoBeforeGateway
import com.bdjobs.app.sms.data.model.SMSSettings
import com.bdjobs.app.videoInterview.data.models.CommonResponse
import com.sslwireless.sslcommerzlibrary.model.response.TransactionInfoModel
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

    suspend fun updateSMSSettings(
        dailyLimit: Int?, alertOn: Int?, isMatchedJobEnable: String?,
        isFollowedEmployerEnable: String?,
        isFavSearchEnable: Int?
    ): CommonResponse {
        return withContext(Dispatchers.IO) {
            ApiServiceMyBdjobs.create().updateSMSSettings(
                userID = session.userId,
                decodeID = session.decodId,
                appId = Constants.APP_ID,
                dailySMSLimit = dailyLimit,
                alertOn = alertOn,
                isMatchedJobEnable = isMatchedJobEnable,
                isFavSearchEnable = isFavSearchEnable,
                isFollowedEmployerEnable = isFollowedEmployerEnable
            )
        }
    }

    suspend fun callPaymentInfoBeforeGatewayApi(
        totalSMS: Int?,
        totalTaka: Int?,
        isFree: String?
    ): PaymentInfoBeforeGateway {
        return withContext(Dispatchers.IO) {
            ApiServiceMyBdjobs.create().paymentInfoBeforeEnteringGateway(
                userId = session.userId,
                decodeId = session.decodId,
                appId = Constants.APP_ID,
                serviceId = Constants.SMS_SERVICE_ID,
                totalQuantity = totalSMS.toString(),
                totalAmount = totalTaka.toString(),
                isFree = isFree
            )
        }
    }

    suspend fun callPaymentAfterReturningGatewayApi(data: TransactionInfoModel?): PaymentInfoAfterGateway {
        return withContext(Dispatchers.IO) {
            ApiServiceMyBdjobs.create().paymentInfoAfterReturningGateway(
                userId = session.userId,
                decodeId = session.decodId,
                appId = Constants.APP_ID,
                tranId = data?.tranId,
                cardType = data?.cardType,
                storeAmount = data?.storeAmount,
                valId = data?.valId,
                status = data?.status,
                currencyType = data?.currencyType,
                tranDate = data?.tranDate
            )
        }
    }


}