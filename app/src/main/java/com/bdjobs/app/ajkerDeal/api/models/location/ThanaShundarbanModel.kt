package com.bdjobs.app.ajkerDeal.api.models.location


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ThanaShundarbanModel(
    @SerializedName("ThanaId")
    var thanaId: Int = 0,
    @SerializedName("Thana")
    var thana: String = "",
    @SerializedName("DeliveryCharge")
    var deliveryCharge: Int = 0,
    @SerializedName("ThanaBng")
    var thanaBng: String = "",
    @SerializedName("IsAdvPaymentActive")
    var isAdvPaymentActive: Int = 0,
    @SerializedName("AppDeliveryCharge")
    var appDeliveryCharge: Int = 0,
    @SerializedName("AppMultipleOrderDeliveryCharge")
    var appMultipleOrderDeliveryCharge: Int = 0,
    @SerializedName("AppAdvPaymentDeliveryCharge")
    var appAdvPaymentDeliveryCharge: Int = 0,
    @SerializedName("ParentId")
    var parentId: Int = 0,
    @SerializedName("IsCity")
    var isCity: Boolean = false,
    @SerializedName("IsCourier")
    var isCourier: Int = 0,
    @SerializedName("ThanaPriority")
    var thanaPriority: Int = 0,
    @SerializedName("Area")
    var area: List<String> = listOf(),
    @SerializedName("HasExpressDelivery")
    var hasExpressDelivery: Int = 0
)