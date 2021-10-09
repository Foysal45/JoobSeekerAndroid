package com.bdjobs.app.ajkerDeal.api.models.response_body.recommended_deal_payload.product_thumbnail_response

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class ProductCategoryDetailsModel(

    @SerializedName("CategoryId")
    val categoryId: Int = 0,

    @SerializedName("SubCategoryId")
    val subCategoryId: Int = 0,

    @SerializedName("SubSubCategoryId")
    val subSubCategoryId: Int = 0

) : Parcelable