package com.bdjobs.app.videoResume.data.models

import androidx.annotation.Keep
import com.squareup.moshi.JsonClass


@Keep
@JsonClass(generateAdapter = true)
data class Guideline(
        var text: String
)
