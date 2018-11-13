package com.bdjobs.app.Jobs


data class User(val name: String, val address: String)
data class ItemDrafted(val job_title: String,
                       val job_status: String,
                       val applications: String,
                       val view_status: String,
                       val date_posted_on: String,
                       val deadline: String,
                       val assessment_status: String)

// JobList Data classes started

data class GetResponseJobLIst(
        val statuscode: String? = null,
        val data: List<DataItem>? = null,
        val common: CommonOld? = null,
        val message: String? = null
)


data class GetJobDetailModel(
        val statuscode: String?,
        val message: String?,
        val data: List<DataJobDetail?>?,
        val common: Any?
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

data class DataJobDetail(

        val jobId: String?,
        val jobFound: String?,
        val compnayName: String?,
        val jobTitle: String?,
        val postedOn: String?,
        val deadline: String?,
        val jobVacancies: String?,
        val jobDescription: String?,
        val jobNature: String?,
        val educationRequirements: String?,
        val publications: String?,
        val age: String?,
        val experience: String?,
        val gender: String?,
        val additionJobRequirements: String?,
        val jobLocation: String?,
        val onlineApply: String?,
        val companyBusiness: String?,
        val companyAddress: String?,
        val companyHideAddress: String?,
        val companyWeb: String?,
        val jobAppliedEmail: String?,
        val jobSource: String?,
        val jobOtherBenifits: String?,
        val jobSalaryRange: String?,
        val jobSalaryRangeText: String?,
        val overseasnote: String?,
        val jobAdType: String?,
        val jobLOgoName: String?,
        val jobKeyPoints: String?,
        val applyInstruction: String?,
        val userID: String?,
        val photograph: String?,
        val photographMsg: String?,
        val jObIMage: String?,
        val upcoming: String?,
        val upcomingln: String?,
        val companyOtherJ0bs: String?,
        val companyID: String?,
        val companyNameENG: String?,
        val assessmentRequired: String?,
        val context: String?,
        val error: String?
)

data class CommonOld(
        val totalRecordsFound: Int? = null,
        val showad: String? = null,
        val totalpages: Int? = null
)


data class JobDetailItem (
        var jobid: String? = null,
        var lantype: String? = null

)

/*@Parcelize
data class JobDetailItem (
        var jobid: String? = null,
        var lantype: String? = null

):Parcelable*/

// JobList Data classes ends
