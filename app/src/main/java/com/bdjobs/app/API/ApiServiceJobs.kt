package com.bdjobs.app.API

import com.bdjobs.app.API.ModelClasses.*
import com.bdjobs.app.BuildConfig
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.Constants.Companion.api_jobs_db_update
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface ApiServiceJobs {
    @GET(api_jobs_db_update)
    fun getDbInfo(@Query("lastUpdateDate") lastUpdateDate: String?="",
                  @Query("appId") appId :String? = Constants.APP_ID
                  ): Call<DatabaseUpdateModel>

//    @GET("joblist.asp")
//    fun getJobList(
//            @Query("jobLevel") jobLevel: String? = "",
//            @Query("Newspaper") Newspaper: String? = "",
//            @Query("armyp") armyp: String? = "",
//            @Query("bc") bc: String? = "",
//            @Query("category") category: String? = "",
//            @Query("deadline") deadline: String? = "",
//            @Query("encoded") encoded: String? = "",
//            @Query("experience") experience: String? = "",
//            @Query("gender") gender: String? = "",
//            @Query("genderB") genderB: String? = "",
//            @Query("industry") industry: String? = "",
//            @Query("isFirstRequest") isFirstRequest: String? = "",
//            @Query("jobNature") jobNature: String? = "",
//            @Query("jobType") jobType: String? = "",
//            @Query("keyword") keyword: String? = "",
//            @Query("lastJPD") lastJPD: String? = "",
//            @Query("location") location: String? = "",
//            @Query("org") org: String? = "",
//            @Query("pageid") pageid: String? = "",
//            @Query("pg") pg: Int? = 1,
//            @Query("postedWithin") postedWithin: String? = "",
//            @Query("qAge") qAge: String? = "",
//            @Query("rpp") rpp: String? = "",
//            @Query("slno") slno: String? = "",
//            @Query("version") version: String? = "",
//            @Query("appId") appId :String? = Constants.APP_ID
//
//
//    ): Call<JobListModel>

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
            @Query("version") version: String? = "",
            @Query("appId") appId :String? = Constants.APP_ID,
            @Query("workplace") workPlace : String? = "",
            @Query("pwd") personWithDisability : String? = "",
            @Query("facilitiesForPWD") facilitiesForPWD : String? = ""


    ): Call<ResponseBody>


    @GET("storedjobsDetailsPagination.asp")
    fun getStoreJobList(
            @Query("p_id") p_id: String? = "",
            @Query("encoded") encoded: String? = "",
            @Query("deadline") deadline: String? = "",
            @Query("page") pg: Int? = 1,
            @Query("rpp") rpp: String? = "",
            @Query("appId") appId :String? = Constants.APP_ID
    ): Call<JobListModel>


    @GET("jobdetailsscreen.asp")
    fun getJobdetailData(
            @Query("encoded") encoded: String?  = "",
            @Query("jobId") jobId: String?  = "",
            @Query("ln") ln: String?  = "",
            @Query("next") next: String? = "",
            @Query("slno") slno: String? = "",
            @Query("userId") userId: String? = "",
            @Query("version") version: String? = "",
            @Query("appId") appId :String? = Constants.APP_ID
    ): Call<JobDetailJsonModel>

    @GET("viewfilters.asp")
//    @GET("viewfilters_pwd.asp")
    fun getFavouriteSearchFilters(
            @Query("encoded") encoded: String? = "",
            @Query("user") userID: String? = "",
            @Query("appId") appId :String? = Constants.APP_ID

    ): Call<FavouritSearchFilterModelClass>

    @GET("CompnayListFollowEmployer.asp")
    fun getFollowEmployerList(
            @Query("userID") userID: String? = "",
            @Query("decodeId") decodeId: String? = "",
            @Query("Apstyp") Apstyp: String? = "J", // M for all list and j for only live job
            @Query("encoded") encoded: String? = "",
            @Query("appId") appId :String? = Constants.APP_ID
    ): Call<FollowEmployerListModelClass>

    @GET("CompanyListFollowEmployerForAll.asp")
    fun getFollowEmployerListLazy(
            @Query("userID") userID: String? = "",
            @Query("decodeId") decodeId: String? = "",
            @Query("Apstyp") Apstyp: String? = "M", // M for all list and j for only live job
            @Query("encoded") encoded: String? = "",
            @Query("isActivityDate") isActivityDate: String? = "0",
            @Query("pg") pg: String? = "1",
            @Query("appId") appId :String? = Constants.APP_ID
    ): Call<FollowEmployerListModelClass>


//    @GET("CompanyListFollowEmployerForAll_smsalert.asp")
//    fun getFollowEmployerListLazy(
//            @Query("userID") userID: String? = "",
//            @Query("decodeId") decodeId: String? = "",
//            @Query("Apstyp") Apstyp: String? = "M", // M for all list and j for only live job
//            @Query("encoded") encoded: String? = "",
//            @Query("isActivityDate") isActivityDate: String? = "0",
//            @Query("pg") pg: String? = "1",
//            @Query("appId") appId :String? = Constants.APP_ID
//    ): Call<FollowEmployerListModelClass>

    @GET("storedjobsDeadlines.asp")  // @GET("storedjobsDetails.asp")
    fun getShortListedJobs(
            @Query("p_id") p_id: String? = "",
            @Query("encoded") encoded: String? = "",
            @Query("appId") appId :String? = Constants.APP_ID
    ): Call<ShortListedJobModel>


    @GET("FollowerEmployer.asp")
    fun getUnfollowMessage(
            @Query("id") id: String? = "",
            @Query("name") name: String? = "",
            @Query("userId") userId: String? = "",
            @Query("encoded") encoded: String? = "",
            @Query("actType") actType: String? = "",
            @Query("decodeId") decodeId: String? = "",
            @Query("appId") appId :String? = Constants.APP_ID
    ): Call<FollowUnfollowModelClass>

    @GET("companyotherjobs.asp")
    fun getEmpJobLists(
            @Query("id") id: String? = "",
            @Query("alias") alias: String? = "",
            @Query("companyname") companyname: String?= "",
            @Query("jobid") jobid: String?= "",
            @Query("encoded") encoded: String?,
            @Query("packageName") packageName: String?= "",
            @Query("packageNameVersion") packageNameVersion: String?= "",
            @Query("appId") appId :String? = Constants.APP_ID
    ): Call<EmployerJobListsModel>

    @GET("Employerlist.asp")
    fun getEmpLists(
            @Query("version") version: String? = "",
            @Query("orgName") orgName: String? = "",
            @Query("orgType") orgType: String?= "",
            @Query("orgFirstLetter") orgFirstLetter: String?= "",
            @Query("encoded") encoded: String? = "",
            @Query("page") page: String?= "",
            @Query("appId") appId :String? = Constants.APP_ID

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
            @Query("version") version: String? = "",
            @Query("appId") appId :String? = Constants.APP_ID,
            @Query("workplace") workPlace: String? = "",
            @Query("pwd") personWithDisability: String? = "",
    ): Call<LastSearchCountModel>

    @FormUrlEncoded
    @POST("savefilter.asp")
    fun saveOrUpdateFilter(
            @Field("icat") icat: String? = "",
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
            @Field("encoded") encoded: String? = "",
            @Field("qPWD") personWithDisability: String? = "",
            @Field("qworkstation") workPlace: String? = "",
            @Query("appId") appId :String? = Constants.APP_ID
    ): Call<SaveUpdateFavFilterModel>


    @GET("store.asp")
    fun insertShortListJob(
            @Query("userID") userID: String? = "",
            @Query("encoded") encoded: String? = "",
            @Query("jobID") jobID: String?="",
            @Query("appId") appId :String? = Constants.APP_ID
    ): Call<ShortlistJobModel>

    @FormUrlEncoded
    @POST("jobapplyscreen.asp")
    fun applyJob(
            @Field("userID") userID: String? = "",
            @Field("decodeID") decodeID: String? = "",
            @Field("jobID") jobID: String? = "",
            @Field("expSalary") expSalary: String? = "",
            @Field("JobSex") JobSex: String? = "",
            @Field("JobPhotograph") JobPhotograph: String? = "",
            @Field("encoded") encoded: String? = "",
            @Query("appId") appId :String? = Constants.APP_ID
    ): Call<ApplyOnlineModel>


    @FormUrlEncoded
    @POST("JobApplyPreScreen.asp")
    fun applyEligibilityCheck(
            @Field("userID") userID: String? = "",
            @Field("decodeID") decodeID: String? = "",
            @Field("jobID") jobID: String? = "",
            @Field("JobSex") JobSex: String? = "",
            @Field("JobPhotograph") JobPhotograph: String? = "",
            @Field("encoded") encoded: String? = "",
            @Query("appId") appId :String? = Constants.APP_ID
    ): Call<ApplyEligibilityModel>


    @FormUrlEncoded
    @POST("view_push_notification.asp")
    fun sendDataForAnalytics(
            @Field("userId") userID: String? = "",
            @Field("decodeID") decodeID: String? = "",
            @Field("unique_Id") uniqueID: String? = "",
            @Field("notification_Type") notificationType: String? = "",
            @Field("encode") encode: String? = "",
            @Field("sent_to") sentTo: String? = ""
    ): Call<String>


    @FormUrlEncoded
    @POST("ResponseBroken.asp")
    fun responseBroken(
            @Field("RURL") url : String? = "",
            @Field("RParameters") params : String? = "",
            @Field("encoded") encoded : String? = "",
            @Field("RUserID") userId : String? = "",
            @Field("RResponse") response : String? = "",
            @Field("appId") appId: String? = ""
    ) : Call<ResponseBody>

//    @FormUrlEncoded
//    @POST("ResponseBroken_TestCase.asp")
//    fun responseBrokenTestCase(
//            @Field("encoded") encoded : String? = "",
//            @Field("param1") param1 : String? = "",
//            @Field("param2") param2 : String? = ""
//
//    ) : Call<TestJsonModel>

    @FormUrlEncoded
    @POST("ResponseBroken_TestCase.asp")
    fun responseBrokenTestCase(
            @Field("encoded") encoded : String? = "",
            @Field("param1") param1 : String? = "",
            @Field("param2") param2 : String? = ""

    ) : Call<ResponseBody>
   /* userId:241028
    decodeId:T8B8Rx
    followId:5077132
    companyId:61065
    action:0
    appId:1*/




    @GET("HOTJOBXMLAutoTemplateNewOnline.asp")
    fun getHotJobs(): Call<HotJobs>


    companion object Factory {

        @Volatile
        private var retrofit: Retrofit? = null


        @Synchronized
        fun create(): ApiServiceJobs {

            retrofit ?: synchronized(this) {
                retrofit = buildRetrofit()
            }

            return retrofit?.create(ApiServiceJobs::class.java)!!
        }


        private fun buildRetrofit():Retrofit {

            val gson = GsonBuilder()
                    .setLenient()
                    .create()

            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            val okHttpClient = OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build()

            return Retrofit.Builder().apply{
                baseUrl(Constants.baseUrlJobs)
                addConverterFactory(GsonConverterFactory.create(gson))
                if (BuildConfig.DEBUG){
                    client(okHttpClient)
                }
            }.build()

        }
    }

}