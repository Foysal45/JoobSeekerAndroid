package com.bdjobs.app.API.ModelClasses
import com.google.gson.annotations.SerializedName


data class EmpVwdResume(
    @SerializedName("common")
    val common: EmpVwdResumeCommon?,
    @SerializedName("data")
    val `data`: List<EmpVwdResumeData?>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("statuscode")
    val statuscode: String?
)

data class EmpVwdResumeCommon(
    @SerializedName("totalNumberOfItems")
    val totalNumberOfItems: String?,
    @SerializedName("totalNumberOfPage")
    val totalNumberOfPage: String?
)

data class EmpVwdResumeData(
    @SerializedName("companyName")
    val companyName: String?,
    @SerializedName("detailView")
    val detailView: String?,
    @SerializedName("sl")
    val sl: String?,
    @SerializedName("summaryView")
    val summaryView: String?,
    @SerializedName("viewedOn")
    val viewedOn: String?
)