package com.bdjobs.app.API.ModelClasses

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ApplyEligibilityModel(
        @SerializedName("data")
        var `data`: List<Data?>? = listOf(),
        @SerializedName("message")
        var message: String? = "",
        @SerializedName("statuscode")
        var statuscode: String? = ""
)
@Keep
data class Data(
        @SerializedName("applyEligibility")
        var applyEligibility: String? = "",
        @SerializedName("message")
        var message: String? = "",
        @SerializedName("title")
        var title: String? = ""
        )