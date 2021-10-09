package com.bdjobs.app.ajkerDeal.api.models.order

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * DeliveryInfoModel
 */
@Keep
data class DeliveryInfoModel(
    @SerializedName("deliveryTypes")
    var deliveryTypes: List<DeliveryTypeModel> = listOf(),
    @SerializedName("paymentMediums")
    var paymentMediums: MutableList<PaymentMediumModel> = mutableListOf(),
    @SerializedName("limits")
    var checkoutLimits: CheckoutLimits? = CheckoutLimits(),
    @SerializedName("ShowPoweredBy")
    var showPoweredBy: Boolean = false
)