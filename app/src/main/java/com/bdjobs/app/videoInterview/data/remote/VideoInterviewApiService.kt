package com.bdjobs.app.videoInterview.data.remote

import android.content.Context
import android.util.Log
import com.bdjobs.app.videoInterview.data.models.*
import com.bdjobs.app.videoInterview.util.NetworkConnectionInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

// TODO: 6/4/20 create the base url
private const val VIDEO_INTERVIEW_BASE_URL = "https://my.bdjobs.com/apps/mybdjobs/v1/"
private const val VIDEO_UPLOAD_BASE_URL = "https://vdo.bdjobs.com/apps/mybdjobs/"

private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

interface VideoInterviewApiService {

    @POST("app_video_interview_invitation_delete_submit.asp")
    suspend fun submitAnswer(
            @Field("userId") userID: String?,
            @Field("decodeId") decodeID: String?,
            @Field("jobId") jobId: String?,
            @Field("applyId") applyId: String?,
            @Field("type") type: String?,
            @Field("totalAnsCount") totalAnswerCount: String?
    ) : CommonResponse

    @Multipart
    @POST("https://vdo.bdjobs.com/apps/mybdjobs/app_video_interview_invitation_upload_answer.asp")
    suspend fun uploadVideo(
            @Part("userId") userID: RequestBody?,
            @Part("decodeId") decodeID: RequestBody?,
            @Part("jobId") jobId: RequestBody?,
            @Part("applyId") applyId: RequestBody?,
            @Part("quesId") quesId: RequestBody?,
            @Part("duration") duration: RequestBody?,
            @Part("questionSerialNo") questionSerialNo: RequestBody?,
            @Part file: MultipartBody.Part?
    ): CommonResponse

    @FormUrlEncoded
    @POST("app_video_interview_invitation_strat_time.asp")
    suspend fun sendVideoStartedInfo(
            @Field("userId") userID: String?,
            @Field("decodeId") decodeID: String?,
            @Field("applyId") applyId: String?,
            @Field("deviceName") deviceName: String?
    ): CommonResponse

    @FormUrlEncoded
    @POST("app_video_interview_invitation_detail.asp")
    suspend fun getVideoInterviewDetails(
            @Field("userId") userID: String?,
            @Field("decodeId") decodeID: String?,
            @Field("jobId") jobId: String?
    ): VideoInterviewDetails

    @FormUrlEncoded
    @POST("app_video_interview_invitation_questionlist.asp")
    suspend fun getVideoInterviewQuestionList(
            @Field("userId") userID: String?,
            @Field("decodeId") decodeID: String?,
            @Field("jobId") jobId: String?,
            @Field("applyId") applyId: String?
    ): VideoInterviewQuestionList


    @FormUrlEncoded
    @POST("app_video_interview_invitation_home.asp")
    suspend fun getInterviewListFromAPI(
            @Field("userId") userID: String? = "",
            @Field("decodeId") decodeID: String? = ""

    ): VideoInterviewListModel

    companion object Factory {
        @Volatile
        private var retrofit: Retrofit? = null

        //0 base url, 1 video upload url
        @Synchronized
        fun create(context: Context, type: Int? = 0): VideoInterviewApiService {

            retrofit ?: synchronized(this) {
                retrofit = buildRetrofit(context, type)
            }

            return retrofit?.create(VideoInterviewApiService::class.java)!!
        }

        private fun buildRetrofit(context: Context, type: Int? = 0): Retrofit {

            Log.d("rakib type ", "$type")

            val loginInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val loginOkHttpClient = OkHttpClient.Builder()
                    .addInterceptor(loginInterceptor)
                    .addInterceptor(NetworkConnectionInterceptor(context))
                    .build()

            return Retrofit.Builder().apply {
                addConverterFactory(MoshiConverterFactory.create(moshi))
                if (type == 0)
                    baseUrl(VIDEO_INTERVIEW_BASE_URL)
                else
                    baseUrl(VIDEO_UPLOAD_BASE_URL)
                client(loginOkHttpClient)
            }.build()
        }

    }

}
