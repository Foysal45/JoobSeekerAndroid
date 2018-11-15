package com.bdjobs.app.API

import com.bdjobs.app.API.ModelClasses.LoginSessionModel
import com.bdjobs.app.API.ModelClasses.LoginUserModel
import com.bdjobs.app.API.ModelClasses.SocialLoginAccountListModel
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.Constants.Companion.api_mybdjobs_app_agent_log
import com.bdjobs.app.Utilities.Constants.Companion.api_mybdjobs_app_signinprocess
import com.bdjobs.app.Utilities.Constants.Companion.api_mybdjobs_app_social_agent_log
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