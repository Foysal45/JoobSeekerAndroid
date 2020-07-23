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
        @Json(name = "dailySmsLimit")
        val dailySmsLimit: String?, // 5
        @Json(name = "remainingSMSAmount")
        val remainingSMSAmount: String?, // 12
        @Json(name = "smsAlertOn")
        val smsAlertOn: String?, // True
        @Json(name = "totalSMSAmount")
        val totalSMSAmount: String? // 20
)