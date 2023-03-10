package com.bdjobs.app.API.ModelClasses

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class LastUpdateModel(
        @SerializedName("common")
        val common: Any?,
        @SerializedName("data")
        val `data`: List<LastUpdateModelData?>?,
        @SerializedName("message")
        val message: String?,
        @SerializedName("statuscode")
        val statuscode: String?
)
@Keep
data class LastUpdateModelData(
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
        @SerializedName("videoInterviview")
        val videoInterviview: String?,
        @SerializedName("liveInterview")
        val liveInterview: String?,

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
        val userPicUrl: String?,

        @SerializedName("isResumeUpdate")
        val isResumeUpdate: String?,
        @SerializedName("changePassword_Eligibility")
        val changePassword_Eligibility: String?,
        @SerializedName("isCVPosted")
        val isCVPosted: String?,

        @SerializedName("postingDate")
        val postingDate: String?,

        @SerializedName("jobApplyLimit")
        val jobApplyLimit: String?,

        @SerializedName("applyRestrictionStatus")
        val applyRestrictionStatus: Boolean?,
        @SerializedName("appliedJobsThreshold")
        val appliedJobsThreshold: String?,

        @SerializedName("subscriptionType")
        val subscriptionType: String?,

        @SerializedName("isSmsFree")
        val isSmsFree : String?
)