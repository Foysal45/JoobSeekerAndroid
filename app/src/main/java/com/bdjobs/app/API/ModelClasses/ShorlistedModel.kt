package com.bdjobs.app.API.ModelClasses

data class ShortListedJobModel(
    val common: ShortListedJobCommon,
    val `data`: List<ShortListedJobData>,
    val message: String,
    val statuscode: String
)

data class  ShortListedJobData(
    val companyname: String,
    val deadline: String,
    val eduRec: String,
    val experience: String,
    val jobid: String,
    val jobtitle: String,
    val lantype: String,
    val logo: String,
    val standout: String
)

data class  ShortListedJobCommon(
    val appliedid: List<String>,
    val totalcount: String
)