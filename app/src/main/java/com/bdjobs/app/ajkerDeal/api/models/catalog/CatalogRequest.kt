package com.bdjobs.app.ajkerDeal.api.models.catalog


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CatalogRequest(
    @SerializedName("CustomerId")
    var customerId: Int = 0,
    @SerializedName("Index")
    var index: Int = 0,
    @SerializedName("Count")
    var count: Int = 20,
    @SerializedName("VideoTitle")
    var videoTitle: String = "",
    @SerializedName("CategoryId")
    var categoryId: Int = 0,
    @SerializedName("SubCategoryId")
    var subCategoryId: Int = 0,
    @SerializedName("SubSubCategoryId")
    var subSubCategoryId: Int = 0,
    @SerializedName("IsHotCatalog")
    var isHotCatalog: Int = 0
)