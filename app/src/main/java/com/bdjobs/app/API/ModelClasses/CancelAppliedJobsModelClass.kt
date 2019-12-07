package com.bdjobs.app.API.ModelClasses
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CancelAppliedJobs(
    @SerializedName("common")
    val common: Any?,
    @SerializedName("data")
    val `data`: Any?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("statuscode")
    val statuscode: String?
)