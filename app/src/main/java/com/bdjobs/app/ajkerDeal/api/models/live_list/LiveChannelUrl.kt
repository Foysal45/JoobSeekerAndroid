package com.bdjobs.app.ajkerDeal.api.models.live_list


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class LiveChannelUrl(
    @SerializedName("Id")
    var id: Int = 0,
    @SerializedName("Input")
    var input: String? = ""
)