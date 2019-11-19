package com.bdjobs.app.API.ModelClasses
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class UploadResume(
    @SerializedName("common")
    val common: String?,
    @SerializedName("data")
    val `data`: List<UploadResumeData?>?,
    @SerializedName("message")
    val message: String?="",
    @SerializedName("statuscode")
    val statuscode: String?
)
@Keep
data class UploadResumeData(
    @SerializedName("path")
    val path: String?
)