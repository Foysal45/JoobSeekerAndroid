package com.bdjobs.app.videoInterview.data.remote

import android.content.Context
import com.bdjobs.app.assessment.models.Home
import com.bdjobs.app.videoInterview.data.models.InterviewListData
import com.bdjobs.app.videoInterview.data.models.VideoInterviewListModel
import com.bdjobs.app.videoInterview.util.NetworkConnectionInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

// TODO: 6/4/20 create the base url
private const val VIDEO_INTERVIEW_BASE_URL = "https://my.bdjobs.com/apps/mybdjobs/v1/"

private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

interface VideoInterviewApiService {


    @FormUrlEncoded
    @POST("app_video_interview_invitation_home.asp")
    suspend fun getInterviewListFromAPI(
            @Field("userId") userID: String? = "241028",
            @Field("decodeId") decodeID: String? = "T8B8Rx"

    ) : VideoInterviewListModel

    companion object Factory{
        @Volatile
        private var retrofit: Retrofit? = null

        @Synchronized
        fun create(context: Context): VideoInterviewApiService {

            retrofit ?: synchronized(this) {
                retrofit = buildRetrofit(context)
            }

            return retrofit?.create(VideoInterviewApiService::class.java)!!
        }

        private fun buildRetrofit(context: Context) : Retrofit{

            val loginInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val loginOkHttpClient = OkHttpClient.Builder()
                    .addInterceptor(loginInterceptor)
                    .addInterceptor(NetworkConnectionInterceptor(context))
                    .build()

            return Retrofit.Builder()
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .baseUrl(VIDEO_INTERVIEW_BASE_URL)
                    .client(loginOkHttpClient)
                    .build()
        }

    }

}
