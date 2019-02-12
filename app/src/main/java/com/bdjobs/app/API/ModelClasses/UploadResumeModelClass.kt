package com.bdjobs.app.API.ModelClasses
import com.google.gson.annotations.SerializedName


data class UploadResume(
    @SerializedName("common")
    val common: String?,
    @SerializedName("data")
    val `data`: List<UploadResume?>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("statuscode")
    val statuscode: String?
)

data class UploadResumeData(
    @SerializedName("path")
    val path: String?
)