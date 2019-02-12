package com.bdjobs.app.API

import com.bdjobs.app.API.ModelClasses.*
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.Constants.Companion.api_mybdjobs_app_agent_log
import com.bdjobs.app.Utilities.Constants.Companion.api_mybdjobs_app_favouritejob_count
import com.bdjobs.app.Utilities.Constants.Companion.api_mybdjobs_app_signinprocess
import com.bdjobs.app.Utilities.Constants.Companion.api_mybdjobs_app_social_agent_log
import com.bdjobs.app.editResume.adapters.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiServiceMyBdjobs {
    @FormUrlEncoded
    @POST(api_mybdjobs_app_signinprocess)
    fun getLoginUserDetails(@Field("username") username: String?): Call<LoginUserModel>

    @FormUrlEncoded
    @POST(api_mybdjobs_app_agent_log)
    fun doLogin(@Field("username") username: String? = "",
                @Field("password") password: String? = "",
                @Field("userId") userId: String? = "",
                @Field("decodId") decodId: String? = "",
                @Field("susername") susername: String? = "",
                @Field("fullName") fullName: String? = "",
                @Field("socialMediaId") socialMediaId: String? = "",
                @Field("socialMediaName") socialMediaName: String? = "",
                @Field("isMap") isMap: String? = "",
                @Field("email") email: String? = "",
                @Field("otpCode") otpCode: String? = "",
                @Field("smId") smId: String? = ""
    ): Call<LoginSessionModel>

    @FormUrlEncoded
    @POST(api_mybdjobs_app_social_agent_log)
    fun getSocialAccountList(
            @Field("SocialMediaId") SocialMediaId: String?,
            @Field("email") email: String? = "",
            @Field("MediaName") MediaName: String? = "",
            @Field("Version") Version: String? = "",
            @Field("SystemName") SystemName: String? = "com.bdjobs.app"

    ): Call<SocialLoginAccountListModel>

    @FormUrlEncoded
    @POST(api_mybdjobs_app_favouritejob_count)
    fun getFavFilterCount(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?,
            @Field("intFId") intFId: String?
    ): Call<FavouriteSearchCountModel>


    @FormUrlEncoded
    @POST("apps_step_03_view_exp.asp")
    fun getExpsList(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?
    ): Call<GetExps>

    @FormUrlEncoded
    @POST("apps_step_03_view_rai.asp")
    fun getArmyExpsList(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?
    ): Call<GetArmyEmpHis>

    @FormUrlEncoded
    @POST("apps_step_02_view_aca.asp")
    fun getAcaInfoList(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?
    ): Call<GetAcademicInfo>

    @FormUrlEncoded
    @POST("apps_step_02_view_tr.asp")
    fun getTrainingInfoList(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?
    ): Call<GetTrainingInfo>

    @FormUrlEncoded
    @POST("apps_step_01_view_jclo.asp")
    fun getPreferredAreaInfo(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?
    ): Call<GetPreferredAreas>

    @FormUrlEncoded
    @POST("apps_step_02_view_prq.asp")
    fun getProfessionalInfoList(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?
    ): Call<ProfessionalModel>

    @FormUrlEncoded
    @POST("apps_step_04_view_lang.asp")
    fun getLanguageInfoList(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?
    ): Call<LanguageModel>

    @FormUrlEncoded
    @POST("apps_step_04_view_spe.asp")
    fun getSpecializationInfo(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?
    ): Call<SpecialzationModel>


    @FormUrlEncoded
    @POST("apps_step_04_view_ref.asp")
    fun getReferenceInfoList(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?
    ): Call<ReferenceModel>

    @FormUrlEncoded
    @POST("apps_step_01_view_per.asp")
    fun getPersonalInfo(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?
    ): Call<GetPersInfo>

    @FormUrlEncoded
    @POST("apps_step_01_view_con.asp")
    fun getContactInfo(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?
    ): Call<GetContactInfo>

    @FormUrlEncoded
    @POST("apps_step_01_view_ori.asp")
    fun getORIInfo(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?
    ): Call<GetORIResponse>

    @FormUrlEncoded
    @POST("apps_step_01_view_cai.asp")
    fun getCareerInfo(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?
    ): Call<GetCarrerInfo>

    @FormUrlEncoded
    @POST("apps_step_01_update_cai.asp")
    fun updateCareerData(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?,
            @Field("isResumeUpdate") isResumeUpdate: String?,
            @Field("objective") objective: String?,
            @Field("presentSalary") presentSalary: String?,
            @Field("expSalary") expSalary: String?,
            @Field("lookingFor") lookingFor: String?,
            @Field("availableFor") availableFor: String?
    ): Call<AddorUpdateModel>

    @FormUrlEncoded
    @POST("apps_step_01_update_ori.asp")
    fun updateORIData(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?,
            @Field("isResumeUpdate") isResumeUpdate: String?,
            @Field("careerSummary") objective: String?,
            @Field("specialQual") presentSalary: String?,
            @Field("keywords") expSalary: String?
    ): Call<AddorUpdateModel>

    @FormUrlEncoded
    @POST("apps_step_01_update_con.asp")
    fun updateContactData(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?,
            @Field("isResumeUpdate") isResumeUpdate: String?,
            @Field("presentInsideOutsideBD") inOut: String?,
            @Field("present_district") present_district: String?,
            @Field("present_thana") present_thana: String?,
            @Field("present_p_office") present_p_office: String?,
            @Field("present_Village") present_Village: String?,
            @Field("present_country_list") present_country_list: String?,
            @Field("permanentInsideOutsideBD") permInOut: String?,
            @Field("permanent_district") permanent_district: String?,
            @Field("permanent_thana") permanent_thana: String?,
            @Field("permanent_p_office") permanent_p_office: String?,
            @Field("permanent_Village") permanent_Village: String?,
            @Field("permanent_country_list") permanent_country_list: String?,
            @Field("same_address") same_address: String?,
            @Field("permanent_adrsID") permanent_adrsID: String?,
            @Field("present_adrsID") present_adrsID: String?,
            @Field("homePhone") homePhone: String?,
            @Field("mobile") mobile: String?,
            @Field("officePhone") officePhone: String?,
            @Field("email") email: String?,
            @Field("alternativeEmail") alternativeEmail: String?
    ): Call<AddorUpdateModel>


    @FormUrlEncoded
    @POST("apps_step_01_update_per.asp")
    fun updatePersonalData(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?,
            @Field("isResumeUpdate") isResumeUpdate: String?,
            @Field("firstName") firstName: String?,
            @Field("lastName") lastName: String?,
            @Field("father") father: String?,
            @Field("mother") mother: String?,
            @Field("birthday") birthday: String?,
            @Field("nationality") nationality: String?,
            @Field("marital") marital: String?,
            @Field("gender") gender: String?,
            @Field("nationalId") nationalId: String?,
            @Field("Religion") Religion: String?
    ): Call<AddorUpdateModel>

    @FormUrlEncoded
    @POST("apps_step_02_update_aca.asp")
    fun updateAcademicData(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?,
            @Field("isResumeUpdate") isResumeUpdate: String?,
            @Field("levelOfEducation") levelOfEducation: String?,
            @Field("examDegreeTitle") examDegreeTitle: String?,
            @Field("institute") institute: String?,
            @Field("yearOfPassing") yearOfPassing: String?,
            @Field("concentration") concentration: String?,
            @Field("hid") hid: String?,
            @Field("foreignInstiture") foreignInstiture: String?,
            @Field("showDegree") showDegree: String?,
            @Field("result") result: String?,
            @Field("CGPA") CGPA: String?,
            @Field("grade") grade: String?,
            @Field("duration") duration: String?,
            @Field("achievements") achievements: String?,
            @Field("hEd_id") hEd_id: String?,
            @Field("chkResult") chkResult: String?
    ): Call<AddorUpdateModel>

    @FormUrlEncoded
    @POST("apps_step_03_update_exp.asp")
    fun updateExpsList(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?,
            @Field("companyName") companyName: String?,
            @Field("compBusiness") compBusiness: String?,
            @Field("compLocation") compLocation: String?,
            @Field("positionHeld") positionHeld: String?,
            @Field("department") department: String?,
            @Field("responsibility") responsibility: String?,
            @Field("from") from: String?,
            @Field("to") to: String?,
            @Field("currentlyWorking") currentlyWorking: String?,
            @Field("areaOfExpertise") areaOfExpertise: String?,
            @Field("hEx_id") hEx_id: String?,
            @Field("hId") hId: String?
    ): Call<AddorUpdateModel>

    @FormUrlEncoded
    @POST("apps_step_03_update_rai.asp")
    fun updateArmyExpsList(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?,
            @Field("isResumeUpdate") isResumeUpdate: String?,
            @Field("comboBANo") comboBANo: String?,
            @Field("txtBANo") txtBANo: String?,
            @Field("comboArms") comboArms: String?,
            @Field("comboRank") comboRank: String?,
            @Field("comboType") comboType: String?,
            @Field("txtCourse") txtCourse: String?,
            @Field("txtTrade") txtTrade: String?,
            @Field("cboCommissionDate") cboCommissionDate: String?,
            @Field("cboRetirementDate") cboRetirementDate: String?,
            @Field("arm_id") arm_id: String?,
            @Field("hId") hId: String?
    ): Call<AddorUpdateModel>

    @FormUrlEncoded
    @POST("apps_step_02_update_tr.asp")
    fun updateTrainingList(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?,
            @Field("isResumeUpdate") isResumeUpdate: String?,
            @Field("trainingTitle") trainingTitle: String?,
            @Field("institute") institute: String?,
            @Field("country") country: String?,
            @Field("year") year: String?,
            @Field("duration") duration: String?,
            @Field("hEd_id") hEd_id: String?,
            @Field("topicCovered") topicCovered: String?,
            @Field("location") location: String?,
            @Field("trainingId") trainingId: String?
    ): Call<AddorUpdateModel>

    @FormUrlEncoded
    @POST("apps_step_02_update_prq.asp")
    fun updatePQualificationList(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?,
            @Field("certification") certification: String?,
            @Field("institute") institute: String?,
            @Field("from") from: String?,
            @Field("to") to: String?,
            @Field("hId") hId: String?,
            @Field("isResumeUpdate") isResumeUpdate: String?,
            @Field("location") location: String?,
            @Field("hp_id") hp_id: String?

    ): Call<ProfessionalModel>

    @FormUrlEncoded
    @POST("apps_step_04_update_lang.asp")
    fun updateLanguageList(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?,
            @Field("language") language: String?,
            @Field("reading") reading: String?,
            @Field("isResumeUpdate") isResumeUpdate: String?,
            @Field("writing") writing: String?,
            @Field("speaking") speaking: String?,
            @Field("h_ID") h_ID: String?

    ): Call<AddorUpdateModel>

    @FormUrlEncoded
    @POST("apps_step_04_update_spe.asp")
    fun updateSpecialization(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?,
            @Field("isResumeUpdate") isResumeUpdate: String?,
            @Field("skills") skills: String?,
            @Field("skillDescription") skillDescription: String?,
            @Field("extracurricular") extracurricular: String?
    ): Call<AddorUpdateModel>

    @FormUrlEncoded
    @POST("apps_step_04_update_ref.asp")
    fun updateReferenceList(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?,
            @Field("name") name: String?,
            @Field("organization") organization: String?,
            @Field("designation") designation: String?,
            @Field("hId") hId: String?,
            @Field("isResumeUpdate") isResumeUpdate: String?,
            @Field("address") h_ID: String?,
            @Field("phone_off") phone_off: String?,
            @Field("phone_res") phone_res: String?,
            @Field("mobile") mobile: String?,
            @Field("email") email: String?,
            @Field("relation") relation: String?,
            @Field("hr_id") hr_id: String?

    ): Call<AddorUpdateModel>

    @FormUrlEncoded
    @POST("apps_delete.asp")
    fun deleteData(
            @Field("itemName") itemName: String,
            @Field("id") id: String,
            @Field("isResumeUpdate") isUpdate: String,
            @Field("userId") userId: String,
            @Field("decodeId") decodeId: String): Call<AddorUpdateModel>


    @FormUrlEncoded
    @POST("app_delfavouritejobs.asp")
    fun deleteFavSearch(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?,
            @Field("intSfID") intSfID: String?
    ): Call<FavouriteSearchCountModel>

    @FormUrlEncoded
    @POST("app_invite_interview_list.asp")
    fun getJobInvitationList(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?
    ): Call<JobInvitationListModel>


    @FormUrlEncoded
    @POST("assessment/apps_smnt_certification_complete_examlist.asp")
    fun getAssesmentCompleteList(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?
    ): Call<AssesmentCompleteModel>


    @FormUrlEncoded
    @POST("apps_create_account.asp")
    fun createAccount(
            @Field("firstName") firstName: String? = "",
            @Field("lastName") lastName: String? = "",
            @Field("gender") gender: String? = "",
            @Field("email") email: String? = "",
            @Field("username") username: String? = "",
            @Field("password") password: String? = "",
            @Field("confirm") confirm: String? = "",
            @Field("mobile") mobile: String? = "",
            @Field("smid") smid: String? = "",
            @Field("isSMLogin") isSMLogin: String? = "",
            @Field("categoryType") categoryType: String? = "",
            @Field("userNameType") userNameType: String? = "",
            @Field("smediatype") smediatype: String? = "",
            @Field("prefCat") prefCat: String? = "",
            @Field("txtCountryCode") txtCountryCode: String? = "",
            @Field("packageName") packageName: String? = "",
            @Field("packageNameVersion") packageNameVersion: String? = ""

    ): Call<CreateAccountModel>


    @FormUrlEncoded
    @POST("apps_otpVerify.asp")
    fun sendOtpToVerify(
            @Field("tmpUserId") tmpUserId: String? = "",
            @Field("optCode") optCode: String? = "",
            @Field("packageName") packageName: String? = "",
            @Field("packageNameVersion") packageNameVersion: String? = ""


    ): Call<CreateAccountModel>


    @FormUrlEncoded
    @POST("apps_updateBlueCv.asp")
    fun sendBlueCollarUserInfo(
            @Field("UserID") userid: String = "",
            @Field("decodeId") decodeId: String = "",
            @Field("village") village: String = "",
            @Field("locationID") locationID: String = "",
            @Field("dob") dob: String = "",
            @Field("expYear") expYear: String = "",
            @Field("skillID") skillID: String = "",
            @Field("age") age: String = "",
            @Field("userName") userName: String = "",
            @Field("levelOfEducation") levelOfEducation: String = "",
            @Field("institute") institute: String = "",
            @Field("inlineRadioOptions") inlineRadioOptions: String = "",
            @Field("examDegreeTitle") examDegreeTitle: String = "",
            @Field("yearOfPassing") yearOfPassing: String = "",
            @Field("hasEdu") hasEdu: String = ""
    ): Call<UpdateBlueCvModel>


    @FormUrlEncoded
    @POST("apps_resendOtp.asp")
    fun resendOtp(
            @Field("userId") tmpUserId: String? = "",
            @Field("mobile") mobile: String? = "",
            @Field("forCreateAccount") forCreateAccount: String? = ""

    ): Call<ResendOtpModel>


    @FormUrlEncoded
    @POST("apps_photoInfo.asp")
    fun getPhotoInfo(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = ""


    ): Call<PhotoInfoModel>


    @FormUrlEncoded
    @POST("apps_delCart.asp")
    fun unShortlistJob(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("JobID") strJobId: String? = ""
    ): Call<UnshorlistJobModel>


    @FormUrlEncoded
    @POST("app_set_cookies.asp")
    fun getCookies(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = ""
    ): Call<CookieModel>


    @FormUrlEncoded
    @POST("apps_view_stats.asp")
    fun mybdjobStats(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?,
            @Field("isActivityDate") isActivityDate: String?,
            @Field("trainingId") trainingId: String?,
            @Field("isResumeUpdate") isResumeUpdate: String?,
            @Field("packageName") packageName: String? = "",
            @Field("packageNameVersion") packageNameVersion: String? = ""

    ): Call<StatsModelClass>

    @FormUrlEncoded
    @POST("apps_apply_position.asp")
    fun getAppliedJobs(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?,
            @Field("isActivityDate") isActivityDate: String?,
            @Field("pageNumber") pageNumber: String?,
            @Field("itemsPerPage") itemsPerPage: String?
    ): Call<AppliedJobModel>


    @FormUrlEncoded
    @POST("apps_LastUpdate.asp")
    fun getInterviewInvitation(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?,
            @Field("packageName") packageName: String? = "",
            @Field("packageNameVersion") packageNameVersion: String? = ""
    ): Call<InterviewInvitation>


    @FormUrlEncoded
    @POST("app_logout.asp")
    fun logout(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = ""
    ): Call<CookieModel>


    @FormUrlEncoded
    @POST("upload_img.aspx")
    fun DeletePhoto(
            @Field("folderName") folderName: String,
            @Field("folderId") folderId: String,
            @Field("imageName") imageName: String,
            @Field("isResumeUpdate") isResumeUpdate: String,
            @Field("status") status: String,
            @Field("userid") userid: String,
            @Field("decodeid") decodeid: String): Call<PhotoInfoModel>


    @FormUrlEncoded
    @POST("apps_resume_view.asp")
    fun getEmpVwdMyResume(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?,
            @Field("pageNumber") pageNumber: String?,
            @Field("itemsPerPage") itemsPerPage: String?,
            @Field("isActivityDate") isActivityDate: String?,
            @Field("AppsDate") AppsDate: String?
    ): Call<EmpVwdResume>

    @FormUrlEncoded
    @POST("app_invite_interview_details.asp")
    fun getIterviewInvitationDetails(
            @Field("userId") userID: String,
            @Field("decodeId") decodeID: String,
            @Field("jobId") jobId: String
    ): Call<InvitationDetailModels>


    @FormUrlEncoded
    @POST("app_invite_interview_confirmation.asp")
    fun sendInterviewConfirmation(
            @Field("userId") userID: String,
            @Field("decodeId") decodeID: String,
            @Field("applyId") applyId: String,
            @Field("activity") activity: String,
            @Field("cancleReason") cancleReason: String = "",
            @Field("otherComment") otherComment: String = "",
            @Field("invitationId") invitationId: String,
            @Field("rescheduleComment") rescheduleComment: String = ""
    ): Call<InvitationDetailModels>

    @FormUrlEncoded
    @POST("app_invite_interview_company_ratting.asp")
    fun sendCompanyRating(
            @Field("userId") userID: String,
            @Field("decodeId") decodeID: String,
            @Field("jobId") jobId: String,
            @Field("ratting") ratting: String,
            @Field("rattingComment") rattingComment: String
    ): Call<InvitationDetailModels>

    @FormUrlEncoded
    @POST("apps_salary_edit.asp")
    fun getUpdateSalaryMsg(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?,
            @Field("JobId") JobId: String?,
            @Field("txtExpectedSalary") txtExpectedSalary: String?
    ): Call<AppliedJobsSalaryEdit>


    @FormUrlEncoded
    @POST("apps_cancelApply.asp")
    fun getAppliedCancelMsg(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?,
            @Field("JobId") JobId: String?
    ): Call<CancelAppliedJobs>

    @FormUrlEncoded
    @POST("apps_update_application_status.asp")
    fun getEmpInteraction(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?,
            @Field("status") status: String?,
            @Field("experienceId") experienceId: String?,
            @Field("changeExprience") changeExprience: String?,
            @Field("JobId") JobId: String?
    ): Call<EmployerInteraction>


    @FormUrlEncoded
    @POST("apps_update_account.asp")
    fun getChangePassword(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?,
            @Field("userName") userName: String?,
            @Field("isResumeUpdate") isResumeUpdate: String?,
            @Field("OldPass") OldPass: String?,
            @Field("NewPass") NewPass: String?,
            @Field("ConfirmPass") ConfirmPass: String?,
            @Field("isSmMedia") isSmMedia: String?,
            @Field("packageName") packageName: String?,
            @Field("packageNameVersion") packageNameVersion: String?
    ): Call<ChangePassword>

    @FormUrlEncoded
    @POST("apps_prc_home.asp")
    fun getInviteCodeUserOwnerInfo(
            @Field("userID") userID: String?,
            @Field("decodeID") decodeID: String?,
            @Field("mobileNumber") mobileNumber: String?,
            @Field("catId") catId: String?,
            @Field("deviceID") deviceID: String?
    ): Call<InviteCodeHomeModel>

    @FormUrlEncoded
    @POST("apps_invite_code.asp")
    fun getOwnerInviteCode(
            @Field("userID") userID: String?,
            @Field("decodeID") decodeID: String?,
            @Field("ownerID") ownerID: String?
    ): Call<OwnerInviteCodeModel>

    @FormUrlEncoded
    @POST("apps_category_amount.asp")
    fun getCategoryAmount(
            @Field("userID") userID: String?,
            @Field("decodeID") decodeID: String?,
            @Field("pcOwnerId") pcOwnerId: String? = ""
    ): Call<InviteCodeCategoryAmountModel>


    @FormUrlEncoded
    @POST("apps_get_invite_list.asp")
    fun getOwnerInviteList(
            @Field("userID") userID: String?,
            @Field("decodeID") decodeID: String?,
            @Field("pcOwnerId") pcOwnerId: String?,
            @Field("verify_status") verify_status: String?
    ): Call<OwnerInviteListModel>

    @FormUrlEncoded
    @POST("apps_prc_balance_report.asp")
    fun getOwnerStatement(
            @Field("userID") userID: String?,
            @Field("decodeID") decodeID: String?,
            @Field("pcOwnerId") pcOwnerId: String?
    ): Call<InviteCodeOwnerStatementModel>


    @FormUrlEncoded
    @POST("apps_invited_user_status.asp")
    fun getInviteCodeUserStatus(
            @Field("userID") userID: String?,
            @Field("decodeID") decodeID: String?,
            @Field("invited_user_id") invited_user_id: String?
    ): Call<InviteCodeUserStatusModel>

    @FormUrlEncoded
    @POST("apps_get_balance.asp")
    fun getOwnerBalance(
            @Field("userID") userID: String?,
            @Field("decodeID") decodeID: String?,
            @Field("pcOwnerId") pcOwnerId: String?
    ): Call<InviteCodeBalanceModel>


    @FormUrlEncoded
    @POST("apps_get_payment_method.asp")
    fun getPaymentMethod(
            @Field("userID") userID: String?,
            @Field("decodeID") decodeID: String?,
            @Field("userType") userType: String?
    ): Call<InviteCodePaymentMethodModel>


    @FormUrlEncoded
    @POST("apps_payment_system_insert.asp")
    fun insertPaymentMethod(
            @Field("userID") userID: String?,
            @Field("decodeID") decodeID: String?,
            @Field("userType") userType: String?,
            @Field("paymentType") paymentType: String?,
            @Field("accountNo") accountNo: String?
    ): Call<PaymentTypeInsertModel>

    @FormUrlEncoded
    @POST("apps_prc_code_submit.asp")
    fun insertInviteCode(
            @Field("userID") userID: String?,
            @Field("decodeID") decodeID: String?,
            @Field("mobileNumber") mobileNumber: String?,
            @Field("catId") catId: String?,
            @Field("deviceID") deviceID: String?,
            @Field("promoCode") promoCode: String?
    ): Call<PaymentTypeInsertModel>

    @FormUrlEncoded
    @POST("apps_prc_verify_user.asp")
    fun inviteCodeUserVerify(
            @Field("userID") userID: String?,
            @Field("decodeID") decodeID: String?,
            @Field("mobileNumber") mobileNumber: String?,
            @Field("catId") catId: String?,
            @Field("deviceID") deviceID: String?
    ): Call<InviteCodeUserVerifyModel>

    @FormUrlEncoded
    @POST("apps_EmailCv.asp")
    fun getEmailResumeMsg(
            @Field("userID") userID: String?,
            @Field("decodeID") decodeID: String?,
            @Field("isResumeUpdate") isResumeUpdate: String?,
            @Field("userEmail") userEmail: String?,
            @Field("companyEmail") companyEmail: String?,
            @Field("mailSubject") mailSubject: String?,
            @Field("application") application: String?,
            @Field("fullName") fullName: String?,
            @Field("uploadedCv") uploadedCv: String?,
            @Field("Jobid") Jobid: String?
    ): Call<EmailResume>


    @FormUrlEncoded
    @POST("apps_file_upload_info.asp")
    fun getCvFileAvailable(
            @Field("userID") userID: String?,
            @Field("decodeID") decodeID: String?

    ): Call<FileInfo>

    @Multipart
    @POST("file_upload.aspx")
     fun UploadCV(
            @PartMap partMap: Map<String, @JvmSuppressWildcards RequestBody>?,
            @Part file: MultipartBody.Part?
    ): Call<UploadResume>

    @Multipart
    @POST("file_upload.aspx")
     fun UploadCV2(
            @PartMap partMap: Map<String, @JvmSuppressWildcards RequestBody>,
            @Part file: MultipartBody.Part): Call<ADDorUpdateModel>

    @FormUrlEncoded
    @POST("app_training.asp")
    fun getTrainingList(
            @Field("userID") userID: String?,
            @Field("decodeID") decodeID: String?,
            @Field("traingId") traingId: String?,
            @Field("AppsDate") AppsDate: String?

    ): Call<TrainingList>



    companion object Factory {

        fun create(): ApiServiceMyBdjobs {
            val retrofit = Retrofit.Builder()
                    .baseUrl(Constants.baseUrlMyBdjobs)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            return retrofit.create(ApiServiceMyBdjobs::class.java)
        }
    }

}