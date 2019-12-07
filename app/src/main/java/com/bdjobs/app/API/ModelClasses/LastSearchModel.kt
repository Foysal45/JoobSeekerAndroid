package com.bdjobs.app.API.ModelClasses
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
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
@Keep
data class LastSearchCountData(
    @SerializedName("error")
    val error: String?,
    @SerializedName("totaljobs")
    val totaljobs: String?
)