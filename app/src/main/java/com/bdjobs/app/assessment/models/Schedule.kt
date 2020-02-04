package com.bdjobs.app.assessment.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
data class Schedule(
    @Json(name = "common")
    var common: Any?,
    @Json(name = "data")
    var `data`: List<ScheduleData?>?,
    @Json(name = "message")
    var message: String?,
    @Json(name = "statuscode")
    var statuscode: String?
)

@Keep
data class ScheduleData(
        @Json(name = "actionType")
        var actionType: String?,
        @Json(name = "examType")
        var examType: String?,
        @Json(name = "istestOnline")
        var istestOnline: String?,
        @Json(name = "jobRoleId")
        var jobRoleId: String?,
        @Json(name = "newScheduleId")
        var newScheduleId: String?,
        @Json(name = "previousSchduleId")
        var previousSchduleId: String?,
        @Json(name = "testCenter")
        var testCenter: String?,
        @Json(name = "testDate")
        var testDate: String?,
        @Json(name = "testTime")
        var testTime: String?
)