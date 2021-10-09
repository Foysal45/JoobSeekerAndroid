package com.bdjobs.app.ajkerDeal.api.models.checkout_live

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class DeliveryInfo(
    @SerializedName("ProductSize")
    var productSize: String = "",
    @SerializedName("InsideDhakaAdvPaymentDeliveryCharge")
    var insideDhakaAdvPaymentDeliveryCharge: Int = 0,
    @SerializedName("InsideDhakaCodPaymentDeliveryCharge")
    var insideDhakaCodPaymentDeliveryCharge: Int = 0,
    @SerializedName("OutsideDhakaAdvPaymentDeliveryCharge")
    var outsideDhakaAdvPaymentDeliveryCharge: Int = 0,
    @SerializedName("OutsideDhakaCodPaymentDeliveryCharge")
    var outsideDhakaCodPaymentDeliveryCharge: Int = 0,

    @SerializedName("DeliveryPaymentType")
    var deliveryPaymentType: Int = 0,
    @SerializedName("ProductMaxPriceLimit")
    var productMaxPriceLimit: Int = 0,
    @SerializedName("ProductMaxPriceDeliveryCharge")
    var productMaxPriceDeliveryCharge: Int = 0,
    @SerializedName("BkashPaymentMaxPriceLimit")
    var bkashPaymentMaxPriceLimit: Int = 0,

    @SerializedName("SaradeshCODDeliveryCharge")
    var saradeshCODDeliveryCharge: Int = 0,
    @SerializedName("SaradeshAdvanceDeliveryCharge")
    var saradeshAdvanceDeliveryCharge: Int = 0,
    @SerializedName("SaradeshBkashDeliveryCharge")
    var saradeshBkashDeliveryCharge: Int = 0,
    @SerializedName("IsMerchantMobileNumberShow")
    var isMerchantMobileNumberShow: Int = 0,

    @SerializedName("DeliveryImageLink")
    var deliveryImageLink: String = ""
) : Parcelable