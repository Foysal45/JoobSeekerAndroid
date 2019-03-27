package com.bdjobs.app.API.ModelClasses
import com.google.gson.annotations.SerializedName


data class TimesEmailed(
    @SerializedName("common")
    val common: TimesEmailedCommon?,
    @SerializedName("data")
    val `data`: List<TimesEmailedData?>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("statuscode")
    val statuscode: String?
)

data class TimesEmailedCommon(
    @SerializedName("totalNumberOfEmail")
    val totalNumberOfEmail: String?,
    @SerializedName("totalNumberOfPage")
    val totalNumberOfPage: String?
)

data class TimesEmailedData(
    @SerializedName("EmailTo")
    val emailTo: String? = "",
    @SerializedName("EmailedOn")
    val emailedOn: String? = "",
    @SerializedName("jobid")
    val jobid: String? = "",
    @SerializedName("sl")
    val sl: String? = "",
    @SerializedName("subject")
    val subject: String? = "",
    @SerializedName("version")
    val version: String? = ""
)