package com.bdjobs.app.editResume.adapters.models

import com.google.gson.annotations.SerializedName

data class DataItem(

        @field:SerializedName("departmant")
        val departmant: String? = null,

        @field:SerializedName("areaofExperience")
        val areaofExperience: String? = null,

        @field:SerializedName("messageType")
        val messageType: String? = null,

        @field:SerializedName("exp_id")
        val expId: String? = null,

        @field:SerializedName("responsibility")
        val responsibility: String? = null,

        @field:SerializedName("companyName")
        val companyName: String? = null,

        @field:SerializedName("companyBusiness")
        val companyBusiness: String? = null,

        @field:SerializedName("from")
        val from: String? = null,

        @field:SerializedName("to")
        val to: String? = null,

        @field:SerializedName("positionHeld")
        val positionHeld: String? = null,

        @field:SerializedName("companyLocation")
        val companyLocation: String? = null
)