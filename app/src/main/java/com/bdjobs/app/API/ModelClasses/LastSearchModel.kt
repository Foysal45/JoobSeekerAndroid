package com.bdjobs.app.API.ModelClasses
import com.google.gson.annotations.SerializedName


data class LastSearchCountModel(
    @SerializedName("common")
    val common: Any?,
    @SerializedName("data")
    val `data`: List<LastSearchCountData?>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("statuscode")
    val statuscode: String?
)

data class LastSearchCountData(
    @SerializedName("error")
    val error: String?,
    @SerializedName("totaljobs")
    val totaljobs: String?
)