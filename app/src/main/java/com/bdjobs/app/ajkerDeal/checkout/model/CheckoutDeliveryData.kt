package com.bdjobs.app.ajkerDeal.checkout.model

import androidx.annotation.Keep

@Keep
data class CheckoutDeliveryData(
    var deliveryCharge: Int = 0,
    var deliveryType: String = "normal",
    var paymentType: String = "",
    var cardType: String = "",
    var paymentName: String = "",
    var paymentImageUrl: String = "",

    var minimumSingleOrderAmount: Int = 0,
    var minimumCartOrderAmount: Int = 0,
    var merchantSecondaryDeliveryCharge: Int = 0,
    var cartFreeDeliveryLimit: Int = 0,
    var singleFreeDeliveryLimit: Int = 0,
    var banglaMedsDeliveryCharge: Int = 0,
    var actualDeliveryCharge: Int = 0, // for regular & express delivery actualDeliveryCharge = deliveryCharge, for special case it contains actual value
    var otherMerchantDeliveryCharge: Int = 0,
    var sameDayDeliveryCharge: Int = 0
)