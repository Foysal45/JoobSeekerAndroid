package com.bdjobs.app.ajkerDeal.api


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ResponseHeader<T> (
    @SerializedName("MessageCode")
    var messageCode: Int = 0,
    @SerializedName("MessageText")
    var messageText: String? = "",
    @SerializedName("DatabseTracking")
    var databaseTracking: Boolean = false,
    @SerializedName("TotalCount")
    var totalCount: Int = 0,
    @SerializedName("Data")
    var `data`: T?
)