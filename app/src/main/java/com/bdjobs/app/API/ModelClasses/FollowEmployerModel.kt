package com.bdjobs.app.API.ModelClasses
import com.google.gson.annotations.SerializedName


data class FollowEmployerListModelClass(
    @SerializedName("common")
    val common: Common?,
    @SerializedName("data")
    val `data`: List<FollowEmployerListData?>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("statuscode")
    val statuscode: String?
)

data class Common(
    @SerializedName("TotalCompnay")
    val totalCompnay: String?,
    @SerializedName("TotalCount")
    val totalCount: String?
)

data class FollowEmployerListData(
    @SerializedName("CompanyID")
    val companyID: String?,
    @SerializedName("CompanyName")
    val companyName: String?,
    @SerializedName("JobCount")
    val jobCount: String?
)