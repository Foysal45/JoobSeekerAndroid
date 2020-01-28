package com.bdjobs.app.assessment.models


import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class Certificate(
    @Json(name = "common")
    var common: Any?,
    @Json(name = "data")
    var data: List<Data?>?,
    @Json(name = "message")
    var message: String?,
    @Json(name = "statuscode")
    var statuscode: String?
)