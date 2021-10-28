package com.bdjobs.app.ajkerDeal.ui.chat.model


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ChatData(
    @SerializedName("key")
    var key: String? = "",
    @SerializedName("id")
    var id: String? = "",
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("userImgUrl")
    var userImgUrl: String? = "",
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("date")
    var date: String? = "",
    @SerializedName("timeStamp")
    var timeStamp: String? = "",
    @SerializedName("isHost")
    var isHost: Boolean = false
)