package com.bdjobs.app.API

import com.bdjobs.app.API.ModelClasses.DatabaseUpdateModel
import com.bdjobs.app.Jobs.GetResponseJobLIst
import com.bdjobs.app.Jobs.JobDetailJsonModel
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.Constants.Companion.api_jobs_db_update
import com.google.gson.GsonBuilder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url


interface ApiServiceJobs {
    @GET(api_jobs_db_update)
    fun getDbInfo(@Query("lastUpdateDate") lastUpdateDate: String): Call<DatabaseUpdateModel>




    @GET("v1/joblist.asp?")
    fun getJobList(
            @Query("Newspaper") Newspaper: String,
            @Query("armyp") armyp: String,
            @Query("bc") bc: String,
            @Query("category") category: String,
            @Query("deadline") deadline: String,
            @Query("encoded") encoded: String,
            @Query("experience") experience: String,
            @Query("gender") gender: String,
            @Query("genderB") genderB: String,
            @Query("industry") industry: String,
            @Query("isFirstRequest") isFirstRequest: String,
            @Query("jobNature") jobNature: String,
            @Query("jobType") jobType: String,
            @Query("keyword") keyword: String,
            @Query("lastJPD") lastJPD: String,
            @Query("location") location: String,
            @Query("org") org: String,
            @Query("pageid") pageid: String,
            @Query("pg") pg: Int,
            @Query("postedWithin") postedWithin: String,
            @Query("qAge") qAge: String,
            @Query("rpp") rpp: String,
            @Query("slno") slno: String,
            @Query("version") version: String


    ): Call<GetResponseJobLIst>


    ///new
    @GET("v1/jobdetailsscreen.asp")
    fun getJobdetailData(
            @Query("encoded") encoded : String,
            @Query("jobId") jobId : String,
            @Query ("ln") ln : String,
            @Query("next") next : String,
            @Query ("slno") slno : String,
            @Query ("userId") userId: String,
            @Query ("version") version : String
    ):Call<JobDetailJsonModel>







    @GET
    fun downloadDatabaseFile(@Url fileUrl: String): Call<ResponseBody>

    companion object Factory {

        fun create(): ApiServiceJobs {
            val gson = GsonBuilder()
                    .setLenient()
                    .create()

            val retrofit = Retrofit.Builder()
                    .baseUrl(Constants.baseUrlJobs)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            return retrofit.create(ApiServiceJobs::class.java)
        }
    }

}