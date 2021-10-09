package com.bdjobs.app.ajkerDeal.api.models.video_comments

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class VideoInsertReplyCommentsRequest(
    @SerializedName("CustomerId")
    val customerId: Int = 0,
    @SerializedName("ReplyComment")
    val replyComment: String = "",
    @SerializedName("InsertedOn")
    val insertedOn: String = "",
    @SerializedName("VSCatalogId")
    val vsCatalogId: Int = 0,
    @SerializedName("CommentId")
    val commentId: Int = 0
)
