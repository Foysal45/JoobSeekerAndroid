package com.bdjobs.app.ajkerDeal.api.models.firebase

import androidx.annotation.Keep

@Keep
data class LiveProductEvent(
    var time: Long = 0L,
    var imageUrl: String = "",
    var price: Int? = 0,
    var productCode: Int? = 0,
    var productHighlight: String = "",
    var productHighlightBGColor: String? = ""
)

