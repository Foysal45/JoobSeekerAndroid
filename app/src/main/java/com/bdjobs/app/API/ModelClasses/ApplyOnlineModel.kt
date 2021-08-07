package com.bdjobs.app.API.ModelClasses

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ApplyOnlineModel(
    @SerializedName("common")
    val common: Any,
    @SerializedName("data")
    val `data`: List<ApplyOnlineDataModel>,
    @SerializedName("message")
    val message: String,
    @SerializedName("statuscode")
    val statuscode: String
)

@Keep
data class ApplyOnlineDataModel(
    @SerializedName("AssessmentDeadline")
    val assessmentDeadline: String,
    @SerializedName("assessmentMessage")
    val assessmentMessage: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("UpdateResume")
    var UpdateResume: String? = ""
)