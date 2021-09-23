package com.bdjobs.app.ajkerDeal.api

data class PagingModel<T> (
    var isInitLoad: Boolean = false,
    var totalCount: Int = 0,
    var dataList: T
)
