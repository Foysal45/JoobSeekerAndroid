package com.bdjobs.app.ajkerDeal.api.models.order


import com.google.gson.annotations.SerializedName

data class DeliveryChargesModel(
    @SerializedName("RegularDeliveryCharge")
    var regularDeliveryCharge: Int = 0,
    @SerializedName("ExpressDeliveryCharge")
    var expressDeliveryCharge: Int = 0,
    @SerializedName("SpecialDeliveryCharge")
    var specialDeliveryCharge: Int = 0
)