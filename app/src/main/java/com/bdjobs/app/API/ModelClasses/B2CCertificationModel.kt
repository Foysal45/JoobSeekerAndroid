package com.bdjobs.app.API.ModelClasses
import com.google.gson.annotations.SerializedName


data class AssesmentCompleteModel(
    @SerializedName("common")
    val common: Any?,
    @SerializedName("data")
    val `data`: List<AssesmentCompleteData?>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("statuscode")
    val statuscode: String?
)

data class AssesmentCompleteData(
    @SerializedName("aid")
    val aid: String?,
    @SerializedName("ajid")
    val ajid: String?,
    @SerializedName("jid")
    val jid: String?,
    @SerializedName("jobRole")
    val jobRole: String?,
    @SerializedName("jrid")
    val jrid: String?,
    @SerializedName("res")
    val res: String?,
    @SerializedName("sType")
    val sType: String?,
    @SerializedName("sid")
    val sid: String?,
    @SerializedName("testDate")
    val testDate: String?
)