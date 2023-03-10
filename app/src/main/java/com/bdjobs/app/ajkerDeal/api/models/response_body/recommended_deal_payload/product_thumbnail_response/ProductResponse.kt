package com.bdjobs.app.ajkerDeal.api.models.response_body.recommended_deal_payload.product_thumbnail_response


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ProductResponse(
    @SerializedName("TotalCount")
    var totalCount: Int = 0,
    @SerializedName("AppProductResponseModel")
    var appProductResponseModel: List<ProductResponseModel> = listOf()
)