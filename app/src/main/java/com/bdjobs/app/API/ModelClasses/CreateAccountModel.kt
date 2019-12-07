package com.bdjobs.app.API.ModelClasses

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CreateAccountModel(
        @SerializedName("common")
        val common: Any?,
        @SerializedName("data")
        val `data`: List<CreateAccountDataModel?>?,
        @SerializedName("message")
        val message: String?,
        @SerializedName("statuscode")
        val statuscode: String?
)