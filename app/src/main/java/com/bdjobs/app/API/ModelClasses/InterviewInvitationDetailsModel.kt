package com.bdjobs.app.API.ModelClasses
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class InvitationDetailModels(
    @SerializedName("common")
    val common: InvitationDetailModelsCommon? = InvitationDetailModelsCommon(),
    @SerializedName("data")
    val `data`: List<InvitationDetailModelsData>? = listOf(),
    @SerializedName("message")
    val message: String? = "",
    @SerializedName("statuscode")
    val statuscode: String? = ""
)
@Keep
data class InvitationDetailModelsCommon(
    @SerializedName("applyDate")
    val applyDate: String? = "",
    @SerializedName("applyId")
    val applyId: String? = "",
    @SerializedName("jobClosed")
    val jobClosed: String? = "",
    @SerializedName("rating")
    val rating: String? = "",
    @SerializedName("ratingDate")
    val ratingDate: String? = "",
    @SerializedName("ratingMessage")
    val ratingMessage: String? = "",
    @SerializedName("showUndo")
    val showUndo: String? = ""
)
@Keep
data class InvitationDetailModelsData(
    @SerializedName("activity")
    val activity: String? = "",
    @SerializedName("confimationStatus")
    val confimationStatus: String? = "",
    @SerializedName("confirmationDate")
    val confirmationDate: String? = "",
    @SerializedName("confirmationMessage")
    val confirmationMessage: String? = "",
    @SerializedName("direction")
    val direction: Direction? = Direction(),
    @SerializedName("examDate")
    val examDate: String? = "",
    @SerializedName("examMessage")
    val examMessage: String? = "",
    @SerializedName("examTime")
    val examTime: String? = "",
    @SerializedName("examTitle")
    val examTitle: String? = "",
    @SerializedName("invitationDate")
    val invitationDate: String? = "",
    @SerializedName("invitationId")
    val invitationId: String? = "",
    @SerializedName("previousScheduleDate")
    val previousScheduleDate: String? = "",
    @SerializedName("previousScheduleTime")
    val previousScheduleTime: String? = "",
    @SerializedName("venue")
    val venue: String? = "",
    @SerializedName("alertMessage")
    val alertMessage: String? = "",
    @SerializedName("contactNo")
    val contactNo: String? = ""
)
@Keep
data class Direction(
    @SerializedName("lan")
    val lan: String? = "",
    @SerializedName("lat")
    val lat: String? = ""
)