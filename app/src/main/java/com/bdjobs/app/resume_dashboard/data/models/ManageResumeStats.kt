package com.bdjobs.app.resume_dashboard.data.models
import androidx.annotation.Keep
import com.squareup.moshi.Json


//
// Created by Soumik on 6/20/2021.
// piyal.developer@gmail.com
// Copyright (c) 2021 Bdjobs.com Ltd. All rights reserved.
//

@Keep
data class ManageResumeStats(
    @Json(name = "common")
    val common: Any?,
    @Json(name = "data")
    val `data`: List<Data?>,
    @Json(name = "message")
    val message: String?,
    @Json(name = "statuscode")
    val statuscode: String?
)

data class Data(
    @Json(name = "bdjobsEmailed")
    val bdjobsEmailed: String?="",
    @Json(name = "bdjobsViews")
    val bdjobsViews: String?="",
    @Json(name = "personalizedEmailed")
    val personalizedEmailed: String?="",
    @Json(name = "personalizedViews")
    val personalizedViews: String?="",
    @Json(name = "totalEmailed")
    val totalEmailed: String?="",
    @Json(name = "totalViews")
    val totalViews: String?="",
    @Json(name = "videoViews")
    val videoViews: String?=""
)