package com.bdjobs.app.API.ModelClasses

import com.google.gson.annotations.SerializedName
data class DataUserModel(
    @SerializedName("userFullName") val userFullName: String?,
    @SerializedName("userId") val userId: String?,
    @SerializedName("imageurl") val imageurl: String?,
    @SerializedName("isBlueCollar") val isBlueCollar: String?
)