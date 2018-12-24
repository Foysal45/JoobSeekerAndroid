package com.bdjobs.app.editResume.adapters.models

import com.google.gson.annotations.SerializedName

data class AddorUpdateModel(

        @field:SerializedName("statuscode")
        val statuscode: String? = null,

        @field:SerializedName("data")
        val data: List<Any?>? = null,

        @field:SerializedName("common")
        val common: List<Any?>? = null,

        @field:SerializedName("message")
        val message: String? = null
)