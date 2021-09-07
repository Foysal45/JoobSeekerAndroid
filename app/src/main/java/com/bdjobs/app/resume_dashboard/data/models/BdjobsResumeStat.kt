package com.bdjobs.app.resume_dashboard.data.models
import androidx.annotation.Keep
import com.squareup.moshi.Json


//
// Created by Soumik on 8/29/2021.
// piyal.developer@gmail.com
// Copyright (c) 2021 Bdjobs.com Ltd. All rights reserved.
//

@Keep
data class BdjobsResumeStat(
    @Json(name = "common")
    val common: Any?,
    @Json(name = "data")
    val `data`: List<DataBDS>?,
    @Json(name = "message")
    val message: String?,
    @Json(name = "statuscode")
    val statuscode: String?
)

@Keep
data class DataBDS(
    @Json(name = "bdjobsDownload")
    val bdjobsDownload: String?,
    @Json(name = "bdjobsEmailed")
    val bdjobsEmailed: String?,
    @Json(name = "bdjobsLastUpdateDate")
    val bdjobsLastUpdateDate: String?,
    @Json(name = "bdjobsViewed")
    val bdjobsViewed: String?
)