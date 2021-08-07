package com.bdjobs.app.liveInterview.data.models

import androidx.annotation.Keep
import com.squareup.moshi.Json
@Keep
data class StartEndCallModel(
    @Json(name = "Error")
    val error: String?,
    @Json(name = "Message")
    val message: String,
    @Json(name = "RequestFrom")
    val requestFrom: String
)