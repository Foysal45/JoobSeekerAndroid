package com.bdjobs.app.ajkerDeal.api.models.order


import com.google.gson.annotations.SerializedName

data class CheckoutLimits(
    @SerializedName("minimunSingleOrderAmount")
    var minimunSingleOrderAmount: Int = 0,
    @SerializedName("minimumCartOrderAmount")
    var minimumCartOrderAmount: Int = 0,
    @SerializedName("merchantSecondaryDeliveryCharge")
    var merchantSecondaryDeliveryCharge: Int = 0,
    @SerializedName("CartFreeDeliveryLimit")
    var cartFreeDeliveryLimit: Int = 0,
    @SerializedName("SingleFreeDeliveryLimit")
    var singleFreeDeliveryLimit: Int = 0,
    @SerializedName("banglaMedsDeliveryCharge")
    var banglaMedsDeliveryCharge: Int = 0,
    @SerializedName("customMessageUrl")
    var customMessageUrl: String? = "",
    @SerializedName("otherMerchantDeliveryCharge")
    var otherMerchantDeliveryCharge: Int = 0,
    @SerializedName("sameDayDeliveryCharge")
    var sameDayDeliveryCharge: Int = 20,
    @SerializedName("BOGOOrderPaymentType")
    var BOGOOrderPaymentType: String = ""
)