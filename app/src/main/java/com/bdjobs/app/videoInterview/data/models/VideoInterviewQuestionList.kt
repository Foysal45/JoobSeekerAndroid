package com.bdjobs.app.videoInterview.data.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class VideoInterviewQuestionList(
    @Json(name = "common")
    var common: Common?,
    @Json(name = "data")
    var `data`: List<Data?>?,
    @Json(name = "message")
    var message: String?, // Success
    @Json(name = "statuscode")
    var statuscode: String? // 0
) {
    @Keep
    @JsonClass(generateAdapter = true)
    data class Common(
        @Json(name = "applyId")
        var applyId: String?, // 182982592
        @Json(name = "isShowDownloadAllButton")
        var isShowDownloadAllButton: String?, // False
        @Json(name = "isShowNotInterestedButton")
        var isShowNotInterestedButton: String?, // True
        @Json(name = "isShowSubmitLaterButton")
        var isShowSubmitLaterButton: String?, // False
        @Json(name = "isShowSubmitNowButton")
        var isShowSubmitNowButton: String?, // False
        @Json(name = "JobId")
        var jobId: String?, // 869795
        @Json(name = "remaingTime")
        var remaingTime: String?, // 0
        @Json(name = "totalQuestion")
        var totalQuestion: String?, // 1
        @Json(name = "vDeadline")
        var vDeadline: String?, // 6/8/2020
        @Json(name = "vUserTotalAnswerequestion")
        var vUserTotalAnswerequestion: String? // 0
    )

    @Keep
    @JsonClass(generateAdapter = true)
    data class Data(
        @Json(name = "buttonName")
        var buttonName: String?, // Record Video
        @Json(name = "employerActivity")
        var employerActivity: String?,
        @Json(name = "questionDuration")
        var questionDuration: String?, // 180
        @Json(name = "questionId")
        var questionId: String?, // 342
        @Json(name = "questionSerialNo")
        var questionSerialNo: String?, // 1
        @Json(name = "questionText")
        var questionText: String?, // Nira Nobboi tk niye bazar korte giye,nobboi tkr bazar korlo. Or kachhe r koto tk achhe
        @Json(name = "remainingExtraAttempt")
        var remainingExtraAttempt: String?,
        @Json(name = "videoUrl")
        var videoUrl: String?
    )
}