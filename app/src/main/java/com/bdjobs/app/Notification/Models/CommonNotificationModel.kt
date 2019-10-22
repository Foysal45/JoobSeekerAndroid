package com.bdjobs.app.Notification.Models

import com.google.gson.annotations.SerializedName

data class CommonNotificationModel(
        @SerializedName("title")
        val title: String? = "",
        @SerializedName("body")
        val body: String? = "",
        @SerializedName("type")
        val type: String? = "",
        @SerializedName("jobId")
        val jobId: String? = "",
        @SerializedName("jobTitle")
        val jobTitle: String? = "",
        @SerializedName("companyName")
        val companyName: String? = "",
        @SerializedName("image_link")
        val imageLink: String? = "",
        @SerializedName("link")
        val link: String? = ""
)