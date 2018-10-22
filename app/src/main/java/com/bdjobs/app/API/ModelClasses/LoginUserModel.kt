package com.bdjobs.app.API.ModelClasses

import com.google.gson.annotations.SerializedName

data class LoginUserModel(
        @SerializedName("statuscode") val statuscode: String?,
        @SerializedName("message") val message: String?,
        @SerializedName("data") val data: List<DataUserModel?>?,
        @SerializedName("common") val common: Any?
)