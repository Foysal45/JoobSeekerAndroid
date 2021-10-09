package com.bdjobs.app.ajkerDeal.api.models.checkout_live


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class LiveOrderResponse(
    @SerializedName("LiveOrderId")
    var liveOrderId: Int = 0,
    @SerializedName("ShopCartId")
    var shopCartId: Int = 0
)