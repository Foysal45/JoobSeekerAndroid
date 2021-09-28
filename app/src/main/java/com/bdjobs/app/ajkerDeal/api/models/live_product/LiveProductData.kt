package com.bdjobs.app.ajkerDeal.api.models.live_product


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LiveProductData(
    @SerializedName("Id")
    var id: Int = 0,
    @SerializedName("LiveId")
    var liveId: Int = 0,
    @SerializedName("IsActive")
    var isActive: Int = 0,
    @SerializedName("ProductTitle")
    var productTitle: String? = "",
    @SerializedName("ProductPrice")
    var productPrice: Int = 0,
    @SerializedName("DiscountPercentage")
    var discountPercentage: Int = 0,
    @SerializedName("DiscountAfterPrice")
    var discountAfterPrice: Int = 0,
    @SerializedName("InsertedBy")
    var insertedBy: Int = 0,
    @SerializedName("CoverPhoto")
    var coverPhoto: String? = "",
    @SerializedName("IsSoldOut")
    var isSoldOut: Boolean = false,
    @SerializedName("FolderName")
    var folderName: String? = "",
    @SerializedName("MerchantId")
    var merchantId: Int = 0,

    // Custom field used in card. Do not remove it.
    var cartDealQuantity: Int = 1,
    var qtnLimitPerUser: Int = 5,

): Parcelable