package com.bdjobs.app.resume_dashboard.data.models
import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class PersonalizedResumeStat(
    @Json(name = "common")
    val common: Any?,
    @Json(name = "data")
    val `data`: List<DataPRS>?,
    @Json(name = "message")
    val message: String?,
    @Json(name = "statuscode")
    val statuscode: String?
)

@Keep
data class DataPRS(
    @Json(name = "personalizedCalculatedFromDate")
    val personalizedCalculatedFromDate: String?,
    @Json(name = "personalizedDownload")
    val personalizedDownload: String?,
    @Json(name = "personalizedEmailed")
    val personalizedEmailed: String?,
    @Json(name = "personalizefileType")
    val personalizefileType: String?,
    @Json(name = "personalizedFileName")
    val personalizedFileName: String?,
    @Json(name = "personalizedFilePath")
    val personalizedFilePath: String?,
    @Json(name = "personalizedLastUpdateDate")
    val personalizedLastUpdateDate: String?,
    @Json(name = "personalizedViewed")
    val personalizedViewed: String?
)