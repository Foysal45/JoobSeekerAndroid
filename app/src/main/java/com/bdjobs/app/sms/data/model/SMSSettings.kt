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

@Keep
data class SMSSettingsData(
    @field:Json(name = "dailySmsLimit")
    val dailySmsLimit: String?,
    @field:Json(name = "isFavSearchEnable")
    val isFavSearchEnable: String?,
    @field:Json(name = "isFollowedEmployerEnable")
    val isFollowedEmployerEnable: String?,
    @field:Json(name = "isMatchedJobEnable")
    val isMatchedJobEnable: String?,
    @field:Json(name = "probableRemainingDay")
    val probableRemainingDay: String?,
    @field:Json(name = "remainingSMSAmount")
    val remainingSMSAmount: String?,
    @field:Json(name = "smsAlertOn")
    val smsAlertOn: String?,
    @field:Json(name = "totalSMSAmount")
    val totalSMSAmount: String?,
    @field:Json(name = "trialConsumed")
    val trialConsumed: String?
)