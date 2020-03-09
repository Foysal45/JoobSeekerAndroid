package com.bdjobs.app.assessment.models
import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class Result(
    @Json(name = "common")
    var common: Any?,
    @Json(name = "data")
    var `data`: List<ResultData?>?,
    @Json(name = "message")
    var message: String?,
    @Json(name = "statuscode")
    var statuscode: String?
)

@Keep
data class ResultData(
        @Json(name = "assessmentId")
        var assessmentId: String?,
        @Json(name = "isShowIncv")
        var isShowIncv: String?,
        @Json(name = "jobRoleId")
        var jobRoleId: String?,
        @Json(name = "modulewisescore")
        var modulewisescore: List<ModuleWiseScore?>?,
        @Json(name = "reportLink")
        var reportLink: String?,
        @Json(name = "res")
        var res: String?,
        @Json(name = "scheduleId")
        var scheduleId: String?,
        @Json(name = "testDate")
        var testDate: String?,
        @Json(name = "testName")
        var testName: String?,
        @Json(name = "totalScore")
        var totalScore: String?
)

@Keep
data class ModuleWiseScore(
        @Json(name = "avgScore")
        var avgScore: String?,
        @Json(name = "moduleId")
        var moduleId: String?,
        @Json(name = "moduleName")
        var moduleName: String?,
        @Json(name = "moduleWiseScore")
        var moduleWiseScore: String?,
        @Json(name = "strScoreMessage")
        var strScoreMessage: String?
)