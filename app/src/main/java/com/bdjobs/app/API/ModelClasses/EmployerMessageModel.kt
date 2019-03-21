package com.bdjobs.app.API.ModelClasses

import com.google.gson.annotations.SerializedName


data class MessageList(
        @SerializedName("common")
        var common: Common? = Common(),
        @SerializedName("data")
        var `data`: List<Data?>? = listOf(),
        @SerializedName("message")
        var message: String? = "",
        @SerializedName("statuscode")
        var statuscode: String? = ""
) {
    data class Common(
            @SerializedName("totalMessage")
            var totalMessage: Int? = 0,
            @SerializedName("totalpages")
            var totalpages: Int? = 0
    )

    data class Data(
            @SerializedName("companyName")
            var companyName: String? = "",
            @SerializedName("jobTite")
            var jobTite: String? = "",
            @SerializedName("mailedOn")
            var mailedOn: String? = "",
            @SerializedName("messageId")
            var messageId: String? = "",
            @SerializedName("status")
            var status: String? = ""
    )
}