package com.bdjobs.app.assessment.models

data class Booking (
    var userId : String? = "",
    var decodeId : String? = "",
    var strActionType : String? = "",
    var scId : String? = "",
    var schId : String? = "",
    var opId : String? = "",
    var fltBdjAmount : String? = "",
    var strTransactionDate : String? = "",
    var isFromHome : String? = ""
)