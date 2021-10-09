package com.bdjobs.app.ajkerDeal.api.models.config

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class ActionModel(
    @SerializedName("ActionName")
    val actionName: String? = "",
    @SerializedName("ActionType")
    var actionType: Int = 0,
    @SerializedName("ActionUrl")
    var actionUrl: String? = "",
    @SerializedName("EventAction")
    val eventAction: EventAction? = null
) : Serializable