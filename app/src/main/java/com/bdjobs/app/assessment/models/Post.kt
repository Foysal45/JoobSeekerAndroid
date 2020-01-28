package com.bdjobs.app.assessment.models

import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class Post(
        @Json(name = "body")
        var body: String?,
        @Json(name = "id")
        var id: Int?,
        @Json(name = "title")
        var title: String?,
        @Json(name = "userId")
        var userId: Int?
)