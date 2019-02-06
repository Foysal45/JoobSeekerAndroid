package com.bdjobs.app.API.ModelClasses
import com.google.gson.annotations.SerializedName


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