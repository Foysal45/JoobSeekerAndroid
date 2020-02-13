package com.bdjobs.app.assessment.models


import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Schedule(
    @Json(name = "common")
    var common: String?,
    @Json(name = "data")
    var `data`: List<ScheduleData?>?,
    @Json(name = "message")
    var message: String?,
    @Json(name = "statuscode")
    var statuscode: String?
) : Parcelable

@Keep
@Parcelize
data class ScheduleData(
        @Json(name = "actionType")
        var actionType: String?,
        @Json(name = "examType")
        var examType: String?,
        @Json(name = "jobRoleId")
        var jobRoleId: String?,
        @Json(name = "scId")
        var scId: String?,
        @Json(name = "schlId")
        var schlId: String?,
        @Json(name = "testCenter")
        var testCenter: String?,
        @Json(name = "testDate")
        var testDate: String?,
        @Json(name = "testTime")
        var testTime: String?
):Parcelable

