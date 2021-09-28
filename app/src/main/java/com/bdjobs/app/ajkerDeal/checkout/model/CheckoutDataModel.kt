package com.bdjobs.app.ajkerDeal.checkout.model

import android.os.Parcelable
import com.bdjobs.app.ajkerDeal.api.models.checkout_live.DeliveryInfo
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CheckoutDataModel(

    var orderType: com.bdjobs.app.ajkerDeal.api.models.checkout.OrderType = com.bdjobs.app.ajkerDeal.api.models.checkout.OrderType.SINGLE,
    var shopCartId: Int = 0,
    var eventId: Int = 0,
    var groupId: Int = 0,
    var isHoursAvailable: Int = 0,
    var couponPrice: Int = 0,
    var unitPrice: Int = 0,
    var dealId: Int = 0,
    var merchantId: Int = 0,
    var sizes: String = "",
    var colors: String = "",
    var commission: Int = 0,
    var voucherId: Int = 0,
    var couponQtn: Int = 0,
    var productImgUrl: String = "",
    var quantityRestrict: Int = 0,
    var IsDealPerishable: Boolean = false,
    //only for group buy
    var advancedAlertPaymentType: String = "",
    //only for group buy
    var cardType: String = "",
    var deliveryChargeModel: DeliveryInfo? = null,
    var merchantDeliveryType: com.bdjobs.app.ajkerDeal.api.models.checkout.DeliveryType = com.bdjobs.app.ajkerDeal.api.models.checkout.DeliveryType.BOTH,
    var unlockCommission: Int = 0,
    var eventType: Int = 0,
    var deliveryChargeForGb: Int = 0,
    var dealTitle: String = "",
    var maxPriceForDelZero: Int = -1,
    //var allBogoProductsModels: List<AllBogoProductsModel>? = null,
    var isDistrictWiseProduct: Int = 0,
    var paymentType: String = "0",
    var collectionChargePercentage: Int = 0,
    var quantityAfterBooking: Int = -1,
    //var cartProductList: List<CartDataModel>? = null,
    var deliveryPaymentType: Int = 0, // 1 Adv 2 COD 0 default
    var referenceId: Int = 0 // used for video shopping
) : Parcelable