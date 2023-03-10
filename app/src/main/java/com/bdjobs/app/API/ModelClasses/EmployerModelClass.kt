package com.bdjobs.app.API.ModelClasses
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class FollowUnfollowModelClass(
    @SerializedName("common")
    var common: Any?,
    @SerializedName("data")
    var `data`: List<FollowUnfollowModelClassData?>?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("statuscode")
    var statuscode: String?
)
@Keep
data class FollowUnfollowModelClassData(
    @SerializedName("jobcount")
    var jobcount: String?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("status")
    var status: String?
)