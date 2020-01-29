package com.bdjobs.app.assessment.network

//import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.bdjobs.app.assessment.models.Certificate
import com.bdjobs.app.assessment.models.Result
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

private val BASE_URL = "https://my.bdjobs.com/apps/mybdjobs/V1/assessment/"

private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

val interceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()

private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()


interface AssessmentApiService {

    @FormUrlEncoded
    @POST("apps_smnt_complete_certificationlist.asp")
    suspend fun getCertificatesFromAPI(
            @Field("userID") userID: String? = "",
            @Field("decodeID") decodeID: String? = ""
    ): Certificate

    @FormUrlEncoded
    @POST("apps_smnt_certification_result_detail.asp")
    suspend fun getResultFromAPI(
            @Field("userID") userID: String? = "",
            @Field("decodeID") decodeID: String? = "",
            @Field("scheduleId") scheduleID: String? = "",
            @Field("jobId") jobId: String? = "",
            @Field("jobRoleId") jobRoleId: String? = "",
            @Field("assessmentId") assessmentId: String? = "",
            @Field("strExamType") strExamType: String? = "",
            @Field("res") res: String? = "",
            @Field("amcatJobId") amcatJobId: String? = ""
    ): Result


}

object AssessmentApi {
    val retrofitService: AssessmentApiService by lazy {
        retrofit.create(
                AssessmentApiService::class.java)
    }
}