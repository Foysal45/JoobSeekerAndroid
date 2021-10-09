package com.bdjobs.app.ajkerDeal.api


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ErrorResponse(
    @SerializedName("Message")
    var message: String? = ""
)