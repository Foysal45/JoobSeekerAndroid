package com.bdjobs.app.videoInterview.data.remote

import android.content.Context
import com.bdjobs.app.videoInterview.data.models.CommonResponse
import com.bdjobs.app.videoInterview.data.models.VideoInterviewDetails
import com.bdjobs.app.videoInterview.data.models.VideoInterviewListModel
import com.bdjobs.app.videoInterview.data.models.VideoInterviewQuestionList
import com.bdjobs.app.videoInterview.util.NetworkConnectionInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import java.io.File

// TODO: 6/4/20 create the base url
private const val VIDEO_INTERVIEW_BASE_URL = "https://my.bdjobs.com/apps/mybdjobs/v1/"
private const val VIDEO_UPLOAD_BASE_URL = "https://vdo.bdjobs.com/apps/mybdjobs/"

private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

interface VideoInterviewApiService {

    @FormUrlEncoded @Multipart
    @POST("app_video_interview_invitation_upload_answer.asp")
    suspend fun uploadVideo(
            @Field("userId") userID: String?,
            @Field("decodeId") decodeID: String?,
            @Field("jobId") jobId: String?,
            @Field("applyId") applyId: String?,
            @Field("quesId") quesId: String?,
            @Field("duration") duration: String?,
            @Field("questionSerialNo") questionSerialNo: String?,
            @Field("file") file : File?
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
