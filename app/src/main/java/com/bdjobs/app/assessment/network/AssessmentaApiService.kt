package com.bdjobs.app.assessment.network

//import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.bdjobs.app.assessment.models.*
import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
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
        .addInterceptor(OkHttpProfilerInterceptor())
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

    @FormUrlEncoded
    @POST("apps_smnt_add_result_in_cv.asp")
    suspend fun updateResult(
            @Field("userID") userID: String? = "",
            @Field("decodeID") decodeID: String? = "",
            @Field("assessmentId") assessmentId: String? = "",
            @Field("jobRoleId") jobRoleId: String? = "",
            @Field("scheduleId") scheduleID: String? = "",
            @Field("actionType") actionType: String? = ""
    ) : Result

    @FormUrlEncoded
    @POST("apps_smnt_new_certification_schedule_list.asp")
    suspend fun getScheduleFromAPI(
            @Field("userID") userID: String? = "",
            @Field("decodeID") decodeID: String? = "",
            @Field("pageno") pageNo: String? = "",
            @Field("pagesize") pageSize: String? = "",
            @Field("fromDate") fromDate: String? = "",
            @Field("toDate") toDate: String? = "",
            @Field("venue") venue: String? = ""
    ) : Schedule

    @FormUrlEncoded
    @POST("apps_smnt_certification_home.asp")
    suspend fun getHomeDetailsFromAPI(
            @Field("userID") userID: String? = "",
            @Field("decodeID") decodeID: String? = "",
            @Field("postingDate") postingDate: String? = ""
    ) : Home

    @FormUrlEncoded
    @POST("app_smnt_certification_schedule_booking_update_cancel.asp")
    suspend fun bookSchedule(
            @Field("userId") userID: String? = "",
            @Field("decodeId") decodeID: String? = "",
            @Field("strActionType") actionType: String? = "",
            @Field("scID") scID: String? = "",
            @Field("SchID") schID: String? = "",
            @Field("OPID") opID: String? = "",
            @Field("fltBdjAmount") amount: String? = "",
            @Field("strTransactionDate") transactionDate: String? = "",
            @Field("isFromHome") isFromHome: String? = ""
    ) : BookingResponse

}

object AssessmentApi {
    val retrofitService: AssessmentApiService by lazy {
        retrofit.create(
                AssessmentApiService::class.java)
    }
}