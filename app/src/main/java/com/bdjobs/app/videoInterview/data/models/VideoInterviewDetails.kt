package com.bdjobs.app.videoInterview.data.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class VideoInterviewDetails(
    @Json(name = "common")
    var common: Common?,
    @Json(name = "data")
    var `data`: List<Data?>?,
    @Json(name = "message")
    var message: String?, // Success
    @Json(name = "statuscode")
    var statuscode: String? // 0
) {
    @Keep
    @JsonClass(generateAdapter = true)
    data class Common(
        @Json(name = "applyDate")
        var applyDate: String?, // 19 Dec 2019
        @Json(name = "applyId")
        var applyId: String?, // 175024003
        @Json(name = "companyName")
        var companyName: String?, // Utopia Test Company
        @Json(name = "showUndo")
        var showUndo: String?,
        @Json(name = "strVideoSubmitMessage")
        var strVideoSubmitMessage: String?
    )

    @Keep
    @JsonClass(generateAdapter = true)
    data class Data(
        @Json(name = "vButtonName")
        var vButtonName: String?,
        @Json(name = "vEmployerStatus")
        var vEmployerStatus: String?,
        @Json(name = "vInvitationDate")
        var vInvitationDate: String?, // 20 May 2020
        @Json(name = "vInvitationDeadline")
        var vInvitationDeadline: String?, // 28 May 2020
        @Json(name = "vStatuCode")
        var vStatuCode: String?, // 6
        @Json(name = "vStatus")
        var vStatus: String?, //  You did not participate in Video Interview.
        @Json(name = "vTotalAttempt")
        var vTotalAttempt: String?, // 2
        @Json(name = "vTotalDuration")
        var vTotalDuration: String?, // 64
        @Json(name = "vTotalQuestion")
        var vTotalQuestion: String? // 3
    )
}