package com.bdjobs.app.API.ModelClasses

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class EmployerMessageModel(
        @SerializedName("common")
        var common: CommonData? = CommonData(),
        @SerializedName("data")
        var `data`: List<MessageDataModel?>? = listOf(),
        @SerializedName("message")
        var message: String? = "",
        @SerializedName("statuscode")
        var statuscode: String? = ""
)
@Keep
data class CommonData(
        @SerializedName("totalMessage")
        var totalMessage: Int? = 0,
        @SerializedName("totalpages")
        var totalpages: Int? = 0
)
@Keep
data class MessageDataModel(
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

@Keep
data class MessageDetailModel(
        @SerializedName("common")
        var common: Any? = Any(),
        @SerializedName("data")
        var `data`: List<MessageDetailData?>? = listOf(),
        @SerializedName("message")
        var message: String? = "",
        @SerializedName("statuscode")
        var statuscode: String? = ""
)
@Keep
data class MessageDetailData(
        @SerializedName("from")
        var from: String? = "",
        @SerializedName("jobtitle")
        var jobtitle: String? = "",
        @SerializedName("lastcontent")
        var lastcontent: String? = "",
        @SerializedName("mailedon")
        var mailedon: String? = "",
        @SerializedName("msgBody")
        var msgBody: String? = "",
        @SerializedName("subject")
        var subject: String? = ""
)
