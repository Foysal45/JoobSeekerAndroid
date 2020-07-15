package com.bdjobs.app.API.ModelClasses

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class SMSSubscribeModel(
        @SerializedName("common")
        var common: Any? = Any(),
        @SerializedName("data")
        var `data`: List<SMSSubscribeModelData?>? = listOf(),
        @SerializedName("message")
        var message: String? = "",
        @SerializedName("statuscode")
        var statuscode: String? = ""
)

@Keep
data class SMSSubscribeModelData(
        @SerializedName("isNewSMSPurchaseNeeded")
        var isNewSMSPurchaseNeeded : String? = ""
)