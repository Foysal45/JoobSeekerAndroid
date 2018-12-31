package com.bdjobs.app.API.ModelClasses

data class CookieModel(
    val common: Any,
    val `data`: List<CookieModelData>,
    val message: String,
    val statuscode: String
)

data class CookieModelData(
    val cookieName: String,
    val cookieValue: String,
    val domain: String,
    val expires: String,
    val path: String
)