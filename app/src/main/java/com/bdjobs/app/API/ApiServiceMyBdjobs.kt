package com.bdjobs.app.API

import com.bdjobs.app.API.ModelClasses.*
import com.bdjobs.app.BuildConfig
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.Constants.Companion.api_mybdjobs_app_agent_log
import com.bdjobs.app.Utilities.Constants.Companion.api_mybdjobs_app_favouritejob_count
import com.bdjobs.app.Utilities.Constants.Companion.api_mybdjobs_app_signinprocess
import com.bdjobs.app.Utilities.Constants.Companion.api_mybdjobs_app_social_agent_log
import com.bdjobs.app.editResume.adapters.models.*
import com.bdjobs.app.liveInterview.data.models.*
import com.bdjobs.app.resume_dashboard.data.models.ManageResumeDetailsStat
import com.bdjobs.app.resume_dashboard.data.models.ManageResumeStats
import com.bdjobs.app.resume_dashboard.data.models.PersonalizedResumeStat
import com.bdjobs.app.resume_dashboard.data.models.ResumePrivacyStatus
import com.bdjobs.app.sms.data.model.PaymentInfoAfterGateway
import com.bdjobs.app.sms.data.model.PaymentInfoBeforeGateway
import com.bdjobs.app.transaction.data.model.TransactionList
import com.bdjobs.app.sms.data.model.SMSSettings
import com.bdjobs.app.videoInterview.data.models.CommonResponse
import com.bdjobs.app.videoInterview.data.models.InterviewFeedback
import com.bdjobs.app.videoInterview.data.models.VideoInterviewDetails
import com.bdjobs.app.videoInterview.data.models.VideoInterviewList
import com.google.gson.GsonBuilder
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

interface ApiServiceMyBdjobs {

    @FormUrlEncoded
    @POST(api_mybdjobs_app_signinprocess)
    fun getLoginUserDetails(@Field("username") username: String? = "",
                            @Field("appId") appId: String? = Constants.APP_ID

    ): Call<LoginUserModel>

    @FormUrlEncoded
    @POST("https://my.bdjobs.com/apps/mybdjobs/v1/apps-own-signinprocess.asp")
    fun getLogin2UserDetails(@Field("username") username: String? = "",
                             @Field("appId") appId: String? = Constants.APP_ID): Call<Login2UserModel>

    @FormUrlEncoded
    @POST("https://my.bdjobs.com/apps/mybdjobs/v1/apps-own-agen-log.asp")
    fun doLogin2(@Field("username") username: String? = "",
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
                 @Field("smId") smId: String? = "",
                 @Field("appId") appId: String? = Constants.APP_ID
    ): Call<LoginSessionModel>

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
                @Field("smId") smId: String? = "",
                @Field("appId") appId: String? = Constants.APP_ID
    ): Call<LoginSessionModel>

    @FormUrlEncoded
    @POST(api_mybdjobs_app_social_agent_log)
    fun getSocialAccountList(
            @Field("SocialMediaId") SocialMediaId: String? = "",
            @Field("email") email: String? = "",
            @Field("MediaName") MediaName: String? = "",
            @Field("Version") Version: String? = "",
            @Field("SystemName") SystemName: String? = "com.bdjobs.app",
            @Field("appId") appId: String? = Constants.APP_ID

    ): Call<SocialLoginAccountListModel>

    @FormUrlEncoded
    @POST(api_mybdjobs_app_favouritejob_count)
    fun getFavFilterCount(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("intFId") intFId: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<FavouriteSearchCountModel>

    @FormUrlEncoded
    @POST("apps_subscribe_sms_favourite_search.asp")
    fun subscribeOrUnsubscribeSMSFromFavouriteSearch(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("savefilterid") filterId: String? = "",
            @Field("filterName") filterName: String? = "",
            @Field("action") action: Int?,
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<SMSSubscribeModel>

    @FormUrlEncoded
    @POST("apps_step_03_view_exp.asp")
    fun getExpsList(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<GetExps>

    @FormUrlEncoded
    @POST("apps_step_03_view_rai.asp")
    fun getArmyExpsList(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<GetArmyEmpHis>

    @FormUrlEncoded
    @POST("apps_step_02_view_aca.asp")
//    @POST("apps_step_02_view_aca_testp.asp")

    fun getAcaInfoList(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<GetAcademicInfo>

    @FormUrlEncoded
    @POST("apps_step_02_view_tr.asp")
    fun getTrainingInfoList(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<GetTrainingInfo>

    @FormUrlEncoded
    @POST("apps_step_01_view_jclo.asp")
    fun getPreferredAreaInfo(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<GetPreferredAreas>

    @FormUrlEncoded
    @POST("apps_step_02_view_prq.asp")
    fun getProfessionalInfoList(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<ProfessionalModel>

    @FormUrlEncoded
    @POST("apps_step_04_view_lang.asp")
    fun getLanguageInfoList(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<LanguageModel>


    ////////////PreviousOne

    /*  @FormUrlEncoded
      @POST("apps_step_04_view_spe.asp")
      fun getSpecializationInfo(
              @Field("userId") userId: String? = "",
              @Field("decodeId") decodeId: String? = "",
              @Field("appId") appId: String? = Constants.APP_ID
      ): Call<SpecialzationModel>*/


    ///////////////////New Test One

    @FormUrlEncoded
    @POST("apps_step_04_view_spe_newP.asp")
    fun getSpecializationInfo(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<SpecialzationModel>


    @FormUrlEncoded
    @POST("apps_step_04_view_ref.asp")
    fun getReferenceInfoList(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<ReferenceModel>

    @FormUrlEncoded
    @POST("apps_step_01_view_per.asp")
    fun getPersonalInfo(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<GetPersInfo>

    @FormUrlEncoded
//    @POST("apps_step_01_view_con.asp")
    @POST("apps_step_01_view_con_test_f.asp")
    fun getContactInfo(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<GetContactInfo>

    @FormUrlEncoded
    @POST("apps_step_01_view_ori.asp")
    fun getORIInfo(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<GetORIResponse>

    @FormUrlEncoded
    @POST("apps_step_01_view_cai.asp")
    fun getCareerInfo(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<GetCarrerInfo>

    @FormUrlEncoded
    @POST("apps_step_01_update_cai.asp")
    fun updateCareerData(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("isResumeUpdate") isResumeUpdate: String? = "",
            @Field("objective") objective: String? = "",
            @Field("presentSalary") presentSalary: String? = "",
            @Field("expSalary") expSalary: String? = "",
            @Field("lookingFor") lookingFor: String? = "",
            @Field("availableFor") availableFor: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<AddorUpdateModel>

    @FormUrlEncoded
    @POST("apps_step_01_update_ori.asp")
    fun updateORIData(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("isResumeUpdate") isResumeUpdate: String? = "",
            @Field("careerSummary") objective: String? = "",
            @Field("specialQual") presentSalary: String? = "",
            @Field("keywords") expSalary: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<AddorUpdateModel>

    @FormUrlEncoded
    @POST("apps_step_01_update_jclo.asp")
    fun updatePrefAreasData(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("isResumeUpdate") isResumeUpdate: String? = "",
            @Field("preferredCategory") preferredCategory: String? = "",
            @Field("selected_blue_Cat") selected_blue_Cat: String? = "",
            @Field("preferredLocationInside") preferredLocationInside: String? = "",
            @Field("prefereedLocatoinOutside") prefereedLocatoinOutside: String? = "",
            @Field("preferredOrganization") preferredOrganization: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<AddorUpdateModel>

    @FormUrlEncoded
    @POST("apps_step_01_update_con_test_f.asp")
//    @POST("apps_step_01_update_con.asp")
    fun updateContactData(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("isResumeUpdate") isResumeUpdate: String? = "",
            @Field("presentInsideOutsideBD") inOut: String? = "",
            @Field("present_district") present_district: String? = "",
            @Field("present_thana") present_thana: String? = "",
            @Field("present_p_office") present_p_office: String? = "",
            @Field("present_Village") present_Village: String? = "",
            @Field("present_country_list") present_country_list: String? = "",
            @Field("permanentInsideOutsideBD") permInOut: String? = "",
            @Field("permanent_district") permanent_district: String? = "",
            @Field("permanent_thana") permanent_thana: String? = "",
            @Field("permanent_p_office") permanent_p_office: String? = "",
            @Field("permanent_Village") permanent_Village: String? = "",
            @Field("permanent_country_list") permanent_country_list: String? = "",
            @Field("same_address") same_address: String? = "",
            @Field("permanent_adrsID") permanent_adrsID: String? = "",
            @Field("present_adrsID") present_adrsID: String? = "",
            @Field("homePhone") homePhone: String? = "",
            @Field("mobile") mobile: String? = "",
            @Field("countryCode") countryCode: String? = "",
            @Field("officePhone") officePhone: String? = "",
            @Field("email") email: String? = "",
            @Field("alternativeEmail") alternativeEmail: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<AddorUpdateModel>


    @FormUrlEncoded
    @POST("apps_step_01_update_per.asp")
    fun updatePersonalData(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("isResumeUpdate") isResumeUpdate: String? = "",
            @Field("firstName") firstName: String? = "",
            @Field("lastName") lastName: String? = "",
            @Field("father") father: String? = "",
            @Field("mother") mother: String? = "",
            @Field("birthday") birthday: String? = "",
            @Field("nationality") nationality: String? = "",
            @Field("marital") marital: String? = "",
            @Field("gender") gender: String? = "",
            @Field("nationalId") nationalId: String? = "",
            @Field("Religion") Religion: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<AddorUpdateModel>

    @FormUrlEncoded
    @POST("apps_step_02_update_aca.asp")
//    @POST("apps_step_02_update_aca_testp.asp")
    fun updateAcademicData(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("isResumeUpdate") isResumeUpdate: String? = "",
            @Field("levelOfEducation") levelOfEducation: String? = "",
            @Field("examDegreeTitle") examDegreeTitle: String? = "",
            @Field("institute") institute: String? = "",
            @Field("yearOfPassing") yearOfPassing: String? = "",
            @Field("concentration") concentration: String? = "",
            @Field("hid") hid: String? = "",
            @Field("foreignInstiture") foreignInstiture: String? = "",
            @Field("showDegree") showDegree: String? = "",
            @Field("result") result: String? = "",
            @Field("CGPA") CGPA: String? = "",
            @Field("grade") grade: String? = "",
            @Field("duration") duration: String? = "",
            @Field("achievements") achievements: String? = "",
            @Field("hEd_id") hEd_id: String? = "",
            @Field("chkResult") chkResult: String? = "",
            @Field("boardId") boardId: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<AddorUpdateModel>

    @FormUrlEncoded
    @POST("apps_step_03_update_exp.asp")
    fun updateExpsList(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("companyName") companyName: String? = "",
            @Field("compBusiness") compBusiness: String? = "",
            @Field("compLocation") compLocation: String? = "",
            @Field("positionHeld") positionHeld: String? = "",
            @Field("department") department: String? = "",
            @Field("responsibility") responsibility: String? = "",
            @Field("from") from: String? = "",
            @Field("to") to: String? = "",
            @Field("currentlyWorking") currentlyWorking: String? = "",
            @Field("areaOfExpertise") areaOfExpertise: String? = "",
            @Field("hEx_id") hEx_id: String? = "",
            @Field("hId") hId: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<AddorUpdateModel>

    /**
     * Param: 1 -> Preferred location (inside BD / outside BD)
     * Param: 2 -> Organisation
     * Param: 3 -> Work Area
     * Param: 4 -> Skill
     * Param: 5 -> Institution
     * Param: 6 -> Concentration/Major/Group
     */
    @GET("apps_auto_suggestion.asp")
    fun fetchAutoSuggestion(
            @Query("term") term: String? = "",
            @Query("param") param: String? = "",
            @Query("con") con: String? = "",
            @Query("ver") ver: String? = "EN/BN",
            @Query("examTitleVal") examTitleVal: String? = ""
    ): Call<AutoSuggestionModel>

    @FormUrlEncoded
    @POST("apps_skill_list.asp")
    fun workSkillSuggestionsBC(
            @Field("cat_id") categoryID: String? = ""
    ): Call<BCWorkSkillModel>

    @FormUrlEncoded
    @POST("apps_step_03_update_rai.asp")
    fun updateArmyExpsList(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("isResumeUpdate") isResumeUpdate: String? = "",
            @Field("comboBANo") comboBANo: String? = "",
            @Field("txtBANo") txtBANo: String? = "",
            @Field("comboArms") comboArms: String? = "",
            @Field("comboRank") comboRank: String? = "",
            @Field("comboType") comboType: String? = "",
            @Field("txtCourse") txtCourse: String? = "",
            @Field("txtTrade") txtTrade: String? = "",
            @Field("cboCommissionDate") cboCommissionDate: String? = "",
            @Field("cboRetirementDate") cboRetirementDate: String? = "",
            @Field("arm_id") arm_id: String? = "",
            @Field("hId") hId: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<AddorUpdateModel>

    @FormUrlEncoded
    @POST("apps_step_02_update_tr.asp")
    fun updateTrainingList(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("isResumeUpdate") isResumeUpdate: String? = "",
            @Field("trainingTitle") trainingTitle: String? = "",
            @Field("institute") institute: String? = "",
            @Field("country") country: String? = "",
            @Field("year") year: String? = "",
            @Field("duration") duration: String? = "",
            @Field("hEd_id") hEd_id: String? = "",
            @Field("topicCovered") topicCovered: String? = "",
            @Field("location") location: String? = "",
            @Field("trainingId") trainingId: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<AddorUpdateModel>

    @FormUrlEncoded
    @POST("apps_step_02_update_prq.asp")
    fun updatePQualificationList(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("certification") certification: String? = "",
            @Field("institute") institute: String? = "",
            @Field("from") from: String? = "",
            @Field("to") to: String? = "",
            @Field("hId") hId: String? = "",
            @Field("isResumeUpdate") isResumeUpdate: String? = "",
            @Field("location") location: String? = "",
            @Field("hp_id") hp_id: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID

    ): Call<ProfessionalModel>

    @FormUrlEncoded
    @POST("apps_step_04_update_lang.asp")
    fun updateLanguageList(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("language") language: String? = "",
            @Field("reading") reading: String? = "",
            @Field("isResumeUpdate") isResumeUpdate: String? = "",
            @Field("writing") writing: String? = "",
            @Field("speaking") speaking: String? = "",
            @Field("h_ID") h_ID: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID

    ): Call<AddorUpdateModel>

    @FormUrlEncoded
    @POST("apps_step_04_update_spe.asp")
    fun updateSpecialization(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("isResumeUpdate") isResumeUpdate: String? = "",
            @Field("skills") skills: String? = "",
            @Field("skillDescription") skillDescription: String? = "",
            @Field("extracurricular") extracurricular: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<AddorUpdateModel>


    ///-------------updateSpcializationTest


    @FormUrlEncoded
    @POST("apps_step_04_update_spe_newP.asp")
    fun updateSpecializationTest(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("isResumeUpdate") isResumeUpdate: String? = "",
            @Field("skills") skills: String? = "",
            @Field("skillDescription") skillDescription: String? = "",
            @Field("extracurricular") extracurricular: String? = "",
            @Field("skilledBy") skilledBy: String? = "",
            @Field("ntvqfLevel") ntvqfLevel: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<AddorUpdateModel>


    ///// new add-------------///////////

    @FormUrlEncoded
    @POST("apps_step_04_update_extra_activity.asp")
    fun updateExtraCuriculam(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("isResumeUpdate") isResumeUpdate: String? = "",
            @Field("skillDescription") skillDescription: String? = "",
            @Field("extracurricular") extracurricular: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<AddorUpdateModel>


    @FormUrlEncoded
    @POST("apps_step_04_update_spe_p.asp")
    fun specializationAddOrUpdate(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("isResumeUpdate") isResumeUpdate: String? = "",
            @Field("skills") skills: String? = "",
            @Field("s_id") s_id: String? = "",
            @Field("skilledBy") skilledBy: String? = "",
            @Field("ntvqfLevel") ntvqfLevel: String? = "",
            @Field("insertOrupdate") insertOrupdate: String? = ""
    ): Call<SpecializationAdModel>


    @FormUrlEncoded
    @POST("apps_step_04_update_ref.asp")
    fun updateReferenceList(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("name") name: String? = "",
            @Field("organization") organization: String? = "",
            @Field("designation") designation: String? = "",
            @Field("hId") hId: String? = "",
            @Field("isResumeUpdate") isResumeUpdate: String? = "",
            @Field("address") h_ID: String? = "",
            @Field("phone_off") phone_off: String? = "",
            @Field("phone_res") phone_res: String? = "",
            @Field("mobile") mobile: String? = "",
            @Field("email") email: String? = "",
            @Field("relation") relation: String? = "",
            @Field("hr_id") hr_id: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID

    ): Call<AddorUpdateModel>

    @FormUrlEncoded
    @POST("apps_delete.asp")
    fun deleteData(
            @Field("itemName") itemName: String? = "",
            @Field("id") id: String? = "",
            @Field("isResumeUpdate") isUpdate: String? = "",
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<AddorUpdateModel>


    @FormUrlEncoded
    @POST("app_delfavouritejobs.asp")
    fun deleteFavSearch(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("intSfID") intSfID: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<FavouriteSearchCountModel>

    @FormUrlEncoded
    @POST("app_invite_interview_list.asp")
    fun getJobInvitationList(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<JobInvitationListModel>

    @FormUrlEncoded
    @POST("app_video_interview_invitation_home.asp")
    fun getVideoInvitationList(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("pageNumber") pageNumber: String? = "1",
            @Field("itemsPerPage") itemsPerPage: String? = "100"
    ): Call<VideoInterviewList>

    @FormUrlEncoded
    @POST("app_live_interview_invitation_home.asp")
    fun getLiveInvitationListHome(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("isActivityDate") isActivityDate: String? = "0",
            @Field("pageNumber") pageNumber: String? = "1",
            @Field("itemsPerPage") itemsPerPage: String? = "100"
    ): Call<LiveInterviewList>

    @FormUrlEncoded
    @POST("assessment/apps_smnt_certification_complete_examlist.asp")
    fun getAssesmentCompleteList(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
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
            @Field("packageNameVersion") packageNameVersion: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID

    ): Call<CreateAccountModel>


    @FormUrlEncoded
    @POST("apps_otpVerify.asp")
    fun sendOtpToVerify(
            @Field("tmpUserId") tmpUserId: String? = "",
            @Field("optCode") optCode: String? = "",
            @Field("packageName") packageName: String? = "",
            @Field("packageNameVersion") packageNameVersion: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID


    ): Call<CreateAccountModel>


    @FormUrlEncoded
    @POST("apps_updateBlueCv.asp")
//    @POST("apps_updateBlueCv_testp.asp")
    fun sendBlueCollarUserInfo(
            @Field("UserID") userid: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("village") village: String? = "",
            @Field("locationID") locationID: String? = "",
            @Field("dob") dob: String = "",
            @Field("expYear") expYear: String? = "",
            @Field("skillID") skillID: String? = "",
            @Field("age") age: String = "",
            @Field("userName") userName: String? = "",
            @Field("levelOfEducation") levelOfEducation: String? = "",
            @Field("institute") institute: String? = "",
            @Field("inlineRadioOptions") inlineRadioOptions: String? = "",
            @Field("examDegreeTitle") examDegreeTitle: String? = "",
            @Field("yearOfPassing") yearOfPassing: String? = "",
            @Field("hasEdu") hasEdu: String? = "",
            @Field("boardId") board: String? = "",
            @Field("SkilledBy") SkilledBy: String = "",
            @Field("ntvqfLevel") ntvqfLevel: String = "",
            @Field("cat_id") cat_id: String = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<UpdateBlueCvModel>


    @FormUrlEncoded
    @POST("apps_resendOtp.asp")
    fun resendOtp(
            @Field("userId") tmpUserId: String? = "",
            @Field("mobile") mobile: String? = "",
            @Field("forCreateAccount") forCreateAccount: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID

    ): Call<ResendOtpModel>


    @FormUrlEncoded
    @POST("apps_photoInfo.asp")
    fun getPhotoInfo(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID


    ): Call<PhotoInfoModel>


    @FormUrlEncoded
    @POST("apps_delCart.asp")
    fun unShortlistJob(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("JobID") strJobId: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<UnshorlistJobModel>


    @FormUrlEncoded
    @POST("app_set_cookies.asp")
    fun getCookies(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<CookieModel>


    @FormUrlEncoded
    @POST("apps_view_stats.asp")
    fun mybdjobStats(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("isActivityDate") isActivityDate: String? = "",
            @Field("trainingId") trainingId: String? = "",
            @Field("isResumeUpdate") isResumeUpdate: String? = "",
            @Field("packageName") packageName: String? = "",
            @Field("packageNameVersion") packageNameVersion: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID

    ): Call<StatsModelClass>

    @FormUrlEncoded
    @POST("apps_apply_position.asp")
    fun getAppliedJobs(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("isActivityDate") isActivityDate: String? = "",
            @Field("pageNumber") pageNumber: String? = "",
            @Field("itemsPerPage") itemsPerPage: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<AppliedJobModel>


    @FormUrlEncoded
    @POST("apps_LastUpdate.asp")
    fun getLastUpdate(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("packageName") packageName: String? = "",
            @Field("packageNameVersion") packageNameVersion: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<LastUpdateModel>


    @FormUrlEncoded
    @POST("app_logout.asp")
    fun logout(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID,
            @Field("deviceId") deviceID: String? = ""
    ): Call<CookieModel>


    @FormUrlEncoded
    @POST("upload_img.aspx")
    fun DeletePhoto(
            @Field("folderName") folderName: String? = "",
            @Field("folderId") folderId: String? = "",
            @Field("imageName") imageName: String? = "",
            @Field("isResumeUpdate") isResumeUpdate: String? = "",
            @Field("status") status: String? = "",
            @Field("userid") userid: String? = "",
            @Field("decodeid") decodeid: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<PhotoInfoModel>

    @FormUrlEncoded
    @POST("apps_resume_view.asp")
    fun getEmpVwdMyResume(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("pageNumber") pageNumber: String? = "",
            @Field("itemsPerPage") itemsPerPage: String? = "",
            @Field("isActivityDate") isActivityDate: String? = "",
            @Field("AppsDate") AppsDate: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<EmpVwdResume>

    @FormUrlEncoded
    @POST("app_invite_interview_details.asp")
    fun getIterviewInvitationDetails(
            @Field("userId") userID: String? = "",
            @Field("decodeId") decodeID: String? = "",
            @Field("jobId") jobId: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<InvitationDetailModels>


    @FormUrlEncoded
    @POST("app_invite_interview_confirmation.asp")
    fun sendInterviewConfirmation(
            @Field("userId") userID: String? = "",
            @Field("decodeId") decodeID: String? = "",
            @Field("applyId") applyId: String? = "",
            @Field("activity") activity: String? = "",
            @Field("cancleReason") cancleReason: String? = "",
            @Field("otherComment") otherComment: String? = "",
            @Field("invitationId") invitationId: String? = "",
            @Field("rescheduleComment") rescheduleComment: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<InvitationDetailModels>

    @FormUrlEncoded
    @POST("app_invite_interview_company_ratting.asp")
    fun sendCompanyRating(
            @Field("userId") userID: String? = "",
            @Field("decodeId") decodeID: String? = "",
            @Field("jobId") jobId: String? = "",
            @Field("ratting") ratting: String? = "",
            @Field("rattingComment") rattingComment: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<InvitationDetailModels>

    @FormUrlEncoded
    @POST("app_invite_video_interview_details.asp")
    fun getVideoInterviewDetails(
            @Field("userId") userID: String? = "",
            @Field("decodeId") decodeID: String? = "",
            @Field("jobId") jobId: String? = ""
    ): Call<VideoInterviewDetails>

    @FormUrlEncoded
    @POST("app_submit_feedback.asp")
    suspend fun submitInterviewFeedback(
            @Field("userId") userID: String?,
            @Field("decodeId") decodeID: String?,
            @Field("jobId") jobId: String?,
            @Field("applyId") applyId: String?,
            @Field("rating") rating: String?,
            @Field("feedbackComment") feedbackComment: String?,
            @Field("featureName") featureName: String?,
            @Field("appId") appId: String?
    ): InterviewFeedback


    @FormUrlEncoded
    @POST("apps_salary_edit.asp")
    fun getUpdateSalaryMsg(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("JobId") JobId: String? = "",
            @Field("txtExpectedSalary") txtExpectedSalary: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<AppliedJobsSalaryEdit>


    @FormUrlEncoded
    @POST("apps_cancelApply.asp")
    fun getAppliedCancelMsg(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("JobId") JobId: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<CancelAppliedJobs>

    @FormUrlEncoded
    @POST("apps_update_application_status.asp")
    fun getEmpInteraction(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("status") status: String? = "",
            @Field("experienceId") experienceId: String? = "",
            @Field("changeExprience") changeExprience: String? = "",
            @Field("JobId") JobId: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<EmployerInteraction>


    @FormUrlEncoded
    @POST("apps_update_account.asp")
    fun getChangePassword(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("userName") userName: String? = "",
            @Field("isResumeUpdate") isResumeUpdate: String? = "",
            @Field("OldPass") OldPass: String? = "",
            @Field("NewPass") NewPass: String? = "",
            @Field("ConfirmPass") ConfirmPass: String? = "",
            @Field("isSmMedia") isSmMedia: String? = "",
            @Field("packageName") packageName: String? = "",
            @Field("packageNameVersion") packageNameVersion: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<ChangePassword>

    @FormUrlEncoded
    @POST("apps_prc_home.asp")
    fun getInviteCodeUserOwnerInfo(
            @Field("userID") userID: String? = "",
            @Field("decodeID") decodeID: String? = "",
            @Field("mobileNumber") mobileNumber: String? = "",
            @Field("catId") catId: String? = "",
            @Field("deviceID") deviceID: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<InviteCodeHomeModel>

    @FormUrlEncoded
    @POST("apps_invite_code.asp")
    fun getOwnerInviteCode(
            @Field("userID") userID: String? = "",
            @Field("decodeID") decodeID: String? = "",
            @Field("ownerID") ownerID: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<OwnerInviteCodeModel>

    @FormUrlEncoded
    @POST("apps_category_amount.asp")
    fun getCategoryAmount(
            @Field("userID") userID: String? = "",
            @Field("decodeID") decodeID: String? = "",
            @Field("pcOwnerId") pcOwnerId: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<InviteCodeCategoryAmountModel>


    @FormUrlEncoded
    @POST("apps_get_invite_list.asp")
    fun getOwnerInviteList(
            @Field("userID") userID: String? = "",
            @Field("decodeID") decodeID: String? = "",
            @Field("pcOwnerId") pcOwnerId: String? = "",
            @Field("verify_status") verify_status: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<OwnerInviteListModel>

    @FormUrlEncoded
    @POST("apps_prc_balance_report.asp")
    fun getOwnerStatement(
            @Field("userID") userID: String? = "",
            @Field("decodeID") decodeID: String? = "",
            @Field("pcOwnerId") pcOwnerId: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<InviteCodeOwnerStatementModel>


    @FormUrlEncoded
    @POST("apps_invited_user_status.asp")
    fun getInviteCodeUserStatus(
            @Field("userID") userID: String? = "",
            @Field("decodeID") decodeID: String? = "",
            @Field("invited_user_id") invited_user_id: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<InviteCodeUserStatusModel>

    @FormUrlEncoded
    @POST("apps_get_balance.asp")
    fun getOwnerBalance(
            @Field("userID") userID: String? = "",
            @Field("decodeID") decodeID: String? = "",
            @Field("pcOwnerId") pcOwnerId: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<InviteCodeBalanceModel>


    @FormUrlEncoded
    @POST("apps_get_payment_method.asp")
    fun getPaymentMethod(
            @Field("userID") userID: String? = "",
            @Field("decodeID") decodeID: String? = "",
            @Field("userType") userType: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<InviteCodePaymentMethodModel>


    @FormUrlEncoded
    @POST("apps_payment_system_insert.asp")
    fun insertPaymentMethod(
            @Field("userID") userID: String? = "",
            @Field("decodeID") decodeID: String? = "",
            @Field("userType") userType: String? = "",
            @Field("paymentType") paymentType: String? = "",
            @Field("accountNo") accountNo: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<PaymentTypeInsertModel>

    @FormUrlEncoded
    @POST("apps_prc_code_submit.asp")
    fun insertInviteCode(
            @Field("userID") userID: String? = "",
            @Field("decodeID") decodeID: String? = "",
            @Field("mobileNumber") mobileNumber: String? = "",
            @Field("catId") catId: String? = "",
            @Field("deviceID") deviceID: String? = "",
            @Field("promoCode") promoCode: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<PaymentTypeInsertModel>

    @FormUrlEncoded
    @POST("apps_prc_verify_user.asp")
    fun inviteCodeUserVerify(
            @Field("userID") userID: String? = "",
            @Field("decodeID") decodeID: String? = "",
            @Field("mobileNumber") mobileNumber: String? = "",
            @Field("catId") catId: String? = "",
            @Field("deviceID") deviceID: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<InviteCodeUserVerifyModel>

    @FormUrlEncoded
    @POST("apps_EmailCv.asp")
    fun getEmailResumeMsg(
            @Field("userID") userID: String? = "",
            @Field("decodeID") decodeID: String? = "",
            @Field("isResumeUpdate") isResumeUpdate: String? = "",
            @Field("userEmail") userEmail: String? = "",
            @Field("companyEmail") companyEmail: String? = "",
            @Field("mailSubject") mailSubject: String? = "",
            @Field("application") application: String? = "",
            @Field("fullName") fullName: String? = "",
            @Field("uploadedCv") uploadedCv: String? = "",
            @Field("Jobid") Jobid: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<EmailResume>


    @FormUrlEncoded
    @POST("apps_file_upload_info.asp")
    fun getCvFileAvailable(
            @Field("userID") userID: String? = "",
            @Field("decodeID") decodeID: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID

    ): Call<FileInfo>

    @Multipart
    @POST("file_upload.aspx")
    fun uploadCV(
            @PartMap partMap: Map<String, @JvmSuppressWildcards RequestBody>,
            @Part file: MultipartBody.Part
    ): Call<UploadResume>


    @FormUrlEncoded
    @POST("app_training.asp")
    fun getTrainingList(
            @Field("userID") userID: String?,
            @Field("decodeID") decodeID: String? = "",
            @Field("traingId") traingId: String? = "",
            @Field("AppsDate") AppsDate: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID

    ): Call<TrainingList>

    @FormUrlEncoded
    @POST("apps_SendEmailCV.aspx")
    fun sendEmailCV(
            @Field("userID") userID: String? = "",
            @Field("decodeID") decodeID: String? = "",
            @Field("uploadedCv") uploadedCv: String? = "",
            @Field("isResumeUpdate") isResumeUpdate: String? = "",
            @Field("fullName") fullName: String? = "",
            @Field("Jobid") Jobid: String? = "",
            @Field("application") application: String? = "",
            @Field("userEmail") userEmail: String? = "",
            @Field("mailSubject") mailSubject: String? = "",
            @Field("companyEmail") companyEmail: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID

    ): Call<SendEmailCV>

    @FormUrlEncoded
    @POST("apps_resume_Email.asp")
    fun emailedMyResume(
            @Field("userID") userID: String? = "",
            @Field("decodeID") decodeID: String? = "",
            @Field("pageNumber") pageNumber: String? = "",
            @Field("itemsPerPage") itemsPerPage: String? = "",
            @Field("isActivityDate") isActivityDate: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID

    ): Call<TimesEmailed>


    @FormUrlEncoded
    @POST("file_upload.aspx")
    fun downloadDeleteCV(
            @Field("userid") userID: String? = "",
            @Field("decodeid") decodeID: String? = "",
            @Field("status") status: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<UploadResume>


    @FormUrlEncoded
    @POST("apps_users_info_log.asp")
    fun sendDeviceInformation(
            @Field("OS_API_Level") OS_API_Level: String? = "",
            @Field("Internal_storage") Internal_storage: String? = "",
            @Field("Free_storage") Free_storage: String? = "",
            @Field("Mobile_Network") Mobile_Network: String? = "",
            @Field("RAM_Size") RAM_Size: String? = "",
            @Field("RAM_Free") RAM_Free: String? = "",
            @Field("ScreenSize") ScreenSize: String? = "",
            @Field("dateTime") dateTime: String? = "",
            @Field("FCMToken") FCMToken: String? = "",
            @Field("Manufecturer") Manufecturer: String? = "",
            @Field("AppVersion") AppVersion: String? = "",
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("deviceID") deviceID: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<UploadResume>


    @FormUrlEncoded
    @POST("apps_Emp_Message.asp")
    fun getEmployerMessageList(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("itemsPerPage") itemsPerPage: String? = "",
            @Field("pageNumber") pageNumber: String? = "",
            @Field("isActivityDate") isActivityDate: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<EmployerMessageModel>


    @FormUrlEncoded
    @POST("apps_Emp_Message_view.asp")
    fun getMessageDetail(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("messageId") messageId: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<MessageDetailModel>

    @FormUrlEncoded
    @POST("apps_update_application_status.asp")
    fun getapps_update_application_status(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("status") status: String? = "",
            @Field("experienceId") experienceId: String? = "",
            @Field("changeExprience") changeExprience: String? = "",
            @Field("jobId") jobId: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<InteractionModel>

    @FormUrlEncoded
    @POST("apps_sms_settings_view.asp")
    suspend fun getSMSSetting(
            @Field("userId") userID: String?,
            @Field("decodeId") decodeID: String?,
            @Field("appId") appId: String?
    ): SMSSettings

    @FormUrlEncoded
    @POST("apps_sms_settings_update.asp")
    suspend fun updateSMSSettings(

            @Field("userId") userID: String?,
            @Field("decodeId") decodeID: String?,
            @Field("appId") appId: String?,
            @Field("dailySmsLimit") dailySMSLimit: Int?,
            @Field("smsAlertOn") alertOn: Int?
    ): CommonResponse


    @FormUrlEncoded
    @POST("apps_transaction_overview_list.asp")
    suspend fun getTransactionList(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("appId") appId: String? = "",
            @Field("startDate") startDate: String? = "",
            @Field("endDate") endDate: String? = "",
            @Field("packageTypeId") packageTypeId: String? = "",
            @Field("pageNumber") pageNumber: String? = "",
            @Field("itemsPerPage") itemsPerPage: String? = ""
    ): TransactionList

    @FormUrlEncoded
    @POST("apps_payment_info_before_entering_gateway.asp")
    suspend fun paymentInfoBeforeEnteringGateway(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("appId") appId: String? = "",
            @Field("totalQuantity") totalQuantity: String? = "",
            @Field("totalAmount") totalAmount: String? = "",
            @Field("serviceId") serviceId: Int?,
            @Field("isFree") isFree: String? = "",
            @Field("ip") ip: String? = ""
    ): PaymentInfoBeforeGateway

    @FormUrlEncoded
    @POST("apps_payment_info_after_returning_gateway.asp")
    suspend fun paymentInfoAfterReturningGateway(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("appId") appId: String? = "",
            @Field("tran_id") tranId: String? = "",
            @Field("card_type") cardType: String? = "",
            @Field("store_amount") storeAmount: String?,
            @Field("val_id") valId: String? = "",
            @Field("status") status: String? = "",
            @Field("currency_type") currencyType: String? = "",
            @Field("tran_date") tranDate: String? = "",
            @Field("ip") ip: String? = ""
    ): PaymentInfoAfterGateway

    /* @FormUrlEncoded
     @POST("apps_subscribe_sms_follow_employer.asp")
     fun subscribeOrUnsubscribeSMSFromFollowedEmployers(
             @Field("userId") userId: String? = "",
             @Field("decodeId") decodeId: String? = "",
             @Field("followId") followId: String? = "",
             @Field("companyId") companyId: String? = "",
             @Field("action") action: Int?,
             @Field("appId") appId: String? = Constants.APP_ID

     ) : Call<EmployerSubscribeModel>*/

    @FormUrlEncoded
    @POST("apps_subscribe_sms_follow_employer.asp")
    fun subscribeOrUnsubscribeSMSFromFollowedEmployers(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("followId") followId: String? = "",
            @Field("companyId") companyId: String? = "",
            @Field("action") action: Int?,
            @Field("appId") appId: String? = Constants.APP_ID
    ): Call<EmployerSubscribeModel>


    //Live Interview
    @FormUrlEncoded
    @POST("app_live_interview_invitation_home.asp")
    suspend fun getLiveInterviewList(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = "",
            @Field("isActivityDate") isActivityDate: String? = "",
            @Field("pageNumber") pageNumber: String? = "",
            @Field("itemsPerPage") itemsPerPage: String? = ""
    ): LiveInterviewList

    @FormUrlEncoded
    @POST("app_live_invite_interview_details.asp")
    suspend fun getLiveInterviewDetails(
            @Field("userId") userID: String?,
            @Field("decodeId") decodeID: String?,
            @Field("jobId") jobId: String?
    ): LiveInterviewDetails

    @FormUrlEncoded
    @POST("apps_live_interview_applicant_ready_status.asp")
    suspend fun applicantStatusInfo(
            @Field("userId") userID: String? = "",
            @Field("decodeId") decodeID: String? = "",
            @Field("applyId") applyId: String? = "",
            @Field("processId") processId: String? = "",
            @Field("applicantStatus") applicantStatus: String? = ""
    ) : ApplicantStatusModel

    @FormUrlEncoded
    @POST("app_invite_interview_confirmation.asp")
    suspend fun sendInterviewConfirmationLive(
            @Field("userId") userID: String? = "",
            @Field("decodeId") decodeID: String? = "",
            @Field("applyId") applyId: String? = "",
            @Field("activity") activity: String? = "",
            @Field("cancleReason") cancleReason: String? = "",
            @Field("otherComment") otherComment: String? = "",
            @Field("invitationId") invitationId: String? = "",
            @Field("rescheduleComment") rescheduleComment: String? = "",
            @Field("appId") appId: String? = Constants.APP_ID
    ): LiveInterviewDetails

    @FormUrlEncoded
    @POST("apps_live_interview_room_set_chat.asp")
    suspend fun postChatMessages(
            @Field("userId") userID: String? = "",
            @Field("decodeId") decodeID: String? = "",
            @Field("processId") processId: String? = "",
            @Field("chatText") chatText: String? = "",
            @Field("hostType") hostType: String? = "",
            @Field("strUserId") strUserId: String? = "0",
            @Field("strTargetUser") strTargetUser: String? = "0"
    ): PostChatModel

    @FormUrlEncoded
    @POST("https://mybdjobs.bdjobs.com/mybdjobs/onlinechat/live_interview_room_chat_info_v2.asp")
    suspend fun chatLog(
            @Field("prId") processId: String?
    ): ChatLogModel


    // resume dashboard

    @FormUrlEncoded
    @POST("apps_resume_emailOrView_count.asp")
    suspend fun manageResumeStats(
        @Field("userId") userID: String? = "",
        @Field("decodeId") decodeID: String? = "",
        @Field("appId") appID: String? = "",
    ) : ManageResumeStats

    @FormUrlEncoded
    @POST("apps_resume_privacy_view.asp")
    suspend fun resumePrivacyStatus(
        @Field("userId") userID: String? = "",
        @Field("decodeId") decodeID: String? = "",
    ) : ResumePrivacyStatus

    @FormUrlEncoded
    @POST("apps_all_resumes_stats.asp")
    suspend fun manageResumeDetailsStat(
        @Field("userId") userID: String? = "",
        @Field("decodeId") decodeID: String? = "",
        @Field("cvPosted") cvPosted:String?=""
    ) : ManageResumeDetailsStat

    @FormUrlEncoded
    @POST("apps_personalize_resume_stats.asp")
    suspend fun personalizedResumeStat(
        @Field("userID") userID: String? = "",
        @Field("decodeID") decodeID: String? = "",
        @Field("cvPosted") cvPosted:String?=""
    ) : PersonalizedResumeStat


    companion object Factory {
        @Volatile
        private var retrofit: Retrofit? = null

        @Synchronized
        fun create(): ApiServiceMyBdjobs {

            retrofit ?: synchronized(this) {
                retrofit = buildRetrofit()
            }

            return retrofit?.create(ApiServiceMyBdjobs::class.java)!!
        }

        private fun buildRetrofit(): Retrofit {

            val gson = GsonBuilder()
                    .setLenient()
                    .create()

            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            val okHttpClient = OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build()

            return Retrofit.Builder().apply {
                baseUrl(Constants.baseUrlMyBdjobs)
                addConverterFactory(GsonConverterFactory.create(gson)).addConverterFactory(MoshiConverterFactory.create(moshi))
                if (BuildConfig.DEBUG) {
                    client(okHttpClient)
                }
            }.build()
        }

    }

}