package com.bdjobs.app.ajkerDeal.api.models.response_body.recommended_deal_payload.product_thumbnail_response

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
open class ProductResponseModel : Parcelable {

    @SerializedName("DealId")
    var dealId = 0

    @SerializedName("IsSoldOut")
    var isSoldOut: String? = null

    @SerializedName("EventId")
    var eventId = 0

    @SerializedName("EventType")
    var EventType = 0

    @SerializedName("EventImage")
    var eventImage: String? = ""

    @SerializedName("VSCatalogId")
    var vsCatalogId = 0

    @SerializedName("ProductCategoryDetails")
    var productCategoryDetails: ProductCategoryDetailsModel? = null

    @SerializedName("BasicInfo")
    var basicInfo: ProductBasicInfo? = null

    @SerializedName("ProductPrice")
    var productPrice: ProductPriceModel? = null

    @SerializedName("ProductPriceDiscount")
    var productPriceDiscount: ProductPriceDiscountModel? = null

    @SerializedName("MerchantInfo")
    var merchantInfo: MerchantInfoModel? = null

    @SerializedName("ImageInfo")
    var imageInfo: ProductImageInfoModel? = null

    /*@SerializedName("OfferBannerInfo")
    var offerBannerInfo: OfferBannerModel? = null*/

    @SerializedName("VideoInfo")
    var videoInfo: VideoInfo? = null

    // Custom field used in deal details. Do not remove it.
    var dealEventId = 0
    var dealEventType = 0

    override fun toString(): String {
        return "ProductResponseModel(dealId=$dealId, isSoldOut=$isSoldOut, EventType=$EventType, vsCatalogId=$vsCatalogId,  productCategoryDetails=$productCategoryDetails, basicInfo=$basicInfo, productPrice=$productPrice, productPriceDiscount=$productPriceDiscount, merchantInfo=$merchantInfo, imageInfo=$imageInfo, videoInfo=$videoInfo, dealEventId=$dealEventId, dealEventType=$dealEventType)"
    }

}