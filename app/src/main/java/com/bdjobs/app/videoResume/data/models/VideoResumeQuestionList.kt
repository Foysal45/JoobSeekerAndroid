package com.bdjobs.app.videoResume.data.models

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class VideoResumeQuestionList(
        @Json(name = "common")
        var common: Any?,
        @Json(name = "data")
        var `data`: List<Data?>?,
        @Json(name = "message")
        var message: String?, // Success
        @Json(name = "statuscode")
        var statuscode: String? // 0
) {
    @Keep
    @JsonClass(generateAdapter = true)
    data class Data(
            @Json(name = "questionSerialNo")
            var questionSerialNo: String?, // 1
            @Json(name = "questionId")
            var questionId: String?, // 918
            @Json(name = "questionDuration")
            var questionDuration: String?, // 20
            @Json(name = "questionText")
            var questionText: String?, // Question 1
            @Json(name = "questionStatus")
            var questionStatus: String?, // 1
            @Json(name = "videoUrl")
            var videoUrl: String?, // https://vdo.bdjobs.com/Videos/Corporate//854375/182982535/182982535_1.webm
            @Json(name = "buttonStatus")
            var buttonStatus: String?, // 1
            @Json(name = "buttonName")
            var buttonName: String?, // View Video
            @Json(name = "totalView")
            var totalView: String?, // View Video
    )
}