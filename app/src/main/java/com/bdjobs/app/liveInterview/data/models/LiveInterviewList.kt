package com.bdjobs.app.liveInterview.data.models


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class LiveInterviewList(
    @Json(name = "common")
    val common: Common?,
    @Json(name = "data")
    val `data`: List<Data?>?,
    @Json(name = "message")
    val message: String?, // Success
    @Json(name = "statuscode")
    val statuscode: String? // 0
) {
    @Keep
    data class Common(
        @Json(name = "totalLiveInterview")
        val totalLiveInterview: String? // 8
    )

    @Keep
    data class Data(
        @Json(name = "companyName")
        val companyName: String?, // Utopia BD Ltd 2
        @Json(name = "dateStringForInvitaion")
        val dateStringForInvitaion: String?, // 8 Sep 2020
        @Json(name = "jobId")
        val jobId: String?, // 896921
        @Json(name = "jobTitle")
        val jobTitle: String?, // Salary
        @Json(name = "liveInterviewDate")
        val liveInterviewDate: String?, // 10 Sep 2020
        @Json(name = "liveInterviewStatus")
        val liveInterviewStatus: String?, // 1 Day Remaining
        @Json(name = "liveInterviewStatusCode")
        val liveInterviewStatusCode: String?, // 2
        @Json(name = "liveInterviewTime")
        val liveInterviewTime: String?, // 12:30:00
        @Json(name = "userSeenLiveInterview")
        val userSeenLiveInterview: String? // True
    )
}