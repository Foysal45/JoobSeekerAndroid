package com.bdjobs.app.API.ModelClasses


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class VideoInterviewDetailsModel(
        @SerializedName("common")
        var common: VideoInterviewDetailsCommon?,
        @SerializedName("data")
        var `data`: List<VideoInterviewDetailsData?>?,
        @SerializedName("message")
        var message: String?, // Success
        @SerializedName("statuscode")
        var statuscode: String? // 0
) {
    @Keep
    data class VideoInterviewDetailsCommon(
            @SerializedName("companyName")
            var companyName: String?, // Utopia BD Limited1,
            @SerializedName("applyDate")
            var applyDate: String?, // 7 May 2020
            @SerializedName("applyId")
            var applyId: String?, // 183155995
            @SerializedName("jobId")
            var jobId: String?, // 904500,
            @SerializedName("showUndo")
            var showUndo: String?
    )

    @Keep
    data class VideoInterviewDetailsData(
            @SerializedName("examMessage")
            var examMessage: String?, // Congratulation! You have been selected for a Video Interview. The employer has some question(s) which you can see before recording the answer(s).
            @SerializedName("vInvitationDate")
            var vInvitationDate: String?, // 7 May 2020
            @SerializedName("vInvitationDeadline")
            var vInvitationDeadline: String?, // 22 May 2020
            @SerializedName("vStatus")
            var vStatus: String?, // Submit Video Interview
            @SerializedName("vButtonText")
            var vButtonText: String?, // Proceed for Video Interview
            @SerializedName("vTotalAttempt")
            var vTotalAttempt: String?, // 4
            @SerializedName("vTotalDuration")
            var vTotalDuration: String?, // 138
            @SerializedName("vTotalQuestion")
            var vTotalQuestion: String? // 5
    )
}