package com.bdjobs.app.videoResume.data.models

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class VideoResumeStatistics(
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
            @Json(name = "videoStatusPercentage")
            var statusPercentage: String?,
            @Json(name = "videoLastUpdateDate")
            var lastUpdateDate: String?,
            @Json(name = "totalView")
            var totalView: String?,
            @Json(name = "totalCompanyView")
            var totalCompanyView: String?,
            @Json(name = "rating")
            var rating: String?,
            @Json(name = "totalQuestion")
            var totalQuestion: String?,
            @Json(name = "totalAnswered")
            var totalAnswered: String?,
            @Json(name = "threshold")
            var threshold: String?,
            @Json(name = "resumeVisibility")
            var resumeVisibility: String?
    )
}