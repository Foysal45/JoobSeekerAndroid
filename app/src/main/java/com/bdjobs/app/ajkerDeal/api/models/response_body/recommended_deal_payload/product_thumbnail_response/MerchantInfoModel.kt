package com.bdjobs.app.ajkerDeal.api.models.response_body.recommended_deal_payload.product_thumbnail_response

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class MerchantInfoModel(

    @SerializedName("ProfileId")
    var profileId: Int = 0,

    @SerializedName("CompanyName")
    var companyName: String? = "",

    @SerializedName("MerchantRating")
    var merchantRating: Int = 0,

    @SerializedName("MerchantAwardName")
    var merchantAwardName: String? = "",

    @SerializedName("IsVerified")
    var isVerified: Int = 0,

    @SerializedName("DeliveryType")
    var deliveryType: Int = 0

) : Parcelable