package com.bdjobs.app.ajkerDeal.api.models.live_order_management


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class LiveOrderManagementResponseBody(
    @SerializedName("OrderId")
    var orderId: Int = 0,
    @SerializedName("InsertedOn")
    var insertedOn: String? = "",
    @SerializedName("LiveProductId")
    var liveProductId: Int = 0,
    @SerializedName("FolderName")
    var folderName: String? = "",
    @SerializedName("ProductTitle")
    var productTitle: String? = "",
    @SerializedName("DealQtn")
    var dealQtn: Int = 0,
    @SerializedName("ImageUrl")
    var imageUrl: String? = "",
    @SerializedName("ProductPrice")
    var productPrice: Int = 0,
    @SerializedName("OrderCount")
    var orderCount: Int = 1
)