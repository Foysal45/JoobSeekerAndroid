package com.bdjobs.app.API

import com.bdjobs.app.API.ModelClasses.*
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.Constants.Companion.api_jobs_db_update
import com.google.gson.GsonBuilder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface ApiServiceJobs {
    @GET(api_jobs_db_update)
    fun getDbInfo(@Query("lastUpdateDate") lastUpdateDate: String): Call<DatabaseUpdateModel>

    @GET("joblist.asp")
    fun getJobList(
            @Query("jobLevel") jobLevel: String? = "",
            @Query("Newspaper") Newspaper: String? = "",
            @Query("armyp") armyp: String? = "",
            @Query("bc") bc: String? = "",
            @Query("category") category: String? = "",
            @Query("deadline") deadline: String? = "",
            @Query("encoded") encoded: String? = "",
            @Query("experience") experience: String? = "",
            @Query("gender") gender: String? = "",
            @Query("genderB") genderB: String? = "",
            @Query("industry") industry: String? = "",
            @Query("isFirstRequest") isFirstRequest: String? = "",
            @Query("jobNature") jobNature: String? = "",
            @Query("jobType") jobType: String? = "",
            @Query("keyword") keyword: String? = "",
            @Query("lastJPD") lastJPD: String? = "",
            @Query("location") location: String? = "",
            @Query("org") org: String? = "",
            @Query("pageid") pageid: String? = "",
            @Query("pg") pg: Int? = 1,
            @Query("postedWithin") postedWithin: String? = "",
            @Query("qAge") qAge: String? = "",
            @Query("rpp") rpp: String? = "",
            @Query("slno") slno: String? = "",
            @Query("version") version: String? = ""


    ): Call<JobListModel>


    @GET("jobdetailsscreen.asp")
    fun getJobdetailData(
            @Query("encoded") encoded: String,
            @Query("jobId") jobId: String,
            @Query("ln") ln: String,
            @Query("next") next: String,
            @Query("slno") slno: String,
            @Query("userId") userId: String,
            @Query("version") version: String
    ): Call<JobDetailJsonModel>

    @GET("viewfilters.asp")
    fun getFavouriteSearchFilters(
            @Query("encoded") encoded: String?,
            @Query("user") userID: String?

    ): Call<FavouritSearchFilterModelClass>

    @GET("CompnayListFollowEmployer.asp")
    fun getFollowEmployerList(
            @Query("userID") userID: String?,
            @Query("decodeId") decodeId: String?,
            @Query("Apstyp") Apstyp: String? = "M",
            @Query("encoded") encoded: String?
    ): Call<FollowEmployerListModelClass>


    @GET("storedjobsDetails.asp")
    fun getShortListedJobs(
            @Query("p_id") p_id: String?,
            @Query("encoded") encoded: String?
    ): Call<ShortListedJobModel>


    @GET("FollowerEmployer.asp")
    fun getUnfollowMessage(
            @Query("id") id: String?,
            @Query("name") name: String?,
            @Query("userId") userId: String?,
            @Query("encoded") encoded: String?,
            @Query("actType") actType: String?,
            @Query("decodeId") decodeId: String?

    ): Call<FollowUnfollowModelClass>

    @GET("companyotherjobs.asp")
    fun getEmpJobLists(
            @Query("id") id: String?,
            @Query("alias") alias: String? = "",
            @Query("companyname") companyname: String?= "",
            @Query("jobid") jobid: String?= "",
            @Query("encoded") encoded: String?,
            @Query("packageName") packageName: String?= "",
            @Query("packageNameVersion") packageNameVersion: String?= ""
    ): Call<EmployerJobListsModel>

    @GET("Employerlist.asp")
    fun getEmpLists(
            @Query("version") version: String? = "",
            @Query("orgName") orgName: String?,
            @Query("orgType") orgType: String?= "",
            @Query("orgFirstLetter") orgFirstLetter: String?= "",
            @Query("encoded") encoded: String?,
            @Query("page") page: String?= ""

    ): Call<EmployerListModelClass>

    @GET
    fun downloadDatabaseFile(@Url fileUrl: String): Call<ResponseBody>


    @GET("homescreen.asp")
    fun getLastSearchCount(
            @Query("lastSearchedOn ") lastSearchedOn: String = "",
            @Query("jobLevel") jobLevel: String? = "",
            @Query("Newspaper") Newspaper: String? = "",
            @Query("armyp") armyp: String? = "",
            @Query("bc") bc: String? = "",
            @Query("category") category: String? = "",
            @Query("deadline") deadline: String? = "",
            @Query("encoded") encoded: String? = "",
            @Query("experience") experience: String? = "",
            @Query("gender") gender: String? = "",
            @Query("genderB") genderB: String? = "",
            @Query("industry") industry: String? = "",
            @Query("isFirstRequest") isFirstRequest: String? = "",
            @Query("jobNature") jobNature: String? = "",
            @Query("jobType") jobType: String? = "",
            @Query("keyword") keyword: String? = "",
            @Query("lastJPD") lastJPD: String? = "",
            @Query("location") location: String? = "",
            @Query("org") org: String? = "",
            @Query("pageid") pageid: String? = "",
            @Query("pg") pg: Int? = 1,
            @Query("postedWithin") postedWithin: String? = "",
            @Query("qAge") qAge: String? = "",
            @Query("rpp") rpp: String? = "",
            @Query("slno") slno: String? = "",
            @Query("version") version: String? = ""
    ): Call<LastSearchCountModel>

    @FormUrlEncoded
    @POST("savefilter.asp")
    fun saveOrUpdateFilter(
            @Field("icat ") icat: String = "",
            @Field("fcat") fcat: String? = "",
            @Field("location") location: String? = "",
            @Field("qOT") qOT: String? = "",
            @Field("qJobNature") qJobNature: String? = "",
            @Field("qJobLevel") qJobLevel: String? = "",
            @Field("qPosted") qPosted: String? = "",
            @Field("qDeadline") qDeadline: String? = "",
            @Field("txtsearch") txtsearch: String? = "",
            @Field("qExp") qExp: String? = "",
            @Field("qGender") qGender: String? = "",
            @Field("qGenderB") qGenderB: String? = "",
            @Field("qJobSpecialSkill") qJobSpecialSkill: String? = "",
            @Field("qRetiredArmy") qRetiredArmy: String? = "",
            @Field("savefilterid") savefilterid: String? = "",
            @Field("userId") userId: String? = "",
            @Field("filterName") filterName: String? = "",
            @Field("qAge") qAge: String? = "",
            @Field("newspaper") newspaper: String? = "",
            @Field("encoded") encoded: String? = ""
    ): Call<SaveUpdateFavFilterModel>


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