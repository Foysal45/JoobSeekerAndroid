package com.bdjobs.app.ajkerDeal.api.models.video_comments

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class VideoInsertCommentsRequest(
    @SerializedName("CustomerId")
    val customerId: Int = 0,
    @SerializedName("Comment")
    val comment: String = "",
    @SerializedName("InsertedOn")
    val insertedOn: String = "",
    @SerializedName("VSCatalogId")
    val vsCatalogId: Int = 0
)