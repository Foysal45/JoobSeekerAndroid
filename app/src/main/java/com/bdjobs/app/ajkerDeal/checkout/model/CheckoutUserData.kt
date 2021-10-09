package com.bdjobs.app.ajkerDeal.checkout.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CheckoutUserData (
    @SerializedName("uid")
    var uid: Long = 0,
    @SerializedName("districtId")
    var districtId: Int = 0,
    @SerializedName("thanaId")
    var thanaId: Int = 0,
    @SerializedName("areaId")
    var areaId: Int = 0,
    @SerializedName("billingAddress")
    var billingAddress: String = "",
    @SerializedName("mobile")
    var mobile: String = "",
    @SerializedName("alternateMobile")
    var alternateMobile: String = "",
    @SerializedName("districtName")
    var districtName: String = "",
    @SerializedName("thanaName")
    var thanaName: String = "",
    @SerializedName("areaName")
    var areaName: String = "",
    @SerializedName("postCode")
    var postCode: String = "",
    @SerializedName("ThirdPartyLocationId")
    var thirdPartyLocationId: Int = 0
)