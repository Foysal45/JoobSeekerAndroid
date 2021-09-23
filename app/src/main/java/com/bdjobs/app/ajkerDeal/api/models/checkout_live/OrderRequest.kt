package com.bdjobs.app.ajkerDeal.api.models.checkout_live


import com.google.gson.annotations.SerializedName

data class OrderRequest(
    @SerializedName("dealId")
    var dealId: Int,
    @SerializedName("couponPrice")
    var couponPrice: Double,
    @SerializedName("unitPrice")
    var unitPrice: Double,
    @SerializedName("ActualPrice")
    var actualPrice: Int,
    @SerializedName("couponQtn")
    var couponQtn: Int,
    @SerializedName("deliveryCharge")
    var deliveryCharge: Int,
    @SerializedName("merchantId")
    var merchantId: Int,

    @SerializedName("customerId")
    var customerId: Int,
    @SerializedName("deliveryDist")
    var deliveryDist: Int,
    @SerializedName("thanaId")
    var thanaId: Int,
    @SerializedName("areaId")
    var areaId: Int,
    @SerializedName("postalInformation")
    var postalInformation: String?,
    @SerializedName("customerBillingAddress")
    var customerBillingAddress: String?,
    @SerializedName("customerMobile")
    var customerMobile: String?,
    @SerializedName("customerAlternateMobile")
    var customerAlternateMobile: String?,

    @SerializedName("cardType")
    var cardType: String?,
    @SerializedName("paymentStatus")
    var paymentStatus: String? = "",
    @SerializedName("paymentType")
    var paymentType: String?,

    @SerializedName("shopCartId")
    var shopCartId: Int = 0,

    @SerializedName("appVersion")
    var appVersion: String?,

    @SerializedName("adDiscountPercentage")
    var adDiscountPercentage: Int = 0,
    @SerializedName("CollectionCharge")
    var collectionCharge: Int = 0,
    @SerializedName("DiscountInAmount")
    var discountInAmount: Int = 0,
    @SerializedName("EMIPeriod")
    var eMIPeriod: Int = 0,
    @SerializedName("EventType")
    var eventType: Int = 0,
    @SerializedName("ThirdPartyImageUrl")
    var thirdPartyImageUrl: String? = "",
    @SerializedName("VoucherCode")
    var voucherCode: String? = "",
    @SerializedName("VoucherCodeId")
    var voucherCodeId: Int = 0,
    @SerializedName("VoucherType")
    var voucherType: Int = 0,
    @SerializedName("adCashBackPercentage")
    var adCashBackPercentage: Int = 0,
    @SerializedName("BogoReferenceId")
    var bogoReferenceId: Int = 0,
    @SerializedName("sizes")
    var sizes: String? = "",
    @SerializedName("colors")
    var colors: String? = "",
    @SerializedName("commission")
    var commission: Int = 0,
    @SerializedName("dealTitle")
    var dealTitle: String? = "",
    @SerializedName("eventId")
    var eventId: Int = 0,
    @SerializedName("getFree")
    var getFree: Int = 0,
    @SerializedName("groupBuyId")
    var groupBuyId: Int = 0,
    @SerializedName("isHoursAvailable")
    var isHoursAvailable: Int = 0,
    @SerializedName("offeredCashBackPercentage")
    var offeredCashBackPercentage: Int = 0,
    @SerializedName("offeredDiscountPercentage")
    var offeredDiscountPercentage: Int = 0,
    @SerializedName("onlineTransactionId")
    var onlineTransactionId: String? = "",
    @SerializedName("orderType")
    var orderType: String? = "live",
    @SerializedName("productsSize")
    var productsSize: String? = "S",
    @SerializedName("SameDayCharge")
    var sameDayCharge: Int = 0,
    @SerializedName("specialNotes")
    var specialNotes: String? = "",
    @SerializedName("thirdPartyLocationId")
    var thirdPartyLocationId: Int = 0,
    @SerializedName("transactionId")
    var transactionId: String? = "",
    @SerializedName("unlockCommission")
    var unlockCommission: Int = 0,
    @SerializedName("voucherId")
    var voucherId: Int = 0,
    @SerializedName("VSReferenceId")
    var vSReferenceId: Int = 0,
    @SerializedName("orderFrom")
    var orderFrom: String = "android",
    @SerializedName("orderSource")
    var orderSource: String = "android"
)