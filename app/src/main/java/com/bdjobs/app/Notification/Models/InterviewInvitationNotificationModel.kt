package com.bdjobs.app.Notification.Models

import com.google.gson.annotations.SerializedName

data class InterviewInvitationNotificationModel(
        @SerializedName("companyName")
        val companyName: String? = "",
        @SerializedName("body")
        val body: String? = "",
        @SerializedName("type")
        val type: String? = "",
        @SerializedName("jobid")
        val jobid: String? = "",
        @SerializedName("title")
        val title: String? = "",
        @SerializedName("jobtitle")
        val jobTitle: String? = ""
)