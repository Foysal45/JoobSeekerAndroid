package com.bdjobs.app.API.ModelClasses
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class ShortListedJobModel(
    @SerializedName("common")
    var common: ShortListedJobCommon? = ShortListedJobCommon(),
    @SerializedName("data")
    var `data`: List<ShortListedJobData?>? = listOf(),
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("statuscode")
    var statuscode: String? = ""
)
@Keep
data class ShortListedJobData(
    @SerializedName("companyname")
    var companyname: String? = "",
    @SerializedName("deadline")
    var deadline: String? = "",
    @SerializedName("eduRec")
    var eduRec: String? = "",
    @SerializedName("experience")
    var experience: String? = "",
    @SerializedName("jobid")
    var jobid: String? = "",
    @SerializedName("jobtitle")
    var jobtitle: String? = "",
    @SerializedName("lantype")
    var lantype: String? = "",
    @SerializedName("logo")
    var logo: String? = "",
    @SerializedName("standout")
    var standout: String? = ""
)
@Keep
data class ShortListedJobCommon(
    @SerializedName("appliedid")
    var appliedid: List<String?>? = listOf(),
    @SerializedName("totalcount")
    var totalcount: String? = ""
)
@Keep
data class UnshorlistJobModel(
    @SerializedName("common")
    var common: Any? = Any(),
    @SerializedName("data")
    var `data`: Any? = Any(),
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("statuscode")
    var statuscode: String? = ""
)
@Keep
data class ShortlistJobModel(
    @SerializedName("common")
    var common: Any? = Any(),
    @SerializedName("data")
    var `data`: List<ShortlistJobModelData?>? = listOf(),
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("statuscode")
    var statuscode: String? = ""
)
@Keep
data class ShortlistJobModelData(
    @SerializedName("error")
    var error: String? = "",
    @SerializedName("message")
    var message: String? = ""
)