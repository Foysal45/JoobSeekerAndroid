package com.bdjobs.app.API.ModelClasses
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
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

@Keep
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

@Keep
data class StatsModelClassData(
    @SerializedName("count")
    val count: String?,
    @SerializedName("title")
    val title: String?
)