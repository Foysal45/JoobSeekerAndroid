package com.bdjobs.app.sms.data.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class SMSSettings(
    @Json(name = "common")
    val common: Any?, // null
    @Json(name = "data")
    val `data`: List<SMSSettingsData>?,
    @Json(name = "message")
    val message: String?, // Success
    @Json(name = "statuscode")
    val statuscode: String? // 0
)

//@Keep
//data class SMSSettingsData(
//        @Json(name = "dailySmsLimit")
//        val dailySmsLimit: String?, // 5
//        @Json(name = "remainingSMSAmount")
//        val remainingSMSAmount: String?, // 12
//        @Json(name = "smsAlertOn")
//        val smsAlertOn: String?, // True
//        @Json(name = "totalSMSAmount")
//        val totalSMSAmount: String? // 20
//)

@Keep
data class SMSSettingsData(
    @Json(name = "dailySmsLimit")
    val dailySmsLimit: String?,
    @Json(name = "isFavSearchEnable  ")
    val isFavSearchEnable: String?,
    @Json(name = "isFollowedEmployerEnable ")
    val isFollowedEmployerEnable: String?,
    @Json(name = "isMatchedJobEnable")
    val isMatchedJobEnable: String?,
    @Json(name = "probableRemainingDay ")
    val probableRemainingDay: String?,
    @Json(name = "remainingSMSAmount")
    val remainingSMSAmount: String?,
    @Json(name = "smsAlertOn")
    val smsAlertOn: String?,
    @Json(name = "totalSMSAmount")
    val totalSMSAmount: String?,
    @Json(name = "trialConsumed  ")
    val trialConsumed: String?
)