package com.bdjobs.app.ajkerDeal.api.models.checkout_live


import com.google.gson.annotations.SerializedName

data class OrderResponse(
    @SerializedName("CouponId")
    var couponId: Int,
    @SerializedName("ShopCartId")
    var shopCartId: Int,
    @SerializedName("CurrentTime")
    var currentTime: String?,
    @SerializedName("CheckCustomerOrder")
    var checkCustomerOrder: Int,
    @SerializedName("DailyOrderCount")
    var dailyOrderCount: Int,
    @SerializedName("CheckShopCartOrder")
    var checkShopCartOrder: Int,
    @SerializedName("TotalPoint")
    var totalPoint: Int,
    @SerializedName("OrderMessage")
    var orderMessage: String?,
    @SerializedName("DealTitle")
    var dealTitle: String?,
    @SerializedName("FolderName")
    var folderName: String?,
    @SerializedName("CustomerMail")
    var customerMail: String?,
    @SerializedName("CustomerName")
    var customerName: String?,
    @SerializedName("DeviceId")
    var deviceId: String?,
    @SerializedName("Sizes")
    var sizes: String?,
    @SerializedName("CouponPrice")
    var couponPrice: Int,
    @SerializedName("DeliveryCharge")
    var deliveryCharge: Int,
    @SerializedName("CouponQtn")
    var couponQtn: Int,
    @SerializedName("CustomerId")
    var customerId: Int,
    @SerializedName("CustomerBillingAddress")
    var customerBillingAddress: String?,
    @SerializedName("CustomerRanking")
    var customerRanking: Int,
    @SerializedName("AreaName")
    var areaName: String?
)