package com.bdjobs.app.editResume.adapters.models

import com.google.gson.annotations.SerializedName

// Employment history

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

data class GetExps(

        @field:SerializedName("statuscode")
        val statuscode: String? = null,

        @field:SerializedName("data")
        val data: ArrayList<DataItem?>? = null,

        @field:SerializedName("common")
        val common: Any? = null,

        @field:SerializedName("message")
        val message: String? = null
)

data class GetArmyEmpHis(

        @field:SerializedName("statuscode")
        val statuscode: String? = null,

        @field:SerializedName("common")
        val common: Any? = null,

        @field:SerializedName("data")
        val armydata: List<ArmydataItem?>? = null,

        @field:SerializedName("message")
        val message: String? = null
)

data class ArmydataItem(

        @field:SerializedName("Trade")
        val trade: String? = null,

        @field:SerializedName("arm_id")
        val armId: String? = null,

        @field:SerializedName("Type")
        val type: String? = null,

        @field:SerializedName("Verified")
        val verified: String? = null,

        @field:SerializedName("messageType")
        val messageType: String? = null,

        @field:SerializedName("DateOfRetirement")
        val dateOfRetirement: String? = null,

        @field:SerializedName("DateOfCommission")
        val dateOfCommission: String? = null,

        @field:SerializedName("Arms")
        val arms: String? = null,

        @field:SerializedName("Rank")
        val rank: String? = null,

        @field:SerializedName("Course")
        val course: String? = null,

        @field:SerializedName("ba_no1")
        val baNo1: String? = null,

        @field:SerializedName("ba_no2")
        val baNo2: String? = null
)

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

// Education and Training

data class GetAcademicInfo(

        @field:SerializedName("statuscode")
        val statuscode: String? = null,

        @field:SerializedName("data")
        val data: List<AcaDataItem?>? = null,

        @field:SerializedName("common")
        val common: Any? = null,

        @field:SerializedName("message")
        val message: String? = null
)

data class AcaDataItem(

        @field:SerializedName("eduType")
        val eduType: String? = null,

        @field:SerializedName("resultId")
        val resultId: String? = null,

        @field:SerializedName("concentration/major/group")
        val concentrationMajorGroup: String? = null,

        @field:SerializedName("exam/degreeTitle")
        val examDegreeTitle: String? = null,

        @field:SerializedName("scale")
        val scale: String? = null,

        @field:SerializedName("marks")
        val marks: String? = null,

        @field:SerializedName("otherEduType")
        val otherEduType: String? = null,

        @field:SerializedName("showMarks")
        val showMarks: String? = null,

        @field:SerializedName("isItDegree")
        val isItDegree: String? = null,

        @field:SerializedName("levelofEducation")
        val levelofEducation: String? = null,

        @field:SerializedName("instituteName")
        val instituteName: String? = null,

        @field:SerializedName("acId")
        val acId: String? = null,

        @field:SerializedName("result")
        val result: String? = null,

        @field:SerializedName("duration")
        val duration: String? = null,

        @field:SerializedName("yearofPAssing")
        val yearofPAssing: String? = null,

        @field:SerializedName("acievement")
        val acievement: String? = null,

        @field:SerializedName("messageType")
        val messageType: String? = null,

        @field:SerializedName("instituteType")
        val instituteType: String? = null
)