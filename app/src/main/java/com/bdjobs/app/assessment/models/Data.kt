package com.bdjobs.app.assessment.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
data class Data(
    @Json(name = "amcatJobId")
    var amcatJobId: String?,
    @Json(name = "assessmentId")
    var assessmentId: String?,
    @Json(name = "jobId")
    var jobId: String?,
    @Json(name = "jobRoleId")
    var jobRoleId: String?,
    @Json(name = "res")
    var res: String?,
    @Json(name = "scheduleId")
    var scheduleId: String?,
    @Json(name = "strExamType")
    var strExamType: String?,
    @Json(name = "testDate")
    var testDate: String?,
    @Json(name = "testName")
    var testName: String?
)