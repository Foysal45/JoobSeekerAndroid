package com.bdjobs.app.API.ModelClasses
import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

import com.squareup.moshi.Json



//
// Created by Soumik on 4/8/2021.
// Copyright (c) 2021 Soumik.  All rights reserved.
//@JsonClass(generateAdapter = true)

@Keep
data class ClientAdModel(
    @field:Json(name = "data")
    val `data`: List<DataClientAd>,
    @field:Json(name = "error")
    val error: String,
    @field:Json(name = "Success")
    val success: Int
)

//@JsonClass(generateAdapter = true)
@Keep
data class DataClientAd(
    @field:Json(name = "adurl")
    val adurl: String,
    @field:Json(name = "height")
    val height: Int,
    @field:Json(name = "imageurl")
    val imageurl: String,
    @field:Json(name = "position")
    val position: String,
    @field:Json(name = "unit")
    val unit: String,
    @field:Json(name = "width")
    val width: Int
)
