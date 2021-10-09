package com.bdjobs.app.ajkerDeal.api

import androidx.annotation.Keep

@Keep
data class PagingModel<T> (
    var isInitLoad: Boolean = false,
    var totalCount: Int = 0,
    var dataList: T
)
