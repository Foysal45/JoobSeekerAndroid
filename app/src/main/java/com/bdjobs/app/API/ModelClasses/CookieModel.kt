package com.bdjobs.app.API.ModelClasses

import androidx.annotation.Keep

@Keep
data class CookieModel(
    val common: Any,
    val `data`: List<CookieModelData>,
    val message: String,
    val statuscode: String
)
@Keep
data class CookieModelData(
    val cookieName: String,
    val cookieValue: String,
    val domain: String,
    val expires: String,
    val path: String
)