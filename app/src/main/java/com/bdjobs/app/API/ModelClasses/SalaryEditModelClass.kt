package com.bdjobs.app.API.ModelClasses
import com.google.gson.annotations.SerializedName


data class AppliedJobsSalaryEdit(
    @SerializedName("common")
    val common: Any?,
    @SerializedName("data")
    val `data`: Any?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("statuscode")
    val statuscode: String?
)