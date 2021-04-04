package com.bdjobs.app.API.ModelClasses
import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class BCWorkSkillModel(
    @field:Json(name = "common")
    val common: Any?,
    @field:Json(name = "data")
    val `data`: List<DataBCWS>?,
    @field:Json(name = "message")
    val message: String?,
    @field:Json(name = "statuscode")
    val statuscode: String?
)

@Keep
data class DataBCWS(
    @field:Json(name = "skillID")
    val skillID: String?,
    @field:Json(name = "skillNameBn")
    val skillNameBn: String?,
    @field:Json(name = "skillNameEn")
    val skillNameEn: String?
)