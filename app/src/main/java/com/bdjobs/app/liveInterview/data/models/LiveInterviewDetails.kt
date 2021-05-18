package com.bdjobs.app.liveInterview.data.models


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class LiveInterviewDetails(
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
            @Json(name = "applyDate")
            val applyDate: String?, // 20 Apr 2020
            @Json(name = "applyId")
            val applyId: String?, // 182907384
            @Json(name = "jobClosed")
            val jobClosed: String?, // false
            @Json(name = "rating")
            val rating: String?,
            @Json(name = "ratingDate")
            val ratingDate: String?,
            @Json(name = "ratingMessage")
            val ratingMessage: String?,
            @Json(name = "showUndo")
            val showUndo: String?, // 0
            @Json(name = "companyName")
            val companyName: String?, // 0,
            @Json(name = "jobTitle")
            val jobTitle: String?, // 0
            @Json(name = "jobId")
            val jobId: String? // 0,
    )

    @Keep
    data class Data(
            @Json(name = "activity")
            val activity: String?, // 3
            @Json(name = "alertMessage")
            val alertMessage: String?,
            @Json(name = "confimationStatus")
            val confimationStatus: String?, // 1
            @Json(name = "confirmationDate")
            val confirmationDate: String?, // 7 Sep 2020
            @Json(name = "confirmationMessage")
            val confirmationMessage: String?, // You have confirmed your scheduled interview.
            @Json(name = "contactNo")
            val contactNo: String?,
            @Json(name = "examDate")
            val examDate: String?, // 7 Sep 2020
            @Json(name = "examMessage")
            val examMessage: String?, // Congratulation! You have been selected for an interview and it's scheduled
            @Json(name = "examTime")
            val examTime: String?, // 18:30:00
            @Json(name = "examTitle")
            val examTitle: String?, // Video interview3
            @Json(name = "invitationDate")
            val invitationDate: String?, // 7 Sep 2020
            @Json(name = "invitationId")
            val invitationId: String?, // 39413
            @Json(name = "processId")
            val processId:String?, // 6464177
            @Json(name = "previousScheduleDate")
            val previousScheduleDate: String?, // 7 Sep 2020
            @Json(name = "previousScheduleTime")
            val previousScheduleTime: String?, // 16:30:00
            @Json(name = "testType")
            val testType: String?, // video
            var showUndo: String?
    )
}