package com.bdjobs.app.ajkerDeal.api.models.live_product


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class LiveProductRequest(
    @SerializedName("LiveId")
    var liveId: Int = 0,
    @SerializedName("Flag")
    val flag: Int = 1 // 1 means only Sold Out product load, 0 means All product load
)