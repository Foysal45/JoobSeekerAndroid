package com.bdjobs.app.API.ModelClasses
import com.google.gson.annotations.SerializedName


data class EmployerJobListsModel(
    @SerializedName("common")
    var common: Any? = Any(),
    @SerializedName("data")
    var `data`: List<EmployerJobListsModelData?>? = listOf(),
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("statuscode")
    var statuscode: String? = ""
)

data class EmployerJobListsModelData(
    @SerializedName("address")
    var address: String? = "",
    @SerializedName("business")
    var business: String? = "",
    @SerializedName("deadline")
    var deadline: String? = "",
    @SerializedName("error")
    var error: String? = "",
    @SerializedName("isshowaddress")
    var isshowaddress: String? = "",
    @SerializedName("jobid")
    var jobid: String? = "",
    @SerializedName("jobtitle")
    var jobtitle: String? = "",
    @SerializedName("ln")
    var ln: String? = ""
)