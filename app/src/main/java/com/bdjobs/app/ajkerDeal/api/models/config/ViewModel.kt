package com.bdjobs.app.ajkerDeal.api.models.config

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ViewModel(
    @SerializedName("ViewType")
    var viewType: Int = 0,
    @SerializedName("IsActive")
    var isActive: Int = 0,
    @SerializedName("SpanCount")
    var spanCount: Int = 1,
    @SerializedName("Orientation")
    var orientation: Int = 0, // 0 H 1 V
    @SerializedName("ViewText")
    var viewText: String? = "",
    @SerializedName("IndexCountId")
    var indexCountId: Int = 20,
    @SerializedName("ImageUrl")
    var imageUrl: String? = "",
    @SerializedName("ActionUrlResponse")
    var actionUrlResponse: ActionModel? = null,
    @SerializedName("Data")
    var data: List<Any>? = null,
    @SerializedName("BlockColor")
    var blockColor: String? = null,
    @SerializedName("BlockTextColor")
    var blockTextColor: String? = null
) : Serializable



