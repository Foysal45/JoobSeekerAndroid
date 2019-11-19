package com.bdjobs.app.API.ModelClasses
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
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
@Keep
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
@Keep
data class EmployerListModelCommon(
    @SerializedName("totalpages")
    var totalpages: String? = "",
    @SerializedName("totalrecordsfound")
    var totalrecordsfound: String? = ""
)