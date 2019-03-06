package com.bdjobs.app.API.ModelClasses
import com.google.gson.annotations.SerializedName


data class HotJobs(
    @SerializedName("data")
    val `data`: List<HotJobsData?>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("statuscode")
    val statuscode: String?
)

data class HotJobsData(
        @SerializedName("companyName")
    val companyName: String?,
        @SerializedName("jobTitles")
        val jobTitles: List<HotJobsJobTitle?>? = listOf(),
        @SerializedName("logoSource")
    val logoSource: String?
)

data class HotJobsJobTitle(
    @SerializedName("jobTitle")
    val jobTitle: String?,
    @SerializedName("linkPage")
    val linkPage: String?
)