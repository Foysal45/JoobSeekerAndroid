package com.bdjobs.app.API.ModelClasses

import androidx.annotation.Keep

@Keep
data class AddExpModel(

        var workExp: String? = "",
        var expSource: ArrayList<String>?,
        var NTVQF: String? = ""


)