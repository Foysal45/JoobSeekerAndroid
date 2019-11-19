package com.bdjobs.app.API.ModelClasses
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class TrainingList(
    @SerializedName("common")
    val common: Any?,
    @SerializedName("data")
    val `data`: List<TrainingListData?>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("statuscode")
    val statuscode: String?
)
@Keep
data class TrainingListData(
    @SerializedName("date")
    val date: String?,
    @SerializedName("detailurl")
    val detailurl: String?,
    @SerializedName("topic")
    val topic: String?,
    @SerializedName("venue")
    val venue: String?
)