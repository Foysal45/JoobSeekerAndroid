package com.bdjobs.app.liveInterview.data.models
import androidx.annotation.Keep
import com.squareup.moshi.Json


//
// Created by Soumik on 5/18/2021.
// piyal.developer@gmail.com
// Copyright (c) 2021 Bdjobs.com Ltd. All rights reserved.
//

@Keep
data class ChatLogModel(
    @Json(name = "arrChatdata")
    val arrChatdata: List<ArrChatdata?>?,
    @Json(name = "Error")
    val error: String?,
    @Json(name = "Message")
    val message: String?
)

@Keep
data class ArrChatdata(
    @Json(name = "chatText")
    val chatText: String?,
    @Json(name = "chatTime")
    val chatTime: String?,
    @Json(name = "contactName")
    val contactName: String?,
    @Json(name = "designation")
    val designation: String?,
    @Json(name = "hostType")
    val hostType: String?,
    @Json(name = "targetUser")
    val targetUser: String?,
    @Json(name = "userID")
    val userID: String?
)