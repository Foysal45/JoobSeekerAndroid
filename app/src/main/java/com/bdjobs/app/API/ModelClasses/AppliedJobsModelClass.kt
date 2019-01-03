package com.bdjobs.app.API.ModelClasses
import com.google.gson.annotations.SerializedName


data class AppliedJobsModel(
    @SerializedName("activity")
    val activity: List<AppliedJobsActivity?>?,
    @SerializedName("common")
    val common: Any?,
    @SerializedName("data")
    val `data`: List<AppliedJobsData?>?,
    @SerializedName("exprience")
    val exprience: List<AppliedJobsExprience?>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("statuscode")
    val statuscode: String?
)

data class AppliedJobsActivity(
    @SerializedName("totalContacted")
    val totalContacted: String?,
    @SerializedName("totalHired")
    val totalHired: String?,
    @SerializedName("totalNotContacted")
    val totalNotContacted: String?
)

data class AppliedJobsData(
    @SerializedName("appliedOn")
    val appliedOn: String?,
    @SerializedName("companyName")
    val companyName: String?,
    @SerializedName("deadLine")
    val deadLine: String?,
    @SerializedName("expectedSalary")
    val expectedSalary: String?,
    @SerializedName("invitaion")
    val invitaion: String?,
    @SerializedName("jobId")
    val jobId: String?,
    @SerializedName("langType")
    val langType: String?,
    @SerializedName("seen")
    val seen: String?,
    @SerializedName("sl")
    val sl: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("totalNum")
    val totalNum: String?,
    @SerializedName("viewedByEmployer")
    val viewedByEmployer: String?
)

data class AppliedJobsExprience(
    @SerializedName("experienceID")
    val experienceID: String?,
    @SerializedName("experienceInfo")
    val experienceInfo: String?,
    @SerializedName("jobid")
    val jobid: String?
)