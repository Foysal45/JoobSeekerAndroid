package com.bdjobs.app.API.ModelClasses
import com.google.gson.annotations.SerializedName


data class EmployerListModelClass(
    @SerializedName("common")
    var common: EmployerListModelCommon? = EmployerListModelCommon(),
    @SerializedName("data")
    var `data`: List<EmployerListModelData?>? = listOf(),
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("statuscode")
    var statuscode: String? = ""
)

data class EmployerListModelData(
    @SerializedName("companyid")
    var companyid: String? = "",
    @SerializedName("companyname")
    var companyname: String? = "",
    @SerializedName("isaliasname")
    var isaliasname: String? = "",
    @SerializedName("totaljobs")
    var totaljobs: String? = ""
)

data class EmployerListModelCommon(
    @SerializedName("totalpages")
    var totalpages: String? = "",
    @SerializedName("totalrecordsfound")
    var totalrecordsfound: String? = ""
)