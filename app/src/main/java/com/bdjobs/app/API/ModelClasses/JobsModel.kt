package com.bdjobs.app.API.ModelClasses

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class JobDetailJsonModel(
        @SerializedName("statuscode") val statuscode: String?,
        @SerializedName("message") val message: String?,
        @SerializedName("data") val data: List<JobDetailData?>?,
        @SerializedName("common") val common: Any?
)

@Keep
data class TestJsonModel(
        @SerializedName("statuscode") val statuscode: String?,
        @SerializedName("message") val message: String?,
        @SerializedName("data") val data: List<String?>?,
        @SerializedName("common") val common: Any?
)

@Keep
data class JobDetailData(
        @SerializedName("DeadlineDB") val DeadlineDB: String?,
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
        @SerializedName("CompanyOtherJ0bs") val companyOtherJ0bs: String? = "0",
        @SerializedName("CompanyID") val companyID: String?,
        @SerializedName("CompanyNameENG") val companyNameENG: String?,
        @SerializedName("AssessmentRequired") val assessmentRequired: String?,
        @SerializedName("Context") val context: String?,
        @SerializedName("error") val error: String?,
        @SerializedName("JobSalaryMinSalary") val minSalary: String?,
        @SerializedName("JobSalaryMaxSalary") val maxSalary: String?,
        @SerializedName("JobWorkPlace") val jobWorkPlace: String?
)

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Keep
data class JobListModel(
        @SerializedName("statuscode") val statuscode: String?,
        @SerializedName("message") val message: String?,
        @SerializedName("data") val data: List<JobListModelData>?,
        @SerializedName("common") val common: JobListCommon?
)

@Keep
data class JobListModelData(
    @SerializedName("jobid") val jobid: String? = "",
    @SerializedName("jobTitle") val jobTitle: String?= "",
    @SerializedName("companyName") val companyName: String?= "",
    @SerializedName("deadline") val deadline: String?= "",
    @SerializedName("publishDate") val publishDate: String?= "",
    @SerializedName("eduRec") val eduRec: String?= "",
    @SerializedName("experience") val experience: String?= "",
    @SerializedName("standout") val standout: String?= "",
    @SerializedName("logo") val logo: String?= "",
    @SerializedName("lantype") val lantype: String?= "",
    @SerializedName("deadlineDB") val deadlineDB: String?= ""
)

@Keep
data class JobListCommon(
    @SerializedName("total_records_found") val totalRecordsFound: Int?=0,
    @SerializedName("showad") val showad: String?,
    @SerializedName("totalpages") val totalpages: Int?=0,
    @SerializedName("appliedid") val appliedid: List<String?>?,
    @SerializedName("totalcount") val totalcount: String?
)