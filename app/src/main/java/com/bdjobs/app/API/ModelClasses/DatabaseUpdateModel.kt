package com.bdjobs.app.API.ModelClasses
import com.google.gson.annotations.SerializedName



data class DatabaseUpdateModel(
        @SerializedName("messageType") val messageType: String?,
        @SerializedName("update") val update: String?,
        @SerializedName("lastupdate") val lastupdate: String?,
        @SerializedName("dblink") val dblink: String?
)