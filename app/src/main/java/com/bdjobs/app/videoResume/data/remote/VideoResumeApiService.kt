package com.bdjobs.app.videoResume.data.remote

//import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
//import okhttp3.logging.HttpLoggingInterceptor
import android.content.Context
import com.bdjobs.app.videoInterview.util.NetworkConnectionInterceptor
import com.bdjobs.app.videoResume.data.models.CommonResponse
import com.bdjobs.app.videoResume.data.models.VideoResumeQuestionList
import com.bdjobs.app.videoResume.data.models.VideoResumeStatistics
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.util.*
import java.util.concurrent.TimeUnit


private const val VIDEO_RESUME_BASE_URL = "https://my.bdjobs.com/apps/mybdjobs/v1/"
private const val ANSWER_UPLOAD_BASE_URL = "https://vdo.bdjobs.com/apps/mybdjobs/"

private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

interface VideoResumeApiService {

    @FormUrlEncoded
    @POST("app_video_resume_stats_v1.asp")
    suspend fun getVideoResumeStatistics(
            @Field("userId") userID: String?,
            @Field("decodeId") decodeID: String?,
            @Field("appId") appId: String?,
            @Field("lang") lang: String?
    ): Response<VideoResumeStatistics>

    @FormUrlEncoded
    @POST("app_video_resume_visibility_status_update.asp")
    suspend fun submitStatusVisibility(
            @Field("userId") userID: String?,
            @Field("decodeId") decodeID: String?,
            @Field("appId") appId: String?,
            @Field("lang") lang: String?,
            @Field("statusVisibility") statusVisibility: String?,
    ): Response<CommonResponse>

    @FormUrlEncoded
    @POST("app_video_resume_questionlist_v1.asp")
    suspend fun getVideoResumeQuestionList(
            @Field("userId") userID: String?,
            @Field("decodeId") decodeID: String?,
            @Field("appId") appId: String?,
            @Field("lang") lang: String?
    ): Response<VideoResumeQuestionList>


    @Multipart
    @POST("https://vdo.bdjobs.com/apps/mybdjobs/app_video_resume_upload_answer.asp")
    suspend fun uploadVideo(
            @Part("userId") userID: RequestBody?,
            @Part("decodeId") decodeID: RequestBody?,
            @Part("questionId") quesId: RequestBody?,
            @Part("questionDuration") duration: RequestBody?,
            @Part("questionSerialNo") questionSerialNo: RequestBody?,
            @Part("deviceType") deviceType: RequestBody?,
            @Part("browserInfo") browserInfo: RequestBody?,
            @Part("appId") appId: RequestBody?,
            @Part("lang") lang: RequestBody?,
            @Part file: MultipartBody.Part?
    ): Response<CommonResponse>


    @FormUrlEncoded
    @POST("https://vdo.bdjobs.com/apps/mybdjobs/app_video_resume_delete_answer.asp")
    suspend fun deleteSingleVideoOfResume(
            @Field("userId") userID: String?,
            @Field("decodeId") decodeID: String?,
            @Field("appId") appId: String?,
            @Field("lang") lang: String?,
            @Field("aID") aID: String?,
            @Field("questionId") questionId: String?,
            ): Response<CommonResponse>



    companion object Factory {
        @Volatile
        private var retrofit: Retrofit? = null

        @Synchronized
        fun create(context: Context, type: Int? = 0): VideoResumeApiService {

            retrofit ?: synchronized(this) {
                retrofit = buildRetrofit(context, type)
            }

            return retrofit?.create(VideoResumeApiService::class.java)!!
        }



        private fun buildRetrofit(context: Context, type: Int? = 0): Retrofit {

            val loginOkHttpClient = OkHttpClient.Builder()
                    .addInterceptor(NetworkConnectionInterceptor(context))
//                    .addInterceptor(OkHttpProfilerInterceptor())
                    .readTimeout(1200, TimeUnit.SECONDS)
                    .connectTimeout(1200, TimeUnit.SECONDS)
                    .writeTimeout(1200, TimeUnit.SECONDS)

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
