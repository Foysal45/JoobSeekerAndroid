package com.bdjobs.app.API.ModelClasses
import androidx.annotation.Keep
import com.squareup.moshi.Json


//
// Created by Soumik on 7/8/2021.
// piyal.developer@gmail.com
// Copyright (c) 2021 Bdjobs.com Ltd. All rights reserved.
//

@Keep
data class EmpViewedResumeModel(
    @Json(name = "common")
    val common: CommonEmpV,
    @Json(name = "data")
    val `data`: List<DataEmpV>,
    @Json(name = "message")
    val message: String?,
    @Json(name = "statuscode")
    val statuscode: String?
)

@Keep
data class CommonEmpV(
    @Json(name = "totalNumberOfItems")
    val totalNumberOfItems: String?,
    @Json(name = "totalNumberOfPage")
    val totalNumberOfPage: String?
)

@Keep
data class DataEmpV(
    @Json(name = "companyName")
    val companyName: String?="",
    @Json(name = "numberOfTotalViewed")
    val numberOfTotalViewed: String?="",
    @Json(name = "sl")
    val sl: String?="",
    @Json(name = "viewBdjobsResume")
    val viewBdjobsResume: String?="0",
    @Json(name = "viewPersonalizeResume")
    val viewPersonalizeResume: String?="0",
    @Json(name = "viewSummeryView")
    val viewSummeryView: String?="0",
    @Json(name = "viewVideoResume")
    val viewVideoResume: String?="0",
    @Json(name = "viewedOn")
    val viewedOn: String?=""
)