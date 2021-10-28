package com.bdjobs.app.ajkerDeal.api.models.video_comments.load_all_comments

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class VideoCommentsModel(
    @SerializedName("CommentId")
    val commentId: Int = 0,
    @SerializedName("Comment")
    val comment: String = "",
    @SerializedName("CustomerId")
    val customerId: Int = 0,
    @SerializedName("CustomerName")
    val customerName: String = "",
    @SerializedName("InsertedOn")
    val insertedOn: String = "",
    @SerializedName("ReplyComments")
    val replyComments: List<VideoReplyCommentsModel>
)
