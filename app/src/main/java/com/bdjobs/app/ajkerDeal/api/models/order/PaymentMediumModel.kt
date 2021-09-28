package com.bdjobs.app.ajkerDeal.api.models.order

import com.google.gson.annotations.SerializedName

data class PaymentMediumModel(
    @SerializedName("Id")
    var id: Int = 0,
    @SerializedName("Name")
    var name: String = "",
    @SerializedName("Icon")
    var icon: String = "",
    @SerializedName("IsActive")
    var isActive: Int = 0,
    @SerializedName("DeliveryType")
    var deliveryType: String = "",
    @SerializedName("IsMajor")
    var isMajor: Int = 0,
    @SerializedName("deliveryCharges")
    var deliveryCharges: DeliveryChargesModel = DeliveryChargesModel(),

    //Internal
    var isMore: Boolean = false
)