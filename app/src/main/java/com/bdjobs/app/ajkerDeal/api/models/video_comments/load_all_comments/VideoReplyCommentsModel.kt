package com.bdjobs.app.ajkerDeal.api.models.video_comments.load_all_comments

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class VideoReplyCommentsModel(
    @SerializedName("CustomerId")
    val customerId: Int = 0,
    @SerializedName("CommentId")
    val commentId: Int = 0,
    @SerializedName("CustomerName")
    val customerName: String = "",
    @SerializedName("ReplyCommentId")
    val replyCommentId: Int = 0,
    @SerializedName("ReplyComment")
    val replyComment: String = "",
    @SerializedName("InsertedOn")
    val insertedOn: String = ""
)
