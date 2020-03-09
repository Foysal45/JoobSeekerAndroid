package com.bdjobs.app.assessment.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
data class BookingResponse(
    @Json(name = "common")
    var common: Any?,
    @Json(name = "data")
    var `data`: List<BookingResponseData?>?,
    @Json(name = "message")
    var message: String?,
    @Json(name = "statuscode")
    var statuscode: String?
)

@Keep
data class BookingResponseData(
        @Json(name = "result")
        var result: String?,
        @Json(name = "scID")
        var scID: String?
)