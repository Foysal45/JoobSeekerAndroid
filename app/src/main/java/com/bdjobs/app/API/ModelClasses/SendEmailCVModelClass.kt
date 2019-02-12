package com.bdjobs.app.API.ModelClasses
import com.google.gson.annotations.SerializedName


data class SendEmailCV(
    @SerializedName("common")
    val common: String?,
    @SerializedName("data")
    val `data`: String?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("statuscode")
    val statuscode: String?
)