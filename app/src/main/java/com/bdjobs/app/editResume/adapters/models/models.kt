package com.bdjobs.app.editResume.adapters.models

import com.google.gson.annotations.SerializedName

// Employment history
data class AreaofExperienceItem(

        @field:SerializedName("exps_name")
        val expsName: String? = null,

        @field:SerializedName("id")
        val id: String? = null
)

data class DataItem(

        @field:SerializedName("departmant")
        val departmant: String? = null,

        @field:SerializedName("areaofExperience")
        val areaofExperience: List<AreaofExperienceItem?>? = null,

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
        val data: List<DataItem?>? = null,

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

        @field:SerializedName("isResumeUpdate")
        val isResumeUpdate: String? = null,

        @field:SerializedName("data")
        val data: Any? = null,

        @field:SerializedName("common")
        val common: Any? = null,

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

// Personal Info
data class P_DataItem(

        @field:SerializedName("firstName")
        val firstName: String? = null,

        @field:SerializedName("lastName")
        val lastName: String? = null,

        @field:SerializedName("fatherName")
        val fatherName: String? = null,

        @field:SerializedName("dateofBirth")
        val dateofBirth: String? = null,

        @field:SerializedName("messageType")
        val messageType: String? = null,

        @field:SerializedName("gender")
        val gender: String? = null,

        @field:SerializedName("motherName")
        val motherName: String? = null,

        @field:SerializedName("nationalIdNo")
        val nationalIdNo: String? = null,

        @field:SerializedName("maritalStatus")
        val maritalStatus: String? = null,

        @field:SerializedName("nationality")
        val nationality: String? = null,

        @field:SerializedName("religion")
        val religion: String? = null
)

data class GetPersInfo(

        @field:SerializedName("statuscode")
        val statuscode: String? = null,

        @field:SerializedName("data")
        val data: List<P_DataItem?>? = null,

        @field:SerializedName("common")
        val common: Any? = null,

        @field:SerializedName("message")
        val message: String? = null
)

// Contact Info
data class C_DataItem(
        @SerializedName("addressType1")
        val addressType1: String?,
        @SerializedName("addressType2")
        val addressType2: String?,
        @SerializedName("alternativeEmail")
        val alternativeEmail: String?,
        @SerializedName("primaryEmail")
        val email: String?,
        @SerializedName("mobileNo1")
        val homePhone: String?,
        @SerializedName("messageType")
        val messageType: String?,
        @SerializedName("primaryMobileNo")
        val mobile: String?,
        @SerializedName("mobileNo2")
        val officePhone: String?,
        @SerializedName("permanentAddressID")
        val permanentAddressID: String?,
        @SerializedName("permanentCountry")
        val permanentCountry: String?,
        @SerializedName("permanentDistrict")
        val permanentDistrict: String?,
        @SerializedName("permanentInsideOutsideBD")
        val permanentInsideOutsideBD: String?,
        @SerializedName("permanentPostOffice")
        val permanentPostOffice: String?,
        @SerializedName("permanentThana")
        val permanentThana: String?,
        @SerializedName("permanentVillage")
        val permanentVillage: String?,
        @SerializedName("presentAddressID")
        val presentAddressID: String?,
        @SerializedName("presentCountry")
        val presentCountry: String?,
        @SerializedName("presentDistrict")
        val presentDistrict: String?,
        @SerializedName("presentInsideOutsideBD")
        val presentInsideOutsideBD: String?,
        @SerializedName("presentPostOffice")
        val presentPostOffice: String?,
        @SerializedName("presentThana")
        val presentThana: String?,
        @SerializedName("presentVillage")
        val presentVillage: String?
)

data class GetContactInfo(
        @SerializedName("common")
        val common: Any?,
        @SerializedName("data")
        val `data`: List<C_DataItem?>?,
        @SerializedName("message")
        val message: String?,
        @SerializedName("statuscode")
        val statuscode: String?
)

// Career Info
data class Ca_DataItem(

        @field:SerializedName("presentSalary")
        val presentSalary: String? = null,

        @field:SerializedName("messageType")
        val messageType: String? = null,

        @field:SerializedName("availableFor")
        val availableFor: String? = null,

        @field:SerializedName("lookingFor")
        val lookingFor: String? = null,

        @field:SerializedName("ExpectedSalary")
        val expectedSalary: String? = null,

        @field:SerializedName("objective")
        val objective: String? = null
)

data class GetCarrerInfo(

        @field:SerializedName("statuscode")
        val statuscode: String? = null,

        @field:SerializedName("data")
        val data: List<Ca_DataItem?>? = null,

        @field:SerializedName("common")
        val common: Any? = null,

        @field:SerializedName("message")
        val message: String? = null
)

// ORI Info
data class GetORIResponse(

        @field:SerializedName("statuscode")
        val statuscode: String? = null,

        @field:SerializedName("data")
        val data: List<ORIdataItem?>? = null,

        @field:SerializedName("common")
        val common: Any? = null,

        @field:SerializedName("message")
        val message: String? = null
)

data class ORIdataItem(

        @field:SerializedName("messageType")
        val messageType: String? = null,

        @field:SerializedName("keywords")
        val keywords: String? = null,

        @field:SerializedName("specialQualifications")
        val specialQualifications: String? = null,

        @field:SerializedName("careerSummery")
        val careerSummery: String? = null
)

// Training Info
data class Tr_DataItem(
        val duration: String? = null,
        val country: String? = null,
        val messageType: String? = null,
        val year: String? = null,
        val topic: String? = null,
        val institute: String? = null,
        val location: String? = null,
        val title: String? = null,
        val trId: String? = null
)

data class GetTrainingInfo(
        val statuscode: String? = null,
        val data: List<Tr_DataItem?>? = null,
        val common: Any? = null,
        val message: String? = null
)

// Landing List


//LanguageInfo

data class LanguageModel(
        @SerializedName("common")
        val common: Any?,
        @SerializedName("data")
        val `data`: List<LanguageDataModel?>?,
        @SerializedName("message")
        val message: String?,
        @SerializedName("statuscode")
        val statuscode: String?
)

data class LanguageDataModel(
        @SerializedName("language")
        val language: String?,
        @SerializedName("ln_id")
        val lnId: String?,
        @SerializedName("messageType")
        val messageType: String?,
        @SerializedName("reading")
        val reading: String?,
        @SerializedName("speaking")
        val speaking: String?,
        @SerializedName("writing")
        val writing: String?
)


data class ReferenceModel(
        @SerializedName("common")
        val common: Any?,
        @SerializedName("data")
        val `data`: List<ReferenceDataModel?>?,
        @SerializedName("message")
        val message: String?,
        @SerializedName("statuscode")
        val statuscode: String?
)

data class ReferenceDataModel(
        @SerializedName("address")
        val address: String?,
        @SerializedName("designation")
        val designation: String?,
        @SerializedName("email")
        val email: String?,
        @SerializedName("messageType")
        val messageType: String?,
        @SerializedName("mobile")
        val mobile: String?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("organization")
        val organization: String?,
        @SerializedName("phone_office")
        val phoneOffice: String?,
        @SerializedName("phone_res")
        val phoneRes: String?,
        @SerializedName("ref_id")
        val refId: String?,
        @SerializedName("relation")
        val relation: String?
)


data class ProfessionalModel(
        @SerializedName("common")
        val common: Any?,
        @SerializedName("data")
        val `data`: List<ProfessionalDataModel?>?,
        @SerializedName("message")
        val message: String?,
        @SerializedName("statuscode")
        val statuscode: String?
)

data class ProfessionalDataModel(
        @SerializedName("certification")
        val certification: String?,
        @SerializedName("from")
        val from: String?,
        @SerializedName("institute")
        val institute: String?,
        @SerializedName("location")
        val location: String?,
        @SerializedName("messageType")
        val messageType: String?,
        @SerializedName("prId")
        val prId: String?,
        @SerializedName("to")
        val to: String?
)
