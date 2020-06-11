package com.bdjobs.app.videoInterview.data.models
import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

/*
@Keep
data class VideoInterviewListModel(
        @Json(name = "common")
    val common: Common?,
        @Json(name = "data")
    val `data`: List<InterviewListData>?,
        @Json(name = "message")
    val message: String?,
        @Json(name = "statuscode")
    val statuscode: String?
)



@Keep
data class Common(
    @Json(name = "totalVideoInterview")
    val totalVideoInterview: String?
)


@Keep
data class InterviewListData(
    @Json(name = "companyName")
    val companyName: String?,
    @Json(name = "invitationSeenDate")
    val invitationSeenDate: String?,
    @Json(name = "invitationSubmitedDate")
    val invitationSubmitedDate: String?,
    @Json(name = "inviteDate")
    val inviteDate: String?,
    @Json(name = "isToday")
    val isToday: String?,
    @Json(name = "jobId")
    val jobId: String?,
    @Json(name = "jobTitle")
    val jobTitle: String?,
    @Json(name = "videoStatus")
    val videoStatus: String?,
    @Json(name = "videoStatusCode")
    val videoStatusCode: String?
)*/


@Keep
data class VideoInterviewListModel(
        @Json(name = "common")
        val common: Common?,
        @Json(name = "data")
        val `data`: List<InterviewListData>?,
        @Json(name = "message")
        val message: String?,
        @Json(name = "statuscode")
        val statuscode: String?
)

@Keep
data class Common(
        @Json(name = "totalVideoInterview")
        val totalVideoInterview: String?
)

@Keep
data class InterviewListData(
        @Json(name = "companyName")
        val companyName: String?,
       /* @Json(name = "dateString")
        val dateString: String?,*/
        @Json(name = "employerSeenDate")
        val employerSeenDate: String?,
        @Json(name = "jobId")
        val jobId: String?,
        @Json(name = "jobTitle")
        val jobTitle: String?,
        @Json(name = "userSeenInterview")
        val userSeenInterview: String?,
        @Json(name = "videoStatus")
        val videoStatus: String?,
        @Json(name = "videoStatusCode")
        val videoStatusCode: String?
)