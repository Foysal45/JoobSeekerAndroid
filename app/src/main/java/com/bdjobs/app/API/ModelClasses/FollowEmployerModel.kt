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
        val totalCount: String?,
        @SerializedName("total_records_found")
        val total_records_found: String?="",
        @SerializedName("TotalJobCount")
        val TotalJobCount: String?="",
        @SerializedName("totalpages")
        val totalpages: String?=""
)

data class FollowEmployerListData(
        @SerializedName("CompanyID")
        val companyID: String?,
        @SerializedName("CompanyName")
        val companyName: String?,
        @SerializedName("FollowedOn")
        val followedOn: String?,
        @SerializedName("JobCount")
        val jobCount: String?

)