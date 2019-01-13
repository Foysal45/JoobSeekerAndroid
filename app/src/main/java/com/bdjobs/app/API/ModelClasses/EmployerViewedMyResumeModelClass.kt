package com.bdjobs.app.API.ModelClasses
import com.google.gson.annotations.SerializedName


data class EmplyerViewMyResume(
    @SerializedName("common")
    val common: EmplyerViewMyResumeCommon?,
    @SerializedName("data")
    val `data`: List<EmplyerViewMyResumeData?>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("statuscode")
    val statuscode: String?
)

data class EmplyerViewMyResumeData(
    @SerializedName("companyName")
    val companyName: String?,
    @SerializedName("sl")
    val sl: String?,
    @SerializedName("summaryView")
    val summaryView: String?,
    @SerializedName("viewedOn")
    val viewedOn: String?
)

data class EmplyerViewMyResumeCommon(
    @SerializedName("totalNumberOfItems")
    val totalNumberOfItems: String?,
    @SerializedName("totalNumberOfPage")
    val totalNumberOfPage: String?
)