package com.bdjobs.app.videoInterview.data.models
import androidx.annotation.Keep
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Json

@Keep
@JsonClass(generateAdapter = true)
data class VideoInterviewList(
        @Json(name = "common")
        var common: Common?,
        @Json(name = "data")
        var `data`: List<Data?>?,
        @Json(name = "message")
        var message: String?, // Success
        @Json(name = "statuscode")
        var statuscode: String? // 0
)

{
        @Keep
        @JsonClass(generateAdapter = true)
        data class Common(
                @Json(name = "totalVideoInterview")
                val totalVideoInterview: String? // 21
        )

        @Keep
        @JsonClass(generateAdapter = true)
        data class Data(
                @Json(name = "companyName")
                val companyName: String?, // Utopia BD Ltd 2
                @Json(name = "jobTitle")
                val jobTitle: String?, // Salary test by payment
                @Json(name = "jobId")
                val jobId: String?, // 905069
                @Json(name = "videoStatusCode")
                val videoStatusCode: String?, // 2
                @Json(name = "videoStatus")
                val videoStatus: String?, // Due Submission
                @Json(name = "userSeenInterview")
                val userSeenInterview: String?, // True
                @Json(name = "employerSeenDate")
                val employerSeenDate: String?, //
                @Json(name = "dateStringForSubmission")
                val dateStringForSubmission: String?, //
                @Json(name = "dateStringForInvitaion")
                val dateStringForInvitaion: String?, // Invited on: 16 Jun 2020
                @Json(name = "videoSubmittedDeadline")
                val videoSubmittedDeadline: String? // 16 Jun 2020
        )
}
