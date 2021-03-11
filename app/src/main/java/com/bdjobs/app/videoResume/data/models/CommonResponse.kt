package com.bdjobs.app.videoResume.data.models

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class CommonResponse(
        @Json(name = "common")
        var common: Any?,
        @Json(name = "data")
        var data: Any?,
        @Json(name = "message")
        var message: Any?, // Success
        @Json(name = "statuscode")
        var statuscode: Any? // 0
)
