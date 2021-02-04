package com.bdjobs.app.videoResume.data.remote

import android.content.Context
import android.util.Log
import com.bdjobs.app.videoInterview.util.NetworkConnectionInterceptor
import com.bdjobs.app.videoResume.data.models.CommonResponse
import com.bdjobs.app.videoResume.data.models.VideoResumeQuestionList
import com.bdjobs.app.videoResume.data.models.VideoResumeStatistics
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

private const val VIDEO_RESUME_BASE_URL = "https://my.bdjobs.com/apps/mybdjobs/v1/"
private const val ANSWER_UPLOAD_BASE_URL = "https://vdo.bdjobs.com/apps/mybdjobs/"

private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

interface VideoResumeApiService {

    @FormUrlEncoded
    @POST("app_video_resume_stats.asp")
    suspend fun getVideoResumeStatistics(
            @Field("userId") userID: String?,
            @Field("decodeId") decodeID: String?,
            @Field("appId") appId: String?,
            @Field("lang") lang: String?
    ): VideoResumeStatistics

    @FormUrlEncoded
    @POST("app_video_resume_visibility_status_update.asp")
    suspend fun submitStatusVisibility(
            @Field("userId") userID: String?,
            @Field("decodeId") decodeID: String?,
            @Field("appId") appId: String?,
            @Field("lang") lang: String?,
            @Field("statusVisibility") statusVisibility: String?,
    ): CommonResponse

    @FormUrlEncoded
    @POST("app_video_resume_questionlist.asp")
    suspend fun getVideoResumeQuestionList(
            @Field("userId") userID: String?,
            @Field("decodeId") decodeID: String?,
            @Field("appId") appId: String?,
            @Field("lang") lang: String?
    ): VideoResumeQuestionList

//    @Multipart
//    @POST("https://vdo.bdjobs.com/apps/mybdjobs/app_video_interview_invitation_upload_answer.asp")
//    suspend fun uploadVideo(
//            @Part("userId") userID: RequestBody?,
//            @Part("decodeId") decodeID: RequestBody?,
//            @Part("jobId") jobId: RequestBody?,
//            @Part("applyId") applyId: RequestBody?,
//            @Part("quesId") quesId: RequestBody?,
//            @Part("duration") duration: RequestBody?,
//            @Part("questionSerialNo") questionSerialNo: RequestBody?,
//            @Part file: MultipartBody.Part?
//    ): CommonResponse
//
//    @FormUrlEncoded
//    @POST("app_video_interview_invitation_delete_submit.asp")
//    suspend fun submitAnswer(
//            @Field("userId") userID: String?,
//            @Field("decodeId") decodeID: String?,
//            @Field("jobId") jobId: String?,
//            @Field("applyId") applyId: String?,
//            @Field("type") type: String?,
//            @Field("totalAnsCount") totalAnswerCount: String?
//    ): CommonResponse


    companion object Factory {
        @Volatile
        private var retrofit: Retrofit? = null

        //0 base url, 1 answer upload url
        @Synchronized
        fun create(context: Context, type: Int? = 0): VideoResumeApiService {

            retrofit ?: synchronized(this) {
                retrofit = buildRetrofit(context, type)
            }

            return retrofit?.create(VideoResumeApiService::class.java)!!
        }

        private fun buildRetrofit(context: Context, type: Int? = 0): Retrofit {

            Log.d("salvin type ", "$type")

            val loginInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val loginOkHttpClient = OkHttpClient.Builder()
                    .addInterceptor(loginInterceptor)
                    .addInterceptor(NetworkConnectionInterceptor(context))
                    .readTimeout(600, TimeUnit.SECONDS)
                    .connectTimeout(600, TimeUnit.SECONDS)
                    .build()

            return Retrofit.Builder().apply {
                addConverterFactory(MoshiConverterFactory.create(moshi))
                if (type == 0)
                    baseUrl(VIDEO_RESUME_BASE_URL)
                else
                    baseUrl(ANSWER_UPLOAD_BASE_URL)
                client(loginOkHttpClient)
            }.build()
        }

    }

}
