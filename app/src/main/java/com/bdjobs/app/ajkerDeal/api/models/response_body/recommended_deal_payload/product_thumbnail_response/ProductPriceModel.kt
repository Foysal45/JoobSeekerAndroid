package com.bdjobs.app.ajkerDeal.api.models.response_body.recommended_deal_payload.product_thumbnail_response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductPriceModel(

    @SerializedName("RegularPrice")
    var regularPrice: Int = 0,

    @SerializedName("AppDiscountPrice")
    var appDiscountPrice: Int = 0

) : Parcelable