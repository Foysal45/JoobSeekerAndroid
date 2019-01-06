package com.bdjobs.app.API.ModelClasses
import com.google.gson.annotations.SerializedName


data class InterviewInvitation(
    @SerializedName("common")
    val common: Any?,
    @SerializedName("data")
    val `data`: List<InterviewInvitationData?>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("statuscode")
    val statuscode: String?
)

data class InterviewInvitationData(
    @SerializedName("SocialMediaId")
    val socialMediaId: String?,
    @SerializedName("SocialMediaType")
    val socialMediaType: String?,
    @SerializedName("catId")
    val catId: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("hasAssmnt")
    val hasAssmnt: String?,
    @SerializedName("inviteInterviview")
    val inviteInterviview: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("pendingTest")
    val pendingTest: String?,
    @SerializedName("smTableId")
    val smTableId: String?,
    @SerializedName("trainingId")
    val trainingId: String?,
    @SerializedName("userName")
    val userName: String?,
    @SerializedName("userPicUrl")
    val userPicUrl: String?
)