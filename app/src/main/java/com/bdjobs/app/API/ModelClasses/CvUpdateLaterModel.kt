package com.bdjobs.app.API.ModelClasses
import com.squareup.moshi.Json


//
// Created by Soumik on 7/1/2021.
// piyal.developer@gmail.com
// Copyright (c) 2021 Bdjobs.com Ltd. All rights reserved.
//

data class CvUpdateLaterModel(
    @Json(name = "error")
    val error: String?="",
    @Json(name = "isUpdated")
    val isUpdated: Int?,
    @Json(name = "message")
    val message: String?
)