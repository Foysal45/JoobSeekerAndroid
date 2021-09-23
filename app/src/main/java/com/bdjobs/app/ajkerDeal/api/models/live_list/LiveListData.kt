package com.bdjobs.app.ajkerDeal.api.models.live_list


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LiveListData(
    @SerializedName("Id")
    var id: Int = 0,
    @SerializedName("LiveDate")
    var liveDate: String? = "",
    @SerializedName("FromTime")
    var fromTime: String? = "",
    @SerializedName("ToTime")
    var toTime: String? = "",
    @SerializedName("ChannelId")
    var channelId: Int = 0,
    @SerializedName("ChannelType")
    var channelType: String? = "",
    @SerializedName("IsActive")
    var isActive: Int = 0,
    @SerializedName("InsertedBy")
    var insertedBy: Int = 0,
    @SerializedName("ScheduleId")
    var scheduleId: Int = 0,
    @SerializedName("CoverPhoto")
    var coverPhoto: String? = "",
    @SerializedName("VideoTitle")
    var videoTitle: String? = "",
    @SerializedName("CustomerName")
    var customerName: String? = "",
    @SerializedName("ProfileID")
    var profileID: String? = "",
    @SerializedName("CompStringName")
    var compStringName: String? = "",
    @SerializedName("ChannelLogo")
    var channelLogo: String? = "",
    @SerializedName("ChannelName")
    var channelName: String? = "",
    @SerializedName("LiveChannelName")
    var liveChannelName: String? = "",
    @SerializedName("VideoChannelLink")
    var videoChannelLink: String? = "",
    @SerializedName("CustomerId")
    var customerId: Int = 0,
    @SerializedName("MerchantId")
    var merchantId: Int = 0,
    @SerializedName("StatusName")
    var statusName: String? = "upcoming",
    @SerializedName("PaymentMode")
    var paymentMode: String? = "both",
    @SerializedName("FacebookPageUrl")
    var facebookPageUrl: String? = "",
    @SerializedName("Mobile")
    var mobile: String? = "",
    @SerializedName("AlternativeMobile")
    var alternativeMobile: String? = "",
    @SerializedName("RedirectToFB")
    var redirectToFB: Boolean = false,
    @SerializedName("IsShowMobile")
    var isShowMobile: Boolean = false,
    @SerializedName("IsShowComment")
    var isShowComment: Boolean = true,
    @SerializedName("IsShowProductCart")
    var isShowProductCart: Boolean = false,
    @SerializedName("FacebookVideoUrl")
    var facebookVideoUrl: String = "",
    @SerializedName("OrderPlaceFlag")
    var orderPlaceFlag: Int = 0, // Used in checkout for distinguish
    @SerializedName("CategoryId")
    var categoryId: Int = 0,
    @SerializedName("SubCategoryId")
    var subCategoryId: Int = 0,
    @SerializedName("SubSubCategoryId")
    var subSubCategoryId: Int = 0,
    @SerializedName("IsThirdPartyProductUrl")
    var isThirdPartyProductUrl: Int = 0,
    @SerializedName("IsNotificationSended")
    var isNotificationSended: Boolean = true,

    @SerializedName("VideoId")
    var videoId: String = "",
    @SerializedName("IsYoutubeVideo")
    var isYoutubeVideo: Int = 0
): Parcelable