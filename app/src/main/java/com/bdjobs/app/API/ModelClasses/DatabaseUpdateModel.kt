package com.bdjobs.app.API.ModelClasses

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class DatabaseUpdateModel(
        @SerializedName("messageType") val messageType: String?,
        @SerializedName("update") val update: String?,
        @SerializedName("lastupdate") val lastupdate: String?,
        @SerializedName("dblink") val dblink: String?
)