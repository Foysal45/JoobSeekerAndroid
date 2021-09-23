package com.bdjobs.app.ajkerDeal.api.models.config

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class EventAction(
    @SerializedName("CategoryId")
    var categoryId: Int = 0,
    @SerializedName("SubCategoryId")
    var subCategoryId: Int = 0,
    @SerializedName("SubSubCategoryId")
    var subSubCategoryId: Int = 0,
    @SerializedName("ActionUrl")
    var actionUrl: String? = "",
    @SerializedName("CategoryRoutingName")
    var categoryRoutingName: String? = "",
    @SerializedName("BrandId")
    var brandId: Int = 0
) : Serializable