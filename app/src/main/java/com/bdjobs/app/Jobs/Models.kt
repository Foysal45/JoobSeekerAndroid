package com.bdjobs.app.Jobs

import com.google.gson.annotations.SerializedName

data class JobDetailJsonModel(
        @SerializedName("statuscode") val statuscode: String?,
        @SerializedName("message") val message: String?,
        @SerializedName("data") val data: List<Data?>?,
        @SerializedName("common") val common: Any?
)

data class Data(
        @SerializedName("JobId") val jobId: String?,
        @SerializedName("JobFound") val jobFound: String?,
        @SerializedName("CompnayName") val compnayName: String?,
        @SerializedName("JobTitle") val jobTitle: String?,
        @SerializedName("PostedOn") val postedOn: String?,
        @SerializedName("Deadline") val deadline: String?,
        @SerializedName("JobVacancies") val jobVacancies: String?,
        @SerializedName("JobDescription") val jobDescription: String?,
        @SerializedName("JobNature") val jobNature: String?,
        @SerializedName("EducationRequirements") val educationRequirements: String?,
        @SerializedName("Publication(s)") val publications: String?,
        @SerializedName("Age") val age: String?,
        @SerializedName("experience") val experience: String?,
        @SerializedName("Gender") val gender: String?,
        @SerializedName("AdditionJobRequirements") val additionJobRequirements: String?,
        @SerializedName("JobLocation") val jobLocation: String?,
        @SerializedName("OnlineApply") val onlineApply: String?,
        @SerializedName("CompanyBusiness") val companyBusiness: String?,
        @SerializedName("CompanyAddress") val companyAddress: String?,
        @SerializedName("CompanyHideAddress") val companyHideAddress: String?,
        @SerializedName("CompanyWeb") val companyWeb: String?,
        @SerializedName("JobAppliedEmail") val jobAppliedEmail: String?,
        @SerializedName("JobSource") val jobSource: String?,
        @SerializedName("JobOtherBenifits") val jobOtherBenifits: String?,
        @SerializedName("JobSalaryRange") val jobSalaryRange: String?,
        @SerializedName("JobSalaryRangeText") val jobSalaryRangeText: String?,
        @SerializedName("overseasnote") val overseasnote: String?,
        @SerializedName("JobAdType") val jobAdType: String?,
        @SerializedName("JobLOgoName") val jobLOgoName: String?,
        @SerializedName("JobKeyPoints") val jobKeyPoints: String?,
        @SerializedName("ApplyInstruction") val applyInstruction: String?,
        @SerializedName("userID") val userID: String?,
        @SerializedName("Photograph") val photograph: String?,
        @SerializedName("PhotographMsg") val photographMsg: String?,
        @SerializedName("JObIMage") val jObIMage: String?,
        @SerializedName("upcoming") val upcoming: String?,
        @SerializedName("upcomingln") val upcomingln: String?,
        @SerializedName("CompanyOtherJ0bs") val companyOtherJ0bs: String?,
        @SerializedName("CompanyID") val companyID: String?,
        @SerializedName("CompanyNameENG") val companyNameENG: String?,
        @SerializedName("AssessmentRequired") val assessmentRequired: String?,
        @SerializedName("Context") val context: String?,
        @SerializedName("error") val error: String?
)






data class GetResponseJobLIst(
        val statuscode: String? = null,
        val data: List<DataItem>? = null,
        val common: CommonOld? = null,
        val message: String? = null
)

data class CommonOld(
        val totalRecordsFound: Int? = null,
        val showad: String? = null,
        val totalpages: Int? = null
)


data class DataItem(
        val jobid: String? = null,
        val standout: String? = null,
        val jobTitle: String? = null,
        val companyName: String? = null,
        val publishDate: String? = null,
        val lantype: String? = null,
        val logo: String? = null,
        val eduRec: String? = null,
        val deadline: String? = null,
        val experience: String? = null
)





