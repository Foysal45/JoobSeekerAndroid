package com.bdjobs.app.resume_dashboard.data.models
import androidx.annotation.Keep
import com.squareup.moshi.Json


//
// Created by Soumik on 6/29/2021.
// piyal.developer@gmail.com
// Copyright (c) 2021 Bdjobs.com Ltd. All rights reserved.
//

@Keep
data class ResumePrivacyUpdate(
    @Json(name = "common")
    val common: Any?,
    @Json(name = "data")
    val `data`: Any?,
    @Json(name = "message")
    val message: String?,
    @Json(name = "statuscode")
    val statuscode: String?
)