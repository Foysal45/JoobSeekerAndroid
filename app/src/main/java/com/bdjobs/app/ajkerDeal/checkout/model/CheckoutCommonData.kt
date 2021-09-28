package com.bdjobs.app.ajkerDeal.checkout.model

data class CheckoutCommonData(
    var orderType: com.bdjobs.app.ajkerDeal.api.models.checkout.OrderType = com.bdjobs.app.ajkerDeal.api.models.checkout.OrderType.SINGLE,

    var totalCouponPriceUpdated: Double = 0.0,
    var totalDeliveryChargeUpdated: Int = 0,
    var totalCouponDiscountUpdated: Int = 0,
    var totalProductCountUpdated: Int = 0,
    var grandTotalUpdated: Double = 0.0,
    var deliveryCharge: Int = 0,
    var secondaryDeliveryCharge: Int = 0,
    var otherMerchantDeliveryCharge: Int = 0,
    var totalCollectionChargeUpdated: Int = 0,
    var sameDayDeliveryCharge: Int = 0,

    var paymentType: String = "",
    var cardType: String = "",
    var orderDeliveryType: String = "",

    var selectedAddress: String = "",
    var selectedPaymentName: String = "",
    var selectedPaymentImage: String = "",
    var isChaldalAvailable: Boolean = false,
    var districtId: Int = 0,
    var dealIdList: ArrayList<Int> = arrayListOf(),
    var isVoucherInvalid: Boolean = false,
    var minimumSingleOrderAmount: Int = 0,
    var minimumCartOrderAmount: Int = 0,
    var merchantSecondaryDeliveryCharge: Int = 0,
    var cartFreeDeliveryLimit: Int = 0,
    var singleFreeDeliveryLimit: Int = 0,
    var banglaMedsDeliveryCharge: Int = 0,
    var imageUrl: String = "",
    var isSameDayDelivery: Boolean = false
)