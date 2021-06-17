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
            @Json(name = "slNo")
            var slNo: String?,
            @Json(name = "questionSerialNo")
            var questionSerialNo: String?,
            @Json(name = "questionId")
            var questionId: String?,
            @Json(name = "questionDuration")
            var questionDuration: String?,
            @Json(name = "questionText")
            var questionText: String?,
            @Json(name = "questionTextBng")
            var questionTextBng: String?,
            @Json(name = "questionStatus")
            var questionStatus: String?,
            @Json(name = "videoUrl")
            var videoUrl: String?,
            @Json(name = "buttonStatus")
            var buttonStatus: String?,
            @Json(name = "buttonName")
            var buttonName: String?,
            @Json(name = "aID")
            var aID: String?,
            @Json(name = "totalView")
            var totalView: String?
    )
}