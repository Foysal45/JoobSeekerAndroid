package com.bdjobs.app.videoInterview.data.models


import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

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
            var applyId: String?, // 182982535
            @Json(name = "companyName")
            var companyName: String?, // Utopia BD Ltd
            @Json(name = "isShowSubmitNowButton")
            var isShowSubmitNowButton: String?, // False
            @Json(name = "isUserSubmitAnswer")
            var userSubmittedAnswer: String?, // True
            @Json(name = "jobId")
            var jobId: String?, // 854375
            @Json(name = "jobTitle")
            var jobTitle: String?, // hot job
            @Json(name = "remaingTime")
            var remaingTime: String?, // 0
            @Json(name = "remainingExtraAttempt")
            var remainingExtraAttempt: String?, // 2
            @Json(name = "totalAttempt")
            var totalAttempt: String?, // 2
            @Json(name = "submitNowButtonStatus")
            var submitNowButtonStatus: String?,
            @Json(name = "totalQuestion")
            var totalQuestion: String?, // 3
            @Json(name = "vUserTotalAnswerequestion")
            var vUserTotalAnswerequestion: String?, // 2
            @Json(name = "videoInterviewDeadline")
            var videoInterviewDeadline: String? // 30 Jun 2020
    )

    @Keep
    @JsonClass(generateAdapter = true)
    data class Data(
            @Json(name = "buttonName")
            var buttonName: String?, // View Video
            @Json(name = "employerActivity")
            var employerActivity: String?,
            @Json(name = "questionDuration")
            var questionDuration: String?, // 20
            @Json(name = "questionId")
            var questionId: String?, // 918
            @Json(name = "questionSerialNo")
            var questionSerialNo: String?, // 1
            @Json(name = "questionText")
            var questionText: String?, // Question 1
            @Json(name = "videoUrl")
            var videoUrl: String?, // https://vdo.bdjobs.com/Videos/Corporate//854375/182982535/182982535_1.webm
            @Json(name = "questionStatus")
            var questionStatus: String?, // 1
            @Json(name = "buttonStatus")
            var buttonStatus: String? // 1
    )
}