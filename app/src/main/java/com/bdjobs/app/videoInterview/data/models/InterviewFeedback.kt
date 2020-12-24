package com.bdjobs.app.videoInterview.data.models

import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class InterviewFeedback(
        @Json(name = "statuscode")
        val statuscode: String?, // 0
        @Json(name = "message")
        val message: String?, // Success
)