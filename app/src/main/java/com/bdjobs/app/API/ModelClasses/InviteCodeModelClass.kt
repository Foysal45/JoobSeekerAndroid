package com.bdjobs.app.API.ModelClasses
import com.google.gson.annotations.SerializedName


data class InviteCodeHomeModel(
    @SerializedName("common")
    val common: Any = Any(),
    @SerializedName("data")
    val `data`: List<InviteCodeHomeModelData> = listOf(),
    @SerializedName("message")
    val message: String = "",
    @SerializedName("statuscode")
    val statuscode: String = ""
)

data class InviteCodeHomeModelData(
    @SerializedName("inviteCodeStatus")
    val inviteCodeStatus: String = "",
    @SerializedName("pcOwnerID")
    val pcOwnerID: String = "",
    @SerializedName("userType")
    val userType: String = ""
)