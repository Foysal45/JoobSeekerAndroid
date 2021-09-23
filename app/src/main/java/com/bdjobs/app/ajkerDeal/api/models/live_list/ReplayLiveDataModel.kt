package com.bdjobs.app.ajkerDeal.api.models.live_list


import com.google.gson.annotations.SerializedName

data class ReplayLiveDataModel(
    @SerializedName("Id")
    var id: Int = 0,
    @SerializedName("LiveDate")
    var liveDate: String? = "",
    @SerializedName("FromTime")
    var fromTime: String? = "",
    @SerializedName("ToTime")
    var toTime: String? = "",
    @SerializedName("ScheduleId")
    var scheduleId: Int = 0,
    @SerializedName("LiveTitle")
    var liveTitle: String? = "",
    @SerializedName("CoverPhoto")
    var coverPhoto: String? = "",
    @SerializedName("LiveChannelName")
    var liveChannelName: String? = "",
    @SerializedName("LiveChannelUrl")
    var liveChannelUrl: LiveChannelUrl? = LiveChannelUrl(),
    @SerializedName("SuggestedPrice")
    var suggestedPrice: String? = "",
    @SerializedName("PaymentMode")
    var paymentMode: String? = "",
    @SerializedName("Facebook")
    var facebook: Boolean = false,
    @SerializedName("Youtube")
    var youtube: Boolean = false,
    @SerializedName("FacebookUrl")
    var facebookUrl: String? = "",
    @SerializedName("FacebookStream")
    var facebookStream: String? = "",
    @SerializedName("YoutubeUrl")
    var youtubeUrl: String? = "",
    @SerializedName("YoutubeStream")
    var youtubeStream: String? = "",
    @SerializedName("LiveStatus")
    var liveStatus: String? = "",
    @SerializedName("TotalCount")
    var totalCount: Int = 0,
    @SerializedName("TotalOrderCount")
    var totalOrderCount: Int = 0,
    @SerializedName("VideoChannelLink")
    var videoChannelLink: String? = "",
    @SerializedName("ChannelName")
    var channelName: String? = "",
    @SerializedName("ChannelLogo")
    var channelLogo: String? = "",
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
)