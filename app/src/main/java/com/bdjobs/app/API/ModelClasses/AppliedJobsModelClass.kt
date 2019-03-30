package com.bdjobs.app.API.ModelClasses

import com.google.gson.annotations.SerializedName


data class AppliedJobModel(
        @SerializedName("activity")
        val activity: List<AppliedJobModelActivity?>?,
        @SerializedName("common")
        val common: AppliedJobModelCommon?,
        @SerializedName("data")
        val `data`: List<AppliedJobModelData?>?,
        @SerializedName("exprience")
        val exprience: List<AppliedJobModelExprience?>?,
        @SerializedName("message")
        val message: String?,
        @SerializedName("statuscode")
        val statuscode: String?
)

data class AppliedJobModelCommon(
        @SerializedName("totalNumberOfApplication")
        val totalNumberOfApplication: String? = "",
        @SerializedName("totalNumberOfPage")
        val totalNumberOfPage: String?
)

data class AppliedJobModelActivity(
        @SerializedName("totalContacted")
        val totalContacted: String?,
        @SerializedName("totalHired")
        val totalHired: String?,
        @SerializedName("totalNotContacted")
        val totalNotContacted: String?
)

data class AppliedJobModelData(
        @SerializedName("appliedOn")
        val appliedOn: String?= "",
        @SerializedName("companyName")
        val companyName: String?= "",
        @SerializedName("deadLine")
        val deadLine: String?= "",
        @SerializedName("expectedSalary")
        var expectedSalary: String?= "",
        @SerializedName("invitaion")
        val invitaion: String?= "",
        @SerializedName("isUserSeenInvitation")
        val isUserSeenInvitation: String?= "",
        @SerializedName("jobId")
        val jobId: String?= "",
        @SerializedName("langType")
        val langType: String?= "",
        @SerializedName("sl")
        val sl: String?= "",
        @SerializedName("status")
        val status: String?= "",
        @SerializedName("title")
        val title: String?= "",
        @SerializedName("viewedByEmployer")
        val viewedByEmployer: String?= ""
)

data class AppliedJobModelExprience(

        @SerializedName("jobid")
        val jobid: String?,
        @SerializedName("companyName")
        val companyName: String?,
        @SerializedName("designation")
        val designation: String?,
        @SerializedName("experienceID")
        val experienceID: String?

)