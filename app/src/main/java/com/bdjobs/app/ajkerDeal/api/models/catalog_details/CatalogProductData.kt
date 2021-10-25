package com.bdjobs.app.ajkerDeal.api.models.catalog_details


import androidx.annotation.Keep
import com.bdjobs.app.ajkerDeal.api.models.ImageInfo
import com.google.gson.annotations.SerializedName

@Keep
data class CatalogProductData(
    @SerializedName("DealId")
    var dealId: Int = 0,
    @SerializedName("DealTitle")
    var dealTitle: String? = "",
    @SerializedName("AccountsTitle")
    var accountsTitle: String? = "",
    @SerializedName("ImageLink")
    var imageLink: String? = "",
    @SerializedName("BrandId")
    var brandId: Int = 0,
    @SerializedName("CustomerId")
    var customerId: Int = 0,
    @SerializedName("RegularPrice")
    var regularPrice: Int = 0,
    @SerializedName("DealPrice")
    var dealPrice: Int = 0,
    @SerializedName("DealQtn")
    var dealQtn: Int = 0,
    @SerializedName("FolderName")
    var folderName: String? = "",
    @SerializedName("BulletDescription")
    var bulletDescription: String? = "",
    @SerializedName("DealKeywords")
    var dealKeywords: String? = "",
    @SerializedName("UserId")
    var userId: Int = 0,
    @SerializedName("Sizes")
    var sizes: String? = "",
    @SerializedName("Colors")
    var colors: String? = "",
    @SerializedName("ProductCode")
    var productCode: String? = "",
    @SerializedName("BulletDescriptionEng")
    var bulletDescriptionEng: String? = "",
    @SerializedName("ConfirmByType")
    var confirmByType: Int? = 0,
    @SerializedName("CommissionPercent")
    var commissionPercent: Int? = 0,
    @SerializedName("CommissionPerCoupon")
    var commissionPerCoupon: Int = 0,
    @SerializedName("ImageCount")
    var imageCount: Int? = 0,
    @SerializedName("CategoryId")
    var categoryId: Int? = 0,
    @SerializedName("SubCategoryId")
    var subCategoryId: Int? = 0,
    @SerializedName("SubSubCategoryId")
    var subSubCategoryId: Int? = 0,
    @SerializedName("RoutingName")
    var routingName: String? = "",
    @SerializedName("IsSoldOut")
    var isSoldOut: Int? = 0,
    @SerializedName("IsActive")
    var isActive: Int? = 0,
    @SerializedName("InsertedOn")
    var insertedOn: String? = "",
    @SerializedName("CouponPrice")
    var couponPrice: Int = 0,
    @SerializedName("DealDiscount")
    var dealDiscount: Int = 0,
    @SerializedName("DeliveryCharge")
    var deliveryCharge: Int? = 0,
    @SerializedName("ProductSize")
    var productSize: String? = "",
    @SerializedName("DeliveryChargeAmount")
    var deliveryChargeAmount: Int = 0,
    @SerializedName("DeliveryChargeAmountOutSideDhaka")
    var deliveryChargeAmountOutSideDhaka: Int = 0,
    @SerializedName("BusinessModelType")
    var businessModelType: Int = 0,
    @SerializedName("QtnAfterBooking")
    var qtnAfterBooking: Int = 0,

    @SerializedName("DealOptions")
    var dealOptions: String = "0",
    @SerializedName("IsDealPerishable")
    var isDealPerishable: Int = 0,
    @SerializedName("IsHoursAvailable")
    var isHoursAvailable: Int = 0,
    @SerializedName("ProfileId")
    var profileId: Int = 0,
    @SerializedName("AppDealCommission")
    var appDealCommission: Int = 0,
    @SerializedName("ReferenceId")
    var referenceId: Int = 0,
    @SerializedName("DistrictExists")
    var districtExists: Int = 0,
    @SerializedName("CollectionChargePercentage")
    var collectionChargePercentage: Int = 0,
    @SerializedName("Unlockcommission")
    var unlockcommission: Int = 0,
    @SerializedName("MaxPriceForDelZero")
    var maxPriceForDelZero: Int = 0,
    @SerializedName("MerchantDeliveryType")
    var merchantDeliveryType: Int = 0,
    @SerializedName("PromotionalBanner")
    var promotionalBanner: String? = "",
    @SerializedName("ImageInfo")
    var imageInfo: ImageInfo = ImageInfo(),

    var quantity: Int = 1,
    var selectedSize: String = ""
)