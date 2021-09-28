package com.bdjobs.app.ajkerDeal.api.models.response_body.recommended_deal_payload.product_thumbnail_response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductBasicInfo(

    @SerializedName("DealTitle")
    var dealTitle: String? = "",

    @SerializedName("BulletDescription")
    var bulletDescription: String? = "",

    @SerializedName("AccountsTitle")
    var accountsTitle: String? = "",

    @SerializedName("Sizes")
    var sizes: String? = "",

    @SerializedName("Colors")
    var colors: String? = null,

    @SerializedName("DealQtn")
    var dealQtn: Int = 0,

    @SerializedName("QtnAfterBooking")
    var qtnAfterBooking: Int = 0,

    @SerializedName("FolderName")
    var folderName: String? = "",

    @SerializedName("ProductRating")
    var productRating: String? = "",

    @SerializedName("IsHoursAvailable")
    var isHoursAvailable: Int = 0,

    @SerializedName("IsGroupByAvailable")
    var isGroupByAvailable: Int = 0,

    @SerializedName("Commission")
    var commission: Int = 0,

    @SerializedName("IsDealPerishable")
    var isDealPerishable: Boolean = false,

    @SerializedName("ThirdPartyDealId")
    var thirdPartyDealId: Int = 0,

    @SerializedName("ProductVariantId")
    var productVariantId: Int = 0,

    @SerializedName("ProductType")
    var productType: Int = 0,

    @SerializedName("BrandId")
    var brandId: Int = 0,

    @SerializedName("BrandRoutingName")
    var brandRoutingName: String? = null

) : Parcelable