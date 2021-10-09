package com.bdjobs.app.ajkerDeal.api.models.response_body.recommended_deal_payload.product_thumbnail_response

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class ProductImageInfoModel(

    @SerializedName("ImageCount")
    var imageCount: Int = 0,

    @SerializedName("LargeImagesLink")
    var largeImagesLink: List<String>? = listOf(),

    @SerializedName("SmallImagesLink")
    var smallImagesLink: List<String>? = listOf()

) : Parcelable