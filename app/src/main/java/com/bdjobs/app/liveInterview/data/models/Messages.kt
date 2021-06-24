package com.bdjobs.app.liveInterview.data.models

import androidx.annotation.Keep

@Keep
data class Messages(
        var userName:String?="",
        var message:String? = "",
        var messageTime:String? = "",
        var itemType:Int? = 0
)