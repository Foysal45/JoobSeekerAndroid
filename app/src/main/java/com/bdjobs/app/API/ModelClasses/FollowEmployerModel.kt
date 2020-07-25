package com.bdjobs.app.API.ModelClasses

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName







/*@Keep
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
@Keep
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
@Keep
data class FollowEmployerListData(
        @SerializedName("CompanyID")
        val companyID: String? = "",
        @SerializedName("CompanyName")
        val companyName: String? = "",
        @SerializedName("FollowedOn")
        val followedOn: String? = "",
        @SerializedName("isSubscribed")
        val isSubscribed: String ="",
        @SerializedName("JobCount")
        val jobCount: String? = ""

)*/
/*
@Keep
data class FollowEmployerListModelClass(
    @SerializedName("common")
    val common: Common?,
    @SerializedName("data")
    val `data`: List<FollowEmployerListData>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("statuscode")
    val statuscode: String?
)

@Keep
data class Common(
    @SerializedName("TotalJobCount")
    val totalJobCount: String?,
    @SerializedName("total_records_found")
    val totalRecordsFound: String?,
    @SerializedName("totalpages")
    val totalpages: String?
)

@Keep
data class FollowEmployerListData(
    @SerializedName("CompanyID")
    val companyID: String?,
    @SerializedName("CompanyName")
    val companyName: String?,
    @SerializedName("FollowedOn")
    val followedOn: String?,
    @SerializedName("isSubscribed")
    val isSubscribed: String?,
    @SerializedName("JobCount")
    val jobCount: String?
)*/

@Keep
data class FollowEmployerListModelClass(
    @SerializedName("common")
    val common: Common?,
    @SerializedName("data")
    val `data`: List<FollowEmployerListData>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("statuscode")
    val statuscode: String?
)

@Keep
data class Common(
    @SerializedName("TotalJobCount")
    val totalJobCount: String?,
    @SerializedName("total_records_found")
    val totalRecordsFound: String?,
    @SerializedName("totalpages")
    val totalpages: String?
)

@Keep
data class FollowEmployerListData(
    @SerializedName("CompanyID")
    val companyID: String? = "",
    @SerializedName("CompanyName")
    val companyName: String?= "",
    @SerializedName("EmployerId")
    val employerId: String?= "",
    @SerializedName("FollowedOn")
    val followedOn: String?= "",
    @SerializedName("isSubscribed")
    var isSubscribed: String?= "",
    @SerializedName("JobCount")
    val jobCount: String?= ""
)


@Keep
data class EmployerSubscribeModel(
    @SerializedName("common")
    val common: Any?,
    @SerializedName("data")
    val `data`: List<EmployerSubscribeData>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("statuscode")
    val statuscode: String?
)

@Keep
data class EmployerSubscribeData(
    @SerializedName("isNewSMSPurchaseNeeded")
    val isNewSMSPurchaseNeeded: String?
)