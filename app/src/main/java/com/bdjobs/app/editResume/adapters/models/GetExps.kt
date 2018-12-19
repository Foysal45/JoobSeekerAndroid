package com.bdjobs.app.editResume.adapters.models

import com.google.gson.annotations.SerializedName

data class GetExps(

        @field:SerializedName("statuscode")
        val statuscode: String? = null,

        @field:SerializedName("data")
        val data: List<DataItem?>? = null,

        @field:SerializedName("common")
        val common: Any? = null,

        @field:SerializedName("message")
        val message: String? = null
)