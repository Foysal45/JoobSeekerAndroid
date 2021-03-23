package com.bdjobs.app.API.ModelClasses
import androidx.annotation.Keep
import com.squareup.moshi.Json


@Keep
data class AutoSuggestionModel(
    @field:Json(name = "common")
    val common: String?,
    @field:Json(name = "data")
    val `data`: List<DataAutoSuggestion>?,
    @field:Json(name = "message")
    val message: String?,
    @field:Json(name = "statuscode")
    val statuscode: String?
)

@Keep
data class DataAutoSuggestion(
    @field:Json(name = "subCatId")
    val subCatId: String?,
    @field:Json(name = "subName")
    val subName: String?,
    @field:Json(name = "subTypeId")
    val subTypeId: String?
)