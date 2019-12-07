package com.bdjobs.app.API.ModelClasses
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class HotJobs(
    @SerializedName("data")
    val `data`: List<HotJobsData?>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("statuscode")
    val statuscode: String?
)
@Keep
data class HotJobsData(
        @SerializedName("companyName")
    val companyName: String?,
        @SerializedName("jobTitles")
        val jobTitles: List<HotJobsJobTitle?>? = listOf(),
        @SerializedName("logoSource")
    val logoSource: String?
)
@Keep
data class HotJobsJobTitle(
    @SerializedName("jobTitle")
    val jobTitle: String?,
    @SerializedName("linkPage")
    val linkPage: String?
)