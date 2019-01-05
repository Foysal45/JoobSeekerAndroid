package com.bdjobs.app.API.ModelClasses
import com.google.gson.annotations.SerializedName


data class StatsModelClass(
    @SerializedName("common")
    val common: StatsModelClassCommon?,
    @SerializedName("data")
    val `data`: List<StatsModelClassData?>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("statuscode")
    val statuscode: String?
)

data class StatsModelClassCommon(
    @SerializedName("IsResumeUpdate")
    val isResumeUpdate: String?,
    @SerializedName("assessmentPendingExam")
    val assessmentPendingExam: String?,
    @SerializedName("decodeId")
    val decodeId: String?,
    @SerializedName("lastUpdateDate")
    val lastUpdateDate: String?,
    @SerializedName("storedJobs")
    val storedJobs: String?,
    @SerializedName("userId")
    val userId: String?
)

data class StatsModelClassData(
    @SerializedName("count")
    val count: String?,
    @SerializedName("title")
    val title: String?
)