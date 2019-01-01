package com.bdjobs.app.API

import com.bdjobs.app.API.ModelClasses.*
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.Constants.Companion.api_mybdjobs_app_agent_log
import com.bdjobs.app.Utilities.Constants.Companion.api_mybdjobs_app_favouritejob_count
import com.bdjobs.app.Utilities.Constants.Companion.api_mybdjobs_app_signinprocess
import com.bdjobs.app.Utilities.Constants.Companion.api_mybdjobs_app_social_agent_log
import com.bdjobs.app.editResume.adapters.models.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiServiceMyBdjobs {


    @FormUrlEncoded
    @POST(api_mybdjobs_app_signinprocess)
    fun getLoginUserDetails(@Field("username") username: String?): Call<LoginUserModel>

    @FormUrlEncoded
    @POST(api_mybdjobs_app_agent_log)
    fun doLogin(@Field("username") username: String?="",
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
    @POST("apps_step_01_view_cai.asp")
    fun getCareerInfo(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?
    ): Call<GetCarrerInfo>

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
            @Field("comboType") comboType: String?,
            @Field("txtCourse") txtCourse: String?,
            @Field("txtTrade") txtTrade: String?,
            @Field("cboCommissionDate") cboCommissionDate: String?,
            @Field("cboRetirementDate") cboRetirementDate: String?,
            @Field("arm_id") arm_id: String?,
            @Field("hId") hId: String?
    ): Call<AddorUpdateModel>

    @FormUrlEncoded
    @POST("apps_step_03_view_rai.asp")
    fun getArmyExpsList(
            @Field("userId") userId: String?,
            @Field("decodeId") decodeId: String?
    ): Call<GetArmyEmpHis>

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
            @Field("gender") gender:String? = "",
            @Field("email") email:String? = "",
            @Field("username") username:String? = "",
            @Field("password") password: String? = "",
            @Field("confirm") confirm: String? = "",
            @Field("mobile") mobile: String? = "",
            @Field("smid") smid: String? = "",
            @Field("isSMLogin") isSMLogin: String? = "",
            @Field("categoryType") categoryType:String? = "",
            @Field("userNameType") userNameType:String? = "",
            @Field("smediatype") smediatype:String? = "",
            @Field("prefCat") prefCat: String? = "",
            @Field("txtCountryCode") txtCountryCode: String? = "",
            @Field("packageName") packageName: String? = "",
            @Field("packageNameVersion") packageNameVersion:String? = ""

    ): Call<CreateAccountModel>



    @FormUrlEncoded
    @POST("apps_otpVerify.asp")
    fun sendOtpToVerify(
            @Field("tmpUserId") tmpUserId: String? = "",
            @Field("optCode") optCode: String? = "",
            @Field("packageName") packageName:String? = "",
            @Field("packageNameVersion") packageNameVersion:String? = ""


    ): Call<CreateAccountModel>


    @FormUrlEncoded
    @POST("apps_updateBlueCv.asp")
    fun sendBlueCollarUserInfo(
            @Field("UserID") userid: String ="",
            @Field("decodeId") decodeId: String="",
            @Field("userName") userName: String = "",
            @Field("village") village: String ="",
            @Field("locationID") locationID: String ="",
            @Field("dob") dob: String ="",
            @Field("age") age: String ="",
            @Field("skillID") skillID: String ="",
            @Field("expYear") expYear: String ="",
            @Field("levelOfEducation") levelOfEducation: String="",
            @Field("institute") institute: String = "",
            @Field("inlineRadioOptions") inlineRadioOptions: String="",
            @Field("examDegreeTitle") examDegreeTitle: String ="",
            @Field("yearOfPassing") yearOfPassing: String ="",
            @Field("hasEdu") hasEdu: String =""
    ): Call<UpdateBlueCvModel>


    @FormUrlEncoded
    @POST("apps_resendOtp.asp")
    fun resendOtp(
            @Field("userId") tmpUserId: String? = "",
            @Field("mobile") mobile: String? = "",
            @Field("forCreateAccount") forCreateAccount:String? = ""

    ): Call<ResendOtpModel>


    @FormUrlEncoded
    @POST("apps_photoInfo.asp")
    fun getPhotoInfo(
            @Field("userId") userId: String? = "",
            @Field("decodeId") decodeId: String? = ""


    ): Call<PhotoInfoModel>




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