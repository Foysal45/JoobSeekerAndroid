package com.bdjobs.app.API

import com.bdjobs.app.API.ModelClasses.*
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.Constants.Companion.api_mybdjobs_app_agent_log
import com.bdjobs.app.Utilities.Constants.Companion.api_mybdjobs_app_favouritejob_count
import com.bdjobs.app.Utilities.Constants.Companion.api_mybdjobs_app_signinprocess
import com.bdjobs.app.Utilities.Constants.Companion.api_mybdjobs_app_social_agent_log
import com.bdjobs.app.editResume.adapters.models.AddorUpdateModel
import com.bdjobs.app.editResume.adapters.models.GetExps
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
    ): Call<List<GetExps>>

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
    @POST("http://my.bdjobs.com/apps/mybdjobs/apps_delete.asp")
    fun deleteData(
            @Field("itemName") itemName: String,
            @Field("id") id: String,
            @Field("isResumeUpdate") isUpdate: String,
            @Field("userId") userId: String,
            @Field("decodeId") decodeId: String): Call<AddorUpdateModel>


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