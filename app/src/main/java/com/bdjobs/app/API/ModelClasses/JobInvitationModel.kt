package com.bdjobs.app.API.ModelClasses
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class JobInvitationListModel(
    @SerializedName("common")
    val common: Any?,
    @SerializedName("data")
    val `data`: List<JobInvitationListData?>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("statuscode")
    val statuscode: String?
)
@Keep
data class JobInvitationListData(
    @SerializedName("companyName")
    val companyName: String?,
    @SerializedName("inviteDate")
    val inviteDate: String?,
    @SerializedName("jobId")
    val jobId: String?,
    @SerializedName("jobTitle")
    val jobTitle: String?,
    @SerializedName("seen")
    val seen: String?
)