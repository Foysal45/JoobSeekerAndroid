package com.bdjobs.app.liveInterview.data.models
import androidx.annotation.Keep
import com.squareup.moshi.Json


//
// Created by Soumik on 5/18/2021.
// piyal.developer@gmail.com
// Copyright (c) 2021 Bdjobs.com Ltd. All rights reserved.
//

@Keep
data class PostChatModel(
    @Json(name = "message")
    val message: String?,
    @Json(name = "statuscode")
    val statuscode: String
)