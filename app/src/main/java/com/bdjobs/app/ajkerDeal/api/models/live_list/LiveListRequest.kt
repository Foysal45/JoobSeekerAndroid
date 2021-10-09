package com.bdjobs.app.ajkerDeal.api.models.live_list


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
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
    @SerializedName("statusName")
    var statusName: String? = "",
    @SerializedName("deviceName")
    var deviceName: String? = "",
    @SerializedName("categoryId")
    var categoryId: Int? = 0,
    @SerializedName("subCategoryId")
    var subCategoryId: Int? = 0,
    @SerializedName("subSubCategoryId")
    var subSubCategoryId: Int? = 0,
    @SerializedName("gender")
    var gender: String? = "",
    @SerializedName("age")
    var age: Int? = 0,
    @SerializedName("currentSalary")
    var currentSalary: String? = "",
    @SerializedName("expectedSalary")
    var expectedSalary: String? = "",
    @SerializedName("location")
    var location: String? = ""
)