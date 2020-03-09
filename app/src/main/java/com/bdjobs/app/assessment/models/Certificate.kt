package com.bdjobs.app.assessment.models


import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Certificate(
        @Json(name = "common")
        var common: String?,
        @Json(name = "data")
        var data: List<CertificateData?>?,
        @Json(name = "message")
        var message: String?,
        @Json(name = "statuscode")
        var statuscode: String?
) : Parcelable


@Parcelize
@Keep
data class CertificateData(
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
) : Parcelable
