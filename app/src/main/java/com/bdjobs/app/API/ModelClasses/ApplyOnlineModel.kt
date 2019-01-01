package com.bdjobs.app.API.ModelClasses

import com.google.gson.annotations.SerializedName


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

data class ApplyOnlineDataModel(
        @SerializedName("AssessmentDeadline")
        val assessmentDeadline: String,
        @SerializedName("assessmentMessage")
        val assessmentMessage: String,
        @SerializedName("message")
        val message: String,
        @SerializedName("status")
        val status: String
)