package com.bdjobs.app.ajkerDeal.api.models.order

import com.google.gson.annotations.SerializedName

data class DeliveryTypeModel(
    @SerializedName("Id")
    var id: Int = 0,
    @SerializedName("Name")
    var name: String = "",
    @SerializedName("InsideDhakaSpeed")
    var insideDhakaSpeed: String = "",
    @SerializedName("OutsideDhakaSpeed")
    var outsideDhakaSpeed: String = ""
)