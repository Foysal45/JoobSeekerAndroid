package com.bdjobs.app.ajkerDeal.api.models.live_list


import com.google.gson.annotations.SerializedName

data class LiveListRequest(
    @SerializedName("Index")
    var index: Int = 0,
    @SerializedName("Count")
    var count: Int = 20,
    @SerializedName("LiveId")
    var liveId: Int = 0,
    @SerializedName("LiveTitle")
    var liveTitle: String? = "",
    @SerializedName("ChannelName")
    var channelName: String? = "",
)