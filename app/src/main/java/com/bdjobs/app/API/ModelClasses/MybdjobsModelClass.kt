package com.bdjobs.app.API.ModelClasses

import androidx.annotation.Keep

@Keep
data class MybdjobsData(
        var itemID: String,
        val itemName: String,
        val backgroundID : Int,
        val resourceID : Int

)