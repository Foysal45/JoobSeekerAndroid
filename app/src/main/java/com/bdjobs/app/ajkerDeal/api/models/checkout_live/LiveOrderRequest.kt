package com.bdjobs.app.ajkerDeal.api.models.checkout_live


import com.google.gson.annotations.SerializedName

data class LiveOrderRequest(
    @SerializedName("LiveProductId")
    var liveProductId: Int = 0,
    @SerializedName("ProductPrice")
    var productPrice: Int = 0,
    @SerializedName("ProductQtn")
    var productQtn: Int = 0,
    @SerializedName("DeliveryCharge")
    var deliveryCharge: Int = 0,
    @SerializedName("channelId")
    var channelId: Int = 0,
    @SerializedName("CustomerId")
    var customerId: Int = 0,
    @SerializedName("DistrictId")
    var districtId: Int = 0,
    @SerializedName("ThanaId")
    var thanaId: Int = 0,
    @SerializedName("AreaId")
    var areaId: Int = 0,
    @SerializedName("PostalCode")
    var postalCode: String? = "",
    @SerializedName("Address")
    var address: String? = "",
    @SerializedName("Mobile")
    var mobile: String? = "",
    @SerializedName("AlternateMobile")
    var alternateMobile: String? = "",

    @SerializedName("cardType")
    var cardType: String?,
    @SerializedName("paymentStatus")
    var paymentStatus: String? = "",
    @SerializedName("PaymentType")
    var paymentType: String? = "",
    @SerializedName("ShopCartId")
    var shopCartId: Int = 0,
    @SerializedName("TotalPrice")
    var totalPrice: Int = 0,
    @SerializedName("LiveId")
    var liveId: Int = 0,
    @SerializedName("ChannelType")
    var channelType: String = "",
    @SerializedName("OrderPlaceFlag")
    var orderPlaceFlag: Int = 0,

    @SerializedName("appVersion")
    var appVersion: String?,
    @SerializedName("orderFrom")
    var orderFrom: String = "android",
    @SerializedName("orderSource")
    var orderSource: String = "bdjobs-android"

)