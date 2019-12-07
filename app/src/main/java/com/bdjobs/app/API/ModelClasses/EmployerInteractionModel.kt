package com.bdjobs.app.API.ModelClasses
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class InteractionModel(
    @SerializedName("common")
    var common: Any? = Any(),
    @SerializedName("data")
    var `data`: Any? = Any(),
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("statuscode")
    var statuscode: String? = ""
)