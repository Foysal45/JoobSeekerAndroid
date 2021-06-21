package com.bdjobs.app.resume_dashboard.data.models

import androidx.annotation.Keep
import com.squareup.moshi.Json


//
// Created by Soumik on 6/21/2021.
// piyal.developer@gmail.com
// Copyright (c) 2021 Bdjobs.com Ltd. All rights reserved.
//

@Keep
data class ManageResumeDetailsStat(
    @Json(name = "common")
    val common: Any?,
    @Json(name = "data")
    val `data`: List<DataMRD>?,
    @Json(name = "message")
    val message: String?="",
    @Json(name = "statuscode")
    val statuscode: String?=""
)

@Keep
data class DataMRD(
    @Json(name = "academicInfo")
    val academicInfo: Int?,
    @Json(name = "bdjobsLastUpdateDate")
    val bdjobsLastUpdateDate: String?="",
    @Json(name = "bdjobsStatusPercentage")
    val bdjobsStatusPercentage: String?="",
    @Json(name = "experienceInfo")
    val experienceInfo: Int?,
    @Json(name = "personalDetailsInfo")
    val personalDetailsInfo: Int?,
    @Json(name = "personalizeLastUpdateDate")
    val personalizeLastUpdateDate: String?="",
    @Json(name = "personalizefileName")
    val personalizefileName: String?="",
    @Json(name = "photographInfo")
    val photographInfo: Int?,
    @Json(name = "proQualificationInfo")
    val proQualificationInfo: Int?,
    @Json(name = "referenceInfo")
    val referenceInfo: Int?,
    @Json(name = "specializationInfo")
    val specializationInfo: Int?,
    @Json(name = "trainingInfo")
    val trainingInfo: Int?,
    @Json(name = "videoLastUpdateDate")
    val videoLastUpdateDate: String?="",
    @Json(name = "videoResumeVisibility")
    val videoResumeVisibility: String?="",
    @Json(name = "videoStatusPercentage")
    val videoStatusPercentage: String?=""
)