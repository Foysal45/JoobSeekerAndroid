package com.bdjobs.app.Notification.Models

import com.google.gson.annotations.SerializedName

data class CommonNotificationModel(
        @SerializedName("pId")
        val pId : String = "",
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
        @SerializedName("imageLink")
        val imageLink: String? = "",
        @SerializedName("link")
        val link: String? = "",
        @SerializedName("deleteType")
        val deleteType: String? = ""

)